package com.mfy.similarnetty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SlaveReactor extends Reactor{

    public SlaveReactor(Selector selector){
        super(selector);
    }

    static class ReadTask implements Runnable{

        private SelectionKey selectionKey;

        public ReadTask(SelectionKey selectionKey){
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = ByteBuffer.allocate(128);
                int read = socketChannel.read(buffer);
                if(read > 0){
                    byte[] bytes = buffer.array();
                    String msg = new String(bytes, "utf-8");
                    System.out.println("接收来自客户端的数据："+ msg);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                    selectionKey.attach(new WriteTask(selectionKey,msg));
                    selectionKey.selector().wakeup();
                } else if(read == -1){
                    socketChannel.close();
                    selectionKey.cancel();
                    System.out.println("客户端:"+socketChannel.socket().getPort()+"断开连接");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class WriteTask implements Runnable{

        private SelectionKey selectionKey;

        private String msg;

        public WriteTask(SelectionKey selectionKey,String msg){
            this.selectionKey = selectionKey;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                while(buffer.hasRemaining()){
                    socketChannel.write(buffer);
                }
                System.out.println("客户端"+socketChannel.socket().getPort()+"请求"+msg+"已处理");
                selectionKey.interestOps(SelectionKey.OP_READ);
                selectionKey.attach(new SlaveReactor.ReadTask(selectionKey));
                selectionKey.selector().wakeup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
