package com.ray.hc_spring2.core;

import groovy.grape.GrapeIvy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class SockTest {
    private static int LISTEN_PORT = 10010;

    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    public static void main(String[] args) {
        SockTest sockTest = new SockTest();

        sockTest.startSocketServer(10010);
    }


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
                        // 有请求进来
                        SocketChannel sc = (SocketChannel) key.channel();
                        int bytesEchoed = 0;
                        rBuffer.clear();

                        while ((bytesEchoed = sc.read(rBuffer)) > 0) {
                            rBuffer.flip();
                            byte[] content = new byte[rBuffer.limit()];
                            rBuffer.get(content);
                            String result = new String(content, "utf-8");
                            System.out.println("bytesEchoed:" + result);
                        }
                        if (bytesEchoed == -1) {
                            System.out.println("connect finish!over!");
                            sc.close();
                            break;
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

}
