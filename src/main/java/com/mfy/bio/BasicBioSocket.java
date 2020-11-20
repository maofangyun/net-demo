package com.mfy.bio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 缺点：阻塞式的，而且只能连接一个client
 * */
public class BasicBioSocket {

    private ServerSocket serverSocket = null;

    public BasicBioSocket(){
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
            Socket client = serverSocket.accept();
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

    public static void main(String[] args) {
        BasicBioSocket basicBioSocket = new BasicBioSocket();
        basicBioSocket.start();
    }
}
