package sunnn.knows.test;

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

    Object reference;

    public static void main(String[] args) {

        Test t1 = new Test();
        Test t2 = new Test();

        t1.reference = t2;
        t2.reference = t1;

        t1 = null;
        t2 = null;

    }
}

