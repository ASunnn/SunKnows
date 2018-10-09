# 原子类

原子类是JUC的*atomic*包里提供的一组类，它能在保证性能的前提下对这个变量的操作是线程安全的

原子类一共有四种：基本类型类、数组类、引用类、字段类

原子类都是使用CAS实现原子操作，换一种方式说，原子类都是使用Unsafe实现的

## 原子更新基本类型类

这里包含了3类：

AtomicInteger

AtomicLong

AtomicBoolean

基本类型类提供了对基本类型变量的包装操作

因为都差不多，拿已经被讲烂的*AtomicInteger*作为例子

![](../PIC/并发-AtomicInteger的类结构图.png)

*AtomicInteger*的本体

```java
    private volatile int value;
```

可以看到使用了*volatile*来保证可见性

首先是get和set：

```java
    public final int get() {
        return value;
    }

    public final void set(int newValue) {
        value = newValue;
    }
```

这里并没有使用CAS，而是直接获取和更新，接下来

```java
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
```

这里的操作都是使用了CAS，也可以看到*AtomicInteger*直接调用了*Unsafe*里的方法，正如前面所说，原子类基本都是使用*Unsafe*实现CAS操作

#### 其他类型

一个问题，基本类型的原子类只有*Integer*、*Long*、*Boolean*三种，为什么呢？

仔细看下*AtomicInteger*里面的代码，会发现所有的CAS操作调用到最后都会调用*compareAndSwapInt*方法

再看下*Unsafe*的代码：

```java
    public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);

    public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);

    public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);
```

会发现*Unsafe*只提供这仨方法，也就是支持对*Integer*、*Long*、*Object*的操作

去看一下*AtomicLong*，CAS操作最后调用的都是*compareAndSwapLong*方法，这也符合了预期

*AtomicBoolean*呢？

```java
    public final boolean compareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }
```

*AtomicBoolean*是先把*boolean*转换为*int*，然后调用的*compareAndSwapInt*方法实现CAS

所以Java没有提供其他类型的原子类也情有可原，不过个人觉得*char*也可以用类似思路是先原子类


# ThreadLocal

# 阻塞队列