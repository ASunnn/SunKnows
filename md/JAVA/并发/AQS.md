# AbstractQueuedSynchronizer

队列同步器，是一个用来构建锁和其他同步器组建的基础框架

它使用了一个FIFO队列来完成对请求支援的线程的排队工作，并使用了一个int类型的变量来保存同步状态

*AbstractQueuedSynchronizer*使用了模板方法模式，使用者需要继承并且重写里

## 同步队列

同步队列是一个FIFO的双向链表，这是本体：

```java
    private transient volatile Node head;

    private transient volatile Node tail;

    static final class Node {
        // 共享
        static final Node SHARED = new Node();

        // 独占式
        static final Node EXCLUSIVE = null;

        // 在同步队列等待的线程等待超时or被中断，要从同步队列中取消等待
        // 节点变成此状态后不会再改变，因为已经结束了
        static final int CANCELLED =  1;
        
        // 等待被唤醒状态
        static final int SIGNAL    = -1;
        
        // 调用了Condition.await()之后就会进入此状态，直到调用了singal()之后
        static final int CONDITION = -2;
        
        static final int PROPAGATE = -3;

        // 等待状态
        volatile int waitStatus;

        // 前驱
        volatile Node prev;

        // 后继
        volatile Node next;

        // 线程
        volatile Thread thread;

        Node nextWaiter;
    }
```

![](../PIC/并发-AQS的同步队列.png)

结构如图（出处见水印）

这个队列的头节点即是获得同步状态的节点，当头节点的线程释放同步状态后，会唤醒下一个节点

如果有新的线程获取同步状态，失败的时候最作为一个节点加入到队尾

## 状态 

之前说过，*AbstractQueuedSynchronizer*使用一个*int*来保存同步状态

```java
    private volatile int state;
```

当*state*大于0时，表示同步器被占用

*AbstractQueuedSynchronizer*有三个方法来访问或者修改*state*

```java
    protected final int getState() {
        return state;
    }

    protected final void setState(int newState) {
        state = newState;
    }

    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }
```

## 可重写的方法

*AbstractQueuedSynchronizer*可以重写的方法有5个

| 方法 | 说明 |
| --- | --- | 
| tryAcquire(int arg) | 独占式获取同步状态 |
| tryRelease(int arg) | 独占式释放同步状态 |
| tryAcquireShared(int arg) | 共享式获取同步状态 |
| tryReleaseShared(int arg) | 共享式释放同步状态 |
| isHeldExclusively() | 同步器是否在独占模式下被当前线程占用 |

## 模板方法

| 方法 | 说明 |
| --- | --- | 
| acquire(int arg) | 独占式获取同步状态 |
| acquireInterruptibly(int arg) | 独占式获取同步状态，但是可以响应中断 |
| tryAcquireNanos(int arg, long nanosTimeout) | 在上边那条的基础上增加了超时限制，超时没有获取到同步状态返回false |
| release(int arg) | 独占式释放同步状态 |
| acquireShared(int arg) | 共享式获取同步状态，共享式与独占式区别在于同一时刻可以有多个线程获取同步状态 |
| acquireShared(int arg) | 共享版的acquireInterruptibly |
| tryAcquireSharedNanos(int arg, long nanosTimeout) | 共享版的tryAcquireNanos|
| releaseShared(int arg) | 共享式释放同步状态 |

## 获取同步状态

以*acquire(int arg)*为例一路跟踪下去

```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

首先调用*tryAcquire(arg)*，如果返回true表明已经获取到了

否则会调用*addWaiter(Node.EXCLUSIVE)*构造一个独占式节点加入到同步队列中

```java
    private Node addWaiter(Node mode) {
        // 构造一个节点
        Node node = new Node(Thread.currentThread(), mode);

        Node pred = tail;
        // 尝试快速添加
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {    // 如果添加成功了就能返回了
                pred.next = node;
                return node;
            }
        }
        // 快速添加不成功会调用这个
        enq(node);
        return node;
    }

    private Node enq(final Node node) {
        // 会发现这个方法是一个死循环
        for (;;) {
            Node t = tail;
            if (t == null) { // 尾节点为null代表整个同步队列是空的，需要初始化
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {    // 正常情况
                node.prev = t;
                // 如果节点添加成功了，就能返回，否则就继续循环去吧
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;   // 这里返回前驱节点
                }
            }
        }
    }
```

*enq(final Node node)*通过一个死循环来确保节点能通过CAS添加到队列中

节点进入队列之后就进入了自旋阻塞的过程：

```java
    final boolean acquireQueued(final Node node, int arg) {
        // 默认失败(￣、￣)
        boolean failed = true;
        try {
            boolean interrupted = false;
            // 这里也是一个死循环
            for (;;) {
                // 获取前驱结点
                final Node p = node.predecessor();  
                // 如果前驱节点是头节点的话就尝试去获取同步状态
                if (p == head && tryAcquire(arg)) {
                    // 获取到同步状态之后就能结束自旋了
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                // 如果失败，判断是否要挂起，挂起唤醒之后检查中断状态
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            // 发生跳出来的话一般都是失败，取消获取同步
            if (failed)
                cancelAcquire(node);
        }
    }

    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;

        // 如果前驱节点是SIGNAL状态，就返回true挂起
        if (ws == Node.SIGNAL)
            return true;
        
        // 如果前驱结点状态>0（即CANCEL），移出同步队列，直到上一个不是CANCEL的前驱节点
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {    
            // 将前驱结点的状态改为SIGNAL
            // 这样下次进来的时候如果前驱节点没啥变化，当前节点就能挂起了
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        // 返回false不挂起
        return false;
    }

    private final boolean parkAndCheckInterrupt() {
        // 挂起线程
        LockSupport.park(this);
        // 被唤醒之后检查线程有没有中断
        return Thread.interrupted();
    }
```

每个节点等待过程中基本没有联系，只有当前驱节点是头节点的时候才再次尝试获取同步状态

当获取同步状态失败时，节点会挂起，并且从*shouldParkAfterFailedAcquire*的代码中可以发现，这个行为会传播到整个同步队列，直到头节点唤醒它的后继节点，后继节点才会醒来

当一个线程从*acquireQueued*正常返回后再从*acquire*返回，代表着请求同步状态的线程获取到了锁

## 释放同步状态

这里以以*release(int arg)*为例

```java
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```

当*tryRelease*返回true，代表释放同步状态成功

这时候会调用*unparkSuccessor(Node node)*唤醒挂起的后继节点

（一般这时候后继节点在*parkAndCheckInterrupt()*的第一行代码那挂着

```java
    private void unparkSuccessor(Node node) {
        // 改变节点状态
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        // 获取后继节点
        Node s = node.next;
        // 如果直接后继节点失效的话，从队尾开始，找出最后一个未被取消的节点
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        // 唤醒节点
        if (s != null)
            LockSupport.unpark(s.thread);
    }
```

释放同步状态简单了不少

可以看得出，这个FIFO队列中，节点只和前驱后继交互的性质，直接避免了锁竞争，也避免了资源浪费

## Condition

*Condition*是一个接口：

```java
public interface Condition {

    void await() throws InterruptedException;

    void awaitUninterruptibly();

    long awaitNanos(long nanosTimeout) throws InterruptedException;

    boolean await(long time, TimeUnit unit) throws InterruptedException;

    boolean awaitUntil(Date deadline) throws InterruptedException;

    void signal();

    void signalAll();
}
```

它仅有俩实现类，*AbstractQueuedSynchronizer*的内部类*ConditionObject*是一个，这里要讲的都是以*ConditionObject*为展开的

#### 等待队列

平时有用过这货的都知道，它的*await()*和*singal()*某种程度上比*wait()*、*notify()*好用，它能定向的唤醒某一个线程

*Condition*之所以能做到这个，是因为每个*Condition*类都有一个等待队列，它保存的节点也是和之前同步队列里一样的节点

```java
    /** First node of condition queue. */
    private transient Node firstWaiter;
    /** Last node of condition queue. */
    private transient Node lastWaiter;
```

*Condition*类的俩核心成员变量↑↑↑

```java
    Condition a = lock.newCondition();
    Condition b = lock.newCondition();
    Condition c = lock.newCondition();
```

像平时这样子，仨*Condition*实例是有仨独立的等待队列的

**想要使用Condition，线程必须先获得同步状态**

当调用*await()* 时，节点会释放同步状态，从同步队列的头节点转移到等待队列中，并且节点状态也会变为CONDITION。调用*singal()*之后会从等待队列移出，重新添加到同步队列的尾部开始获取同步状态

#### await

先看看*await()*是怎样的

```java
    public final void await() throws InterruptedException {
        // 先判断线程有没有中断，有丢异常
        if (Thread.interrupted())
            throw new InterruptedException();
        // 创建新节点加入等待队列
        Node node = addConditionWaiter();
        // 释放同步状态
        int savedState = fullyRelease(node);
        int interruptMode = 0;
        // 循环，判断节点是否处于同步状态中，换句话说就是判断线程是否被唤醒
        while (!isOnSyncQueue(node)) {
            // 挂起线程
            LockSupport.park(this);
            // 判断线程是不是被中断唤醒的
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                break;
        }
        // 唤醒之后调用acquireQueued获取同步状态，失败的话顺手判断中断情况
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
            interruptMode = REINTERRUPT;
        // 等待队列还有后继节点的话，清理已经不是CONDITION状态的节点
        if (node.nextWaiter != null) 
            unlinkCancelledWaiters();
        // 处理中断
        if (interruptMode != 0)
            reportInterruptAfterWait(interruptMode);
    }
```

稍微看是怎么把节点从同步队列转到等待队列的

```java
    /**
     * 创建新节点加入等待队列
     */
    private Node addConditionWaiter() {
        Node t = lastWaiter;
        // 如果最后一个节点不是CONDITION状态，unlink
        if (t != null && t.waitStatus != Node.CONDITION) {
            unlinkCancelledWaiters();
            t = lastWaiter;
        }
        /*
            构造、保存新节点
        */
        Node node = new Node(Thread.currentThread(), Node.CONDITION);
        if (t == null)
            firstWaiter = node;
        else
            t.nextWaiter = node;
        lastWaiter = node;
        return node;
    }
    
    /**
     * 释放同步状态
     */
    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            // 到头来又调用了之前说过的release(int arg)
            if (release(savedState)) {
                failed = false;
                return savedState;  // 这里保存state，唤醒之后恢复的时候还要用到
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            // 失败的话除了丢异常，还把节点状态改成CANCELLED
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }
```

代码不难

await大致流程就是这样，首先就是构造好节点，然后在while循环挂着线程，唤醒的时候判断是不是真醒了，之后重新竞争同步状态

#### signal

```java
    public final void signal() {
        if (!isHeldExclusively())
            throw new IllegalMonitorStateException();
        Node first = firstWaiter;
        if (first != null)
            doSignal(first);
    }
```

首先一上来就是判断当前线程有没有占独占锁，没有就丢异常，这里可以看出来**只有独占式才能使用这些**

然后就是唤醒队列头节点

```java
    private void doSignal(Node first) {
        do {
            /*
                移除等待队列的头节点
            */
            if ( (firstWaiter = first.nextWaiter) == null)
                // 如果头节点为整个队列唯一节点的话，lastWaiter变量置null
                lastWaiter = null;  
            first.nextWaiter = null;
        } while (!transferForSignal(first) &&
                 (first = firstWaiter) != null);    
        // 如果没有把first节点放到同步队列，就继续通知下一个节点（如果还有的话）
    }

    final boolean transferForSignal(Node node) {
        // 先把状态从CONDITION换成初始状态
        // 如果没有成功，证明这个节点已经不是CONDITION状态了，返回false
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        // 这里调用enq重新入同步队列，并获得前驱节点
        Node p = enq(node);
        int ws = p.waitStatus;
        // 如果前驱节点已经被取消，或者设置状态为SIGNAL失败，就唤醒node节点
        // 这时候node节点在await()的while循环里挂着
        // 就算这里没有唤醒，最终也会由它的前驱节点，在释放同步状态的时候唤醒
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }
```

*signal()*的大致流程就是这样，它是优先唤醒队头节点的

*signalAll()*的操作也差不多，只不过它是把整个等待队列的节点全部丢到同步队列

#### 其他