package com.mfy.bio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 相比BasicBioSocket优点：支持多个client的连接
 * 缺点：由于每个client都占用一个线程，导致系统资源的浪费，可连接数不高
 * */
public class ImproveBioSocket {

    private ServerSocket serverSocket = null;

    public ImproveBioSocket(){
        initServer();
    }

    private void initServer() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(9090));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while(true){
                Socket client = serverSocket.accept();
                System.out.println("客户端:"+client.getPort()+"已连接");
                ClientThread clientThread = new ClientThread(client);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable{

        private Socket client;

        ClientThread(Socket socket){
            this.client = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("线程"+Thread.currentThread().getName()+"已启动");
                InputStream inputStream = client.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    String msg = buffer.toString();
                    System.out.println(msg);
                    buffer.reset();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ImproveBioSocket improveBioSocket = new ImproveBioSocket();
        improveBioSocket.start();
    }
}
