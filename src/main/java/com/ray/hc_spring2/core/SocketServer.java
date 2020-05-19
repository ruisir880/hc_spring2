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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * nio socket服务端
 */
public class SocketServer {

    private static Logger log = LoggerFactory.getLogger(SocketServer.class);
    private static final String CONTENT_PATTERN = ".+,.+,.+,.+";

    private static Pattern pattern = Pattern.compile(CONTENT_PATTERN,Pattern.DOTALL);

    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
    //接受数据缓冲区
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    //发送数据缓冲区
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    //选择器（叫监听器更准确些吧应该）
    private static Selector selector;

    private WarnComponent warnComponent  = SpringContextUtils.getApplicationContext().getBean(WarnComponent.class);

    /**
     * 启动socket服务，开启监听
     *
     * @param port
     * @throws IOException
     */
    public void startSocketServer(int port) {
        try {
            //打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            //绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
            //打开监听器
            selector = Selector.open();
            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                selector.select();//select方法会一直阻塞直到有相关事件发生或超时
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//监听到的事件
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                selectionKeys.clear();//清除处理过的事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 处理不同的事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handle(SelectionKey selectionKey) {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        String requestMsg = "";
        int count = 0;
        try {

            if (selectionKey.isAcceptable()) {
                //每有客户端连接，即注册通信信道为可读
                serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            } else if (selectionKey.isReadable()) {
                dealMsg(socketChannel,selectionKey);
            }
        }catch (Exception e){
            log.error("Error occurs in SocketServer",e);
        }finally {

        }
    }

    private void dealMsg(SocketChannel socketChannel ,SelectionKey selectionKey) throws MsgReadException {
        int count =0;
        String requestMsg = "";

        try {
            socketChannel = (SocketChannel) selectionKey.channel();
            rBuffer.clear();
            count = socketChannel.read(rBuffer);
            //读取数据
            if (count > 0) {
                rBuffer.flip();
                requestMsg = String.valueOf(cs.decode(rBuffer).array());
            }
            log.info("接受到光纤系统的信息："+requestMsg);

            if(!pattern.matcher(requestMsg).matches()){
                throw new MsgReadException(String.format("Message %s content is not legal.",requestMsg));
            }
            List<String> stringList = Splitter.on(",").splitToList(requestMsg);
            String defenseArea = stringList.get(3).substring(2);
            warnComponent.addWarn(defenseArea, Constants.SYSTEM_REMOTE);
        }catch (Exception e){
            log.error("error occurs when deal msg from other system.",e);
        }finally {
            if(socketChannel != null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    log.warn("error occurs when close socket.",e);
                }
            }
        }

    }

}