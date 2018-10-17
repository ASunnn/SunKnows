# Redis

Redis是一个使用C开发的开源的NoSQL数据库。它的数据结构都是基于键值对（key-value），并且数据都是在内存中，也可以持久化到磁盘。Redis对数据的操作都是原子性的

# 数据类型

## String

String类型是Redis中最基本的数据类型

它是二进制安全的（即你放flac都行

最大大小：512MB

#### demo

key - "Hello World"

## Lists

列表类型。存储一堆的String，这些String按照插入的顺序排序，而且可以前后插

随机访问的时间复杂度是O(n)

最大长度：2^32 – 1

#### demo

key - {"Hello", "World"}

## Set

一堆不重复的String的无序集合

怎么访问时间复杂度都是O(1)

最大长度：2^32 – 1

#### demo

key - {"World", "Hello"}

## Sorted Set

一堆不重复的String的有序集合

Sorted Set中的每个成员都关联一个Score，Score用于排序的，按照Score的值从小到大进行排序

集合中每个元素是唯一的，但Score可以重复

怎么访问时间复杂度都是O(1)

最大长度：2^32 – 1

#### demo

key - {1 : "World", 2 : "Hello"}

# Hash

哈希类型。可以看成value域就是一个HashMap（实际并不是

最大长度：2^32 – 1

#### demo

key - {"first" : "World", "second" : "Hello"}
