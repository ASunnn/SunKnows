# SELECT

## SELECT

检索单个字段

```sql
SELECT c_a FROM t_x
```

检索多个字段

```sql
SELECT c_a, c_b, c_c FROM t_x
```

检索表的全部字段

```sql
SELECT * FROM t_x
```

## DISTINCT

一个字段不同的数据

```sql
SELECT DISTINCT c_a FROM t_x
```

## LIMIT

限制返回5条记录

```sql
SELECT * FROM t_x LIMIT 5
```

限制返回第10条记录开始的5条记录

```sql
SELECT * FROM t_x LIMIT 9, 5
```

## ORDER BY

根据一个记录排序顺序

```sql
SELECT * FROM t_x ORDER BY c_a
```

#### DESC

升序排序

```sql
SELECT * FROM t_x ORDER BY c_a DESC
```

按多个记录排序

```sql
SELECT * FROM t_x ORDER BY c_a, c_b DESC
```

## WHERE

#### WHERE子句操作符

| 操作符 | 说明 |
| --- | --- | 
| = | 等于 |
| <> | 不等于 |
| != |  |
| < | 小于 |
| <= | 小于等于 |
| > | 大于 |
| >= | 大于等于 |
| BETWEEN | 介于指定的两个值之间（>=a && <=b） |

指定单个列的过滤

```sql
SELECT * FROM t_x WHERE c_a = 1
```

获取c_a为空的列

```sql
SELECT * FROM t_x WHERE c_a IS NULL
```

## AND & OR

#### AND

用AND指定多个列的过滤

```sql
SELECT * FROM t_x WHERE c_a = 1 AND c_b = 'ABC'
```

#### OR

用OR指定多个列的过滤

```sql
SELECT * FROM t_x WHERE c_a = 1 OR c_b = 'ABC'
```

#### 组合使用

```sql
SELECT * FROM t_x 
WHERE (c_a = 1 OR c_b = 'ABC') AND c_c = 2
```

> AND和OR组合使用时，AND的优先级更高

## IN

指定一个记录的合法值

```sql
SELECT * FROM t_x WHERE c_a IN (1, 2, 3)
```

这句SQL等同于

```sql
SELECT * FROM t_x 
WHERE c_a = 1 OR c_a = 2 OR c_a = 3
```

## NOT

NOT否定之后的任何条件

查找c_a小于1或大于3的列

```sql
SELECT * FROM t_x WHERE c_a NOT BETWEEN 1 AND 3
```

## LIKE

#### %

%表示任何字符出现任意次数

```sql
SELECT c_a FROM t_x WHERE c_a LIKE '%ABC%'
```

查找包含ABC的c_a

```sql
SELECT c_a FROM t_x WHERE c_a LIKE 'A%C'
```

查找以A打头，以C结尾的c_a

#### _

_匹配单个字符，除此之外和%一样

## AS

用于起别名

```sql
SELECT c_a AS a, c_b AS b 
FROM t_x 
WHERE c_a = 'ABC'
```

output:

```
| a   | b |
-----------
| ABC | 1 | 
| ABC | 2 |
| ABC | 3 |
```

#### 执行算数计算

```sql
SELECT c_a AS a, c_b AS b, c_a * c_b AS c
FROM t_x 
WHERE c_a = '1'
```

output:

```
| a | b | c |
-------------
| 1 | 1 | 1 |
| 1 | 2 | 2 |
| 1 | 3 | 3 |
```

## 函数

#### Concat()

能将多个字段/值拼接到一起形成单个值

```sql
SELECT Concat(c_a, '[', c_b, ']') AS o
FROM t_x 
WHERE c_a = 'ABC' AND c_b = 0
```

output:

```
| o      |
----------
| ABC[0] |
```

## 聚集函数

#### AVG()

计算某一个字段的平均值

获取c_a的平均值：

```sql
SELECT AVG(c_a) FROM t_x 
```

#### COUNT()

计数

获取t_x中c_a大于1的记录数量

```sql
SELECT COUNT(*) AS count FROM t_x WHERE c_a > 1 
```

> 如果在参数中指定字段名，则指定字段为空的记录不会被COUNT()计算在内，除非使用COUNT(*)

#### MAX() & MIN()

返回指定记录中的最大/小值

#### SUM()

返回指定字段值的和

假设原来有的数据：

```sql
SELECT c_a FROM t_x
```

output:

```
| c_a |
-------
| 1   |
| 2   |
| 2   |
| 3   |
| 3   |
| 3   |
```

获取c_a不同值的总和：

```sql
SELECT SUM(DISTINCT c_a) AS s FROM t_x
```

output:

```
| s |
-----
| 6 |
```

## GROUP BY

## JOIN（联结）

<!-- > 设表t_x：c_a、c_b、c_c，表t_y：c_a、c_bb、c_cc，表t_z：c_bb、c_ccc

一个简单的多表查询：

```sql
SELECT c_b, c_c, c_bb, c_cc 
FROM t_x, t_y 
WHERE t_x.c_a = t_y.c_a
```

#### INNER JOIN

刚才的例子就是一个内部联结，或者叫等值联结

可以这样写：

```sql
SELECT c_b, c_c, c_bb, c_cc 
FROM t_x INNER JOIN t_y 
ON t_x.c_a = t_y.c_a
``` -->

# INSERT

## 插入一条记录

```sql
INSERT INTO t_x 
VALUES(1, 'ABC', 2, 'DEF', 3)
```

也可以这样写

```sql
INSERT INTO t_x (
    c_a, c_b, c_c, c_d, c_e)
VALUES(1, 'ABC', 2, 'DEF', 3)
```

这种写法繁琐些，但是可以自定义字段顺序

## 插入多条记录

```sql
INSERT INTO t_x (
    c_a, c_b, c_c, c_d, c_e)
VALUES
(1, 'ABC', 2, 'DEF', 3),
(4, 'GHI', 5, 'JKL', 6),
(7, 'MNO', 8, 'PQR', 9)
```

## 插入查询出的数据

```sql
INSERT INTO t_x (
    c_a, c_b, c_c, c_d, c_e)
SELECT c_a, c_b, c_c, c_d, c_e FROM t_y
```

也可以这样写：

```sql
INSERT INTO t_x (
    c_a, c_b, c_c, c_d, c_e)
SELECT * FROM t_y
```

MySQL只是简单的把查询出的第一个字段给c_a，第二个字段给c_b……这样而已

# UPDATE

## 更新一个字段

```sql
UPDATE t_x 
SET c_a = 'abc' 
WHERE c_a = 'ABC'
```

## 更新多个字段

```sql
UPDATE t_x 
SET c_a = 'abc', c_b = 0, c_c = 'def'
WHERE c_a = 'ABC'
```

WHERE子句可以不要

# DELETE

## 删除特定行

```sql
DELETE FROM t_x WHERE c_a > 0
```

## 删除全部

```sql
DELETE FROM t_x
```

或者

```sql
TRUNCATE TABLE t_x
```

> TRUNCATE TABLE实际上是删除原来的表并重新创建一个，不是像DELETE那样一个个删

# CREATE

用来创建表

```sql
CREATE TABLE t_x (
    `c_a` int NOT NULL AUTO_INCREMENT,
    `c_b` char(255) NULL DEFAULT 'ABC',
    `c_c` varchar(255) BINARY NULL,
    `c_d` date NOT NULL,
    `c_e` int NOT NULL,
    PRIMARY KEY (c_a),
    FOREIGN KEY (c_e) REFERENCES t_y(c_a) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE = InnoDB;
```

群众演员表t_y的结构：

```sql
CREATE TABLE t_y (
    `c_a` int NOT NULL,
    PRIMARY KEY (c_a)
);
```

CREATE的大致操作都在这了

# ALTER

对表结构的修改

## 添加

```sql
ALTER TABLE t_x ADD COLUMN c_g varchar(255) NOT NULL;
```

## 修改

```sql
ALTER TABLE t_x 
MODIFY COLUMN c_c int UNSIGNED NULL DEFAULT 114514;
```

## 删除

```sql
ALTER TABLE t_x DROP COLUMN c_e;
```

# RENAME

重命名一个表

```sql
RENAME TABLE t_x TO t_xyz;
```

# DROP

删除表

比如把t_x删除：

```sql
DROP TABLE t_x;
```

# 用户管理

## 用户操作

#### 创建用户

```sql
CREATE USER sunnn IDENTIFIED BY '19960923';
```

另外一种：

```sql
CREATE USER sunnn@localhost IDENTIFIED BY '19960923';
```

这个语句指定了sunnn只能在本地登录。使用通配符%表示可以从任何主机登录（这个是默认策略

#### 删除用户

```sql
DROP USER sunnn;
```

## 权限操作

正常情况下可以这样查看权限：

```sql
SHOW GRANTS FOR sunnn;
```

#### 设置权限

基本格式是：

```
GRANT xxx ON x.x TO xxx
```

设置sunnn在服务器范围所有权限

```sql
GRANT ALL ON *.* TO sunnn;
```

设置sunnn在服务器范围所有权限，并且可以赋予其他用户自己所有的权限

```sql
GRANT ALL ON *.* TO sunnn WITH GRANT OPTION;
```

设置sunnn在test数据库有UPDATE、DELETE权限

```sql
GRANT UPDATE, DELETE ON test.* TO sunnn;
```

设置sunnn在test数据库的t_x表有SELECT权限

```sql
GRANT UPDATE, DELETE ON test.* TO sunnn;
```

#### 撤销权限

基本上和设置权限相反，基本格式是：

```
REVOKE xxx ON x.x FROM xxx
```

撤销sunnn在服务器范围所有权限

```sql
REVOKE ALL ON *.* FROM sunnn;
```