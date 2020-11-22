package com.mfy.similarnetty;

import lombok.Data;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Acceptor implements Runnable{

    private ServerSocketChannel serverSocketChannel;

    private int slaveNum;

    private AtomicInteger id = new AtomicInteger(0);

    private Selector[] slaveSelectors;

    private SlaveReactor[] slaveReactors;

    private Thread[] slaveThreads;

    public Acceptor(ServerSocketChannel serverSocketChannel){
        try {
            this.serverSocketChannel = serverSocketChannel;
            initSlave();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initSlave() throws IOException {
        this.slaveNum = Runtime.getRuntime().availableProcessors()/2;
        slaveSelectors = new Selector[slaveNum];
        slaveReactors = new SlaveReactor[slaveNum];
        slaveThreads = new Thread[slaveNum];
        for(int i=0;i<slaveNum;i++){
            slaveSelectors[i] = Selector.open();
            slaveReactors[i] = new SlaveReactor(slaveSelectors[i]);
            slaveThreads[i] = new Thread(slaveReactors[i],"subreactor-"+i);
            slaveThreads[i].start();
        }
    }

    @Override
    public void run() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if(client != null){
                System.out.println("客户端:"+client.socket().getPort()+"已连接");
                client.configureBlocking(false);
                int i = id.get();
                SelectionKey selectionKey = client.register(slaveSelectors[i], SelectionKey.OP_READ);
                selectionKey.attach(new SlaveReactor.ReadTask(selectionKey));
                if(id.incrementAndGet() == slaveNum) {
                    id.set(0);
                }
                selectionKey.selector().wakeup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
