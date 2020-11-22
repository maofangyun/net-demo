package com.mfy.similarnetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class MasterReactor extends Reactor{

    private ServerSocketChannel serverSocketChannel;

    public MasterReactor(){
        try {
            setSelector(Selector.open());
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9090));
            serverSocketChannel.configureBlocking(false);
            SelectionKey selectionKey = serverSocketChannel.register(getSelector(), SelectionKey.OP_ACCEPT);
            selectionKey.attach(new Acceptor(serverSocketChannel));
            selectionKey.selector().wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
