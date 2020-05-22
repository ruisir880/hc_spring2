package com.ray.hc_spring2.core;

import com.google.common.base.Splitter;
import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.execptions.MsgReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * nio socket服务端
 */
public class SocketServer {

    private static Logger log = LoggerFactory.getLogger(SocketServer.class);
    private static final String CONTENT_PATTERN = ".+,.+";

    private static Pattern pattern = Pattern.compile(CONTENT_PATTERN,Pattern.DOTALL);
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    private WarnComponent warnComponent  = SpringContextUtils.getApplicationContext().getBean(WarnComponent.class);


    public void startSocketServer(int port){
        try {
            ServerSocketChannel ssc = buildServerSocketChannel(port);
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("socket 准备就绪！");
            while (true) {
                int num = selector.select();//获取通道内是否有选择器的关心事件
                if (num < 1) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();//获取通道内关心事件的集合
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    //遍历每个key
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        newSocket(key,selector);
                    }
                    if (key.isReadable()) {

                        SocketChannel sc = (SocketChannel) key.channel();
                        int count = 0;
                        rBuffer.clear();

                        try {
                            while ((count = sc.read(rBuffer)) > 0) {
                                rBuffer.flip();
                                byte[] content = new byte[rBuffer.limit()];
                                rBuffer.get(content);
                                String result = new String(content, "utf-8");
                                dealMsg(result);
                            }
                            if (count == -1) {
                                System.out.println("connect finish!over!");
                                sc.close();
                                break;
                            }
                        }catch (Exception e){
                            log.warn("光纤系统关闭连接.",e);
                            key.cancel();
                            sc.socket().close();
                            sc.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerSocketChannel buildServerSocketChannel(int port) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);//使通道为非阻塞
        ServerSocket ss = channel.socket();//创建基于NIO通道的socket连接
        ss.bind(new InetSocketAddress(port));//新建socket通道的端口
        //将NIO通道选绑定到择器,当然绑定后分配的主键为skey
        return channel;
    }

    private void newSocket(SelectionKey key,Selector selector) throws IOException {
        // 有新的socket链接进来
        ServerSocketChannel serverChanel = (ServerSocketChannel) key.channel();
        SocketChannel sc = serverChanel.accept();
        sc.configureBlocking(false);
        SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
        System.out.println("Got connection from " + sc);
    }



    private void dealMsg(String msg){
        try {
            log.info("接受到光纤系统的信息：" + msg);
            if (!pattern.matcher(msg).matches()) {
                throw new MsgReadException(String.format("Message %s content is not legal.",msg));
            }
            List<String> stringList = Splitter.on(",").splitToList(msg);
            String defenseArea = stringList.get(3).substring(2);
            log.info("防区："+defenseArea);
            warnComponent.addWarn(defenseArea, Constants.SYSTEM_REMOTE);
        }catch (Exception e){
            log.error("Error occurs when receive msg from other system.",e);
        }

    }

}