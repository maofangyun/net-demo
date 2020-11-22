package com.mfy.similarnetty;

/**
 * 类似netty的io线程模型
 * */
public class SimilarServer {
    public static void main(String[] args) {
        MasterReactor masterReactor = new MasterReactor();
        new Thread(masterReactor).start();
    }
}
