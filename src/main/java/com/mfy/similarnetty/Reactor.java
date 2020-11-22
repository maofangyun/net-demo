package com.mfy.similarnetty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Reactor implements Runnable{

    private Selector selector;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                while(selector.select(200)>0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        Runnable runnable = (Runnable) selectionKey.attachment();
                        if(runnable != null)
                            runnable.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
