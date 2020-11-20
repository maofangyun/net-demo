package com.mfy.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 相比BIO模式，NIO由于是非阻塞式，可以用单个线程处理多个连接，提高了资源利用率
 * */
public class BasicNioSocket {

    private ServerSocketChannel serverSocketChannel = null;

    private static LinkedBlockingQueue<SocketChannel> blockingQueue = new LinkedBlockingQueue();

    public BasicNioSocket(){
        initServer();
    }

    private void initServer() {
        try {
            serverSocketChannel =  ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9090));
            // 设置成非阻塞模式
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(Thread thread){
        thread.start();
        while(true){
            try {
                // 非阻塞的
                SocketChannel client = serverSocketChannel.accept();
                if(client != null){
                    // 服务器端的Socket也设置成非阻塞模式
                    client.configureBlocking(false);
                    System.out.println("客户端:"+client.socket().getPort()+"已连接");
                    blockingQueue.add(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientThread implements Runnable{

        private LinkedBlockingQueue<SocketChannel> clients;

        ClientThread(LinkedBlockingQueue<SocketChannel> sockets){
            this.clients = sockets;
        }

        @Override
        public void run() {
            try {
                System.out.println("数据处理线程"+Thread.currentThread().getName()+"已启动");
                while (true){
                    for(SocketChannel client : clients){
                        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                        // 非阻塞的
                        int read = client.read(byteBuffer);
                        if(read > 0){
                            byte[] bytes = byteBuffer.array();
                            System.out.println(new String(bytes,"utf-8"));
                        } else if(read == 0){
                        } else{
                            client.close();
                            System.out.println("客户端:"+client.socket().getPort()+"断开连接");
                            blockingQueue.remove(client);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BasicNioSocket basicNioSocket = new BasicNioSocket();
        ClientThread clientThread = new ClientThread(blockingQueue);
        Thread thread = new Thread(clientThread);
        basicNioSocket.start(thread);

    }
}
