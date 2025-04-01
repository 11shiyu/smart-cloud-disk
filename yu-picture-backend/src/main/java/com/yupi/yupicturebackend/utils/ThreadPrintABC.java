package com.yupi.yupicturebackend.utils;

public class ThreadPrintABC {
    private static int flag = 0; //0表示A 1表示B 2表示C
    private static final Object lock = new Object();

    static class ThreadA implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    while (flag % 3 != 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print("A");
                    flag++;
                    lock.notifyAll(); //唤醒其他线程
                }
            }
        }
    }

    static class ThreadB implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    while (flag % 3 != 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print("B");
                    flag++;
                    lock.notifyAll(); //唤醒其他线程
                }
            }
        }
    }

    static class ThreadC implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    while (flag % 3 != 2) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print("C");
                    flag++;
                    lock.notifyAll(); //唤醒其他线程
                }
            }
        }
    }
    public static void main(String[] args) {
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
        new Thread(new ThreadC()).start();


    }
}
