# JAVA集合-List

Queue表示了一个FIFO的队列结构

Queue的元素是有序、可重复的

Queue接口是一个单向队列，它的子类Deque接口则是一个双向队列

常用的实现类有PriorityQueue和ArrayDeque（还有LinkedList，List接口部分已经有了

---

## PriorityQueue

PriorityQueue即优先队列，队列根据元素的权值大小保持了队中元素的顺序，并且保证出队的是权值最小的元素

优先队列，通常又叫做堆，因此PriorityQueue是一个小顶堆

堆一般使用数组保存它的完全二叉树，因此PriorityQueue的本体是一个Object数组

（强行分析.jpg↑↑↑

```java
    transient Object[] queue;
```

#### 初始化

PriorityQueue的构造方法乍一看很多，其实你叼我我叼你最后只有两个是有用的：
```java
    /**
     * 这个方法是通过给定初始化容量和Comparator构造一个空的优先队列
     */
    public PriorityQueue(int initialCapacity,
                         Comparator<? super E> comparator) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
    }

    /* ———————————————————————————————————————— */

    /**
     * 也可以直接提供一个Collection进行初始化
     * 分为三种情况
     */
    public PriorityQueue(Collection<? extends E> c) {
        if (c instanceof SortedSet<?>) {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            initElementsFromCollection(ss);
        }
        else if (c instanceof PriorityQueue<?>) {
            PriorityQueue<? extends E> pq = (PriorityQueue<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityQueue(pq);
        }
        else {
            this.comparator = null;
            initFromCollection(c);
        }
    }

    /**
     * 使用给定的PriorityQueue来初始化PriorityQueue
     */
    private void initFromPriorityQueue(PriorityQueue<? extends E> c) {
        if (c.getClass() == PriorityQueue.class) {
            this.queue = c.toArray();
            this.size = c.size();
        } else {
            initFromCollection(c);
        }
    }
    
    /**
     * 使用给定的Collection来初始化PriorityQueue
     */
    private void initFromCollection(Collection<? extends E> c) {
        initElementsFromCollection(c);
        // 这里重新建一个小顶堆
        heapify();
    }

    /**
     * 使用给定的Collection来初始化PriorityQueue里 面 的 元 素
     */
    private void initElementsFromCollection(Collection<? extends E> c) {
        Object[] a = c.toArray();
        // If c.toArray incorrectly doesn't return Object[], copy it.
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf(a, a.length, Object[].class);
        int len = a.length;
        if (len == 1 || this.comparator != null)
            for (int i = 0; i < len; i++)
                if (a[i] == null)
                    throw new NullPointerException();
        this.queue = a;
        this.size = a.length;
    }
```

#### 操作
PriorityQueue的操作和正常的队列差不多，该有的方法都有，使用起来没什么两样，只不过它会自动维护小顶堆，取到的永远是最小的元素而已

![](../PIC/集合-PriorityQueue的public方法.png)

#### 堆

PriorityQueue是一个小顶堆，当有增删操作的时候必须要对这个堆进行维护

上面的操作方法中，均会调用*siftUp(int k, E x)*、*siftDown(int k, E x)*：

* 往PriorityQueue增加元素时，最把新进元素追加到数组最后（即叶子节点），这时候调用*siftUp(int k, E x)*
* 从PriorityQueue删除元素时，会把数组最后一个元素填充到空出来的位置，这时调用*siftDown(int k, E x)*

这俩方法就是建堆或者对堆的一个维护

```java
    /**
     * 从k指定的位置开始，将x逐层与当前点的parent进行比较并交换，
     * 直到满足x >= queue[parent]为止
     */
    private void siftUp(int k, E x) {
        if (comparator != null)
            siftUpUsingComparator(k, x);
        else
            siftUpComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    /**
     * 从k指定的位置开始，将x逐层向下与当前点的左右孩子中较小的那个交换，
     * 直到x小于或等于左右孩子中的任何一个为止
     */
    private void siftDown(int k, E x) {
        if (comparator != null)
            siftDownUsingComparator(k, x);
        else
            siftDownComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>)x;
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1; // assume left child is least
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
                c = queue[child = right];
            if (key.compareTo((E) c) <= 0)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                comparator.compare((E) c, (E) queue[right]) > 0)
                c = queue[child = right];
            if (comparator.compare(x, (E) c) <= 0)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }
```
看得出这些操作都是看着熟悉的堆操作

---

## Deque

---

## ArrayDeque
