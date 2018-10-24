package sunnn.knows.test;

import sunnn.knows.algorithm.Sort;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * 数据结构
 * 计算机网络
 * JAVA
 *      集合
 *          List
 *          Queue
 *          Set
 *          Map
 *          线程安全
 *      并发
 *          多线程
 *          线程池
 *          锁
 *          CAS/原子类
 *          AQS
 *          volatile
 *          阻塞队列
 *          *底层原理
 *      网络编程
 *      其他
 * JVM
 *      内存区域
 *      GC
 *      类加载机制
 *      Java内存模型
 * 设计模式
 * Web
 *      Spring
 *      Servlet
 *      Tomcat
 *      HTTP
 * 数据库
 *      MySQL
 *          事务
 *          索引
 *          引擎
 *      Mongo
 */
public class Test {

    private static final Object o1 = new Object();

    private static final Object o2 = new Object();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        TaskOne t1 = new TaskOne();
        TaskTwo t2 = new TaskTwo();
        pool.submit(t1);
        pool.submit(t2);
    }

    static class TaskOne implements Runnable {
        @Override
        public void run() {
            synchronized (o1) {
                sleepDown();

                doSomething();
            }
        }

        private void doSomething() {
            synchronized (o2) {
                System.out.println("Hello World");
            }
        }
    }

    static class TaskTwo implements Runnable {
        @Override
        public void run() {
            synchronized (o2) {
                sleepDown();

                doSomething();
            }
        }

        private void doSomething() {
            synchronized (o1) {
                System.out.println("Hello World");
            }
        }
    }

    private static void sleepDown() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

