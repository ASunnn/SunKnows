package sunnn.knows.test;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
 *          锁
 *          CAS
 *          volatile
 *          阻塞队列
 *          *底层原理
 *      虚拟机
 *      网络编程
 *      其他
 * 设计模式
 * Web
 *      Spring
 *      Servlet
 *      Tomcat
 *      HTTP
 * 数据库
 *      MySQL
 *      Mongo
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread t = new Thread(new R());
        t.start();
        t.join();

        System.out.println("Work Complete");
    }

    static class R implements Runnable {
        @Override
        public void run() {
            System.out.println("Work Start");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

