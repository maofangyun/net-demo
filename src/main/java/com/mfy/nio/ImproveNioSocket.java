package com.mfy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * IO多路复用模式，selector，当有accept事件或者read事件到达时，通过selector可以直接获取到是哪些client的事件
 * 比BasicNioSocket更高效，不用每次都遍历全部的client
 * */
public class ImproveNioSocket {

    private ServerSocketChannel serverSocketChannel= null;

    private Selector selector = null;

    public ImproveNioSocket(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9090));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            while (true){
                while(selector.select(200)>0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        if(selectionKey.isAcceptable()){
                            handleAccept(selectionKey);
                        } else if(selectionKey.isReadable()){
                            handleRead(selectionKey);
                        }
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey selectionKey) {
        try {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(128);
            socketChannel.read(buffer);
            System.out.println("接收来自客户端的数据：" + new String(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey selectionKey) {
        try {
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            System.out.println("客户端:"+client.socket().getPort()+"已连接");
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ImproveNioSocket improveNioSocket = new ImproveNioSocket();
        improveNioSocket.start();
    }
}
