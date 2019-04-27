# redis 
## redis 常用命令解析
1. redis 中部分特殊规则
- nx结尾的：   不存在是，操作返回错误（0） 例如 setnx,msetnx,
- ex 结尾：    设置并设置超时时间（expire） 例如setex,psetex,
- at 结尾：    设置超时时间，但时间用时间戳表示，例如expireat key xxx
- p 开始：     设置时间为毫秒（默认为秒）例如：psetex,pttl
- range 结尾： 设置操作的下标位置，例如:setrange,getrange
- by:          可设置操作的数值 例如：incrby,decrby,decrbyfloat
- bit:         对位进行操作，例如，bitcount,setbit,getbit
- h :          hash
- s :          集合set
- z :          有序集合

### key

1. **del key[key....]**  
删除给定的一个或多个 key 。                     
不存在的 key 会被忽略。
2. **dump/restore key**

序列化给定 key ，并返回被序列化的值，使用 RESTORE 命令可以将这个值反序列化为 Redis 键。

序列化生成的值有以下几个特点：

- 它带有 64 位的校验和，用于检测错误， RESTORE 在进行反序列化之前会先检查校验和。
值的编码格式和 RDB 文件保持一致。
- RDB 版本会被编码在序列化值当中，如果因为 Redis 的版本不同造成 RDB 格式不兼容，那么 Redis 会拒绝对这个值进行反序列化操作。
- 序列化的值不包括任何生存时间信息。

3. **exists key**
检查给定 key 是否存在。

4. **expire/expireat/pexpire/pexpireat key**

设置超时时间，at 为设置时间戳，p 设置毫秒

5. **migrate host port key destination-db timeout [COPY] [REPLACE]** 

将 key 原子性地从当前实例传送到目标实例的指定数据库上，一旦传送成功， key 保证会出现在目标实例上，而当前实例上的 key 会被删除。

这个命令是一个原子操作，它在执行的时候会阻塞进行迁移的两个实例，直到以下任意结果发生：迁移成功，迁移失败，等到超时。

命令的内部实现是这样的：它在当前实例对给定 key 执行 DUMP 命令 ，将它序列化，然后传送到目标实例，目标实例再使用 RESTORE 对数据进行反序列化，并将反序列化所得的数据添加到数据库中；当前实例就像目标实例的客户端那样，只要看到 RESTORE 命令返回 OK ，它就会调用 DEL 删除自己数据库上的 key 。

timeout 参数以毫秒为格式，指定当前实例和目标实例进行沟通的最大间隔时间。这说明操作并不一定要在 timeout 毫秒内完成，只是说数据传送的时间不能超过这个 timeout 数。

MIGRATE 命令需要在给定的时间规定内完成 IO 操作。如果在传送数据时发生 IO 错误，或者达到了超时时间，那么命令会停止执行，并返回一个特殊的错误： IOERR 。

当 IOERR 出现时，有以下两种可能：

key 可能存在于两个实例
key 可能只存在于当前实例
唯一不可能发生的情况就是丢失 key ，因此，如果一个客户端执行 MIGRATE 命令，并且不幸遇上 IOERR 错误，那么这个客户端唯一要做的就是检查自己数据库上的 key 是否已经被正确地删除。

如果有其他错误发生，那么 MIGRATE 保证 key 只会出现在当前实例中。（当然，目标实例的给定数据库上可能有和 key 同名的键，不过这和 MIGRATE 命令没有关系）。

可选项：

>COPY ：不移除源实例上的 key 。\
REPLACE ：替换目标实例上已存在的 key 。

>MIGRATE 127.0.0.1 7777 greeting 0 1000
6. **move key db**
将当前数据库的 key 移动到给定的数据库 db 当中。

如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。

因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)。
> move a 1

7. **object REFCOUNT /ENCODING /IDLETIME  key** \
OBJECT 命令允许从内部察看给定 key 的 Redis 对象。
8. **PERSIST/PEXPIREAT key**
移除给定 key 的生存时间
9.RENAME/RENAMENX key 重命名
10. **sort** 排序
11. ttl/pttl 查看超时时间
12. type 查看超时类型
13. scan 

## String

1.SET key value 
>设置指定 key 的值
2.GET key 
>获取指定 key 的值。
3. GETRANGE key start end 
>返回 key 中字符串值的子字符
4. GETSET key value
>将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
5. GETBIT key offset
>对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
6. MGET key1 [key2..]
>获取所有(一个或多个)给定 key 的值。
7. SETBIT key offset value
>对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
8. SETEX key seconds value
>将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。
9. SETNX key value
>只有在 key 不存在时设置 key 的值。
10. SETRANGE key offset value
>用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始。
11. STRLEN key
>返回 key 所储存的字符串值的长度。
12. MSET key value [key value ...]
>同时设置一个或多个 key-value 对。
13. MSETNX key value [key value ...] 
>同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
14. PSETEX key milliseconds value
>这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那样，以秒为单位。
15. INCR key
>将 key 中储存的数字值增一。
16. INCRBY key increment
>将 key 所储存的值加上给定的增量值（increment） 。
17. INCRBYFLOAT key increment
>将 key 所储存的值加上给定的浮点增量值（increment） 。
18. DECR key
>将 key 中储存的数字值减一。
19. DECRBY key decrement
>key 所储存的值减去给定的减量值（decrement） 。
20. APPEND key value
>如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾。

## Hash
1. HDEL key field1 [field2] 
>删除一个或多个哈希表字段
2. HEXISTS key field 
>查看哈希表 key 中，指定的字段是否存在。
3. HGET key field 
>获取存储在哈希表中指定字段的值。
4. HGETALL key 
>获取在哈希表中指定 key 的所有字段和值
5. HINCRBY key field increment 
>为哈希表 key 中的指定字段的整数值加上增量 increment 。
6. HINCRBYFLOAT key field increment 
>为哈希表 key 中的指定字段的浮点数值加上增量 increment 。
7. HKEYS key 
>获取所有哈希表中的字段
8. HLEN key 
>获取哈希表中字段的数量
9. HMGET key field1 [field2] 
>获取所有给定字段的值
10. HMSET key field1 value1 [field2 value2 ] 
>同时将多个 field-value (域-值)对设置到哈希表 key 中。
11. HSET key field value 
>将哈希表 key 中的字段 field 的值设为 value 。
12. HSETNX key field value 
>只有在字段 field 不存在时，设置哈希表字段的值。
13. HVALS key 
>获取哈希表中所有值
14. HSCAN key cursor [MATCH pattern] [COUNT count] 
>迭代哈希表中的键值对。

## List
> l 为左，r 为右 \
push 则添加，pop 取出并删除 \
先进为表头，后进为表尾


1. BLPOP key1 [key2 ] timeout 
>移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
2. BRPOP key1 [key2 ] timeout 
>移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
3. BRPOPLPUSH source destination timeout 
>从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
4. LINDEX key index 
>通过索引获取列表中的元素
5. LINSERT key BEFORE|AFTER pivot value 
>在列表的元素前或者后插入元素   linsert list before '1' '0' 执行错误
6. LLEN key 
>获取列表长度
7. LPOP key 
>移出并获取列表的第一个元素
8. LPUSH key value1 [value2] 
>将一个或多个值插入到列表头部
9. LPUSHX key value 
>将一个值插入到已存在的列表头部
10. LRANGE key start stop 
>获取列表指定范围内的元素
11. LREM key count value 
>移除列表元素
count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。\
count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。\
count = 0 : 移除表中所有与 value 相等的值。\
12. LSET key index value 
>通过索引设置列表元素的值
13. LTRIM key start stop 
>对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
情况 1： 常见情况， start 和 stop 都在列表的索引范围之内    （范围删除）\
情况 2： stop 比列表的最大下标还要大 (不在范围内的被删除) \
情况 3： start 和 stop 都比列表的最大下标要大，并且 start < stop （全删除）\
情况 4： start 和 stop 都比列表的最大下标要大，并且 start > stop （全删除）\
14. RPOP key 
>移除列表的最后一个元素，返回值为移除的元素。
15. RPOPLPUSH source destination 
>移除列表的最后一个元素，并将该元素添加到另一个列表并返回
16. RPUSH key value1 [value2] 
>在列表中添加一个或多个值
17. RPUSHX key value 
>为已存在的列表添加值

## Set
1. SADD key member1 [member2] 
>向集合添加一个或多个成员
2. SCARD key 
>获取集合的成员数
3. SDIFF key1 [key2] 
>返回给定所有集合的差集\
对比另一个集合集（key2 key3）中是否含有当前集合
如果只有一个key 那么列出所以值

4. SDIFFSTORE destination key1 [key2] 
>返回给定所有集合的差集并存储在 destination 中
5. SINTER key1 [key2] 
>返回给定所有集合的交集
6. SINTERSTORE destination key1 [key2] 
>返回给定所有集合的交集并存储在 destination 中
7. SISMEMBER key member 
>判断 member 元素是否是集合 key 的成员
8. SMEMBERS key 
>返回集合中的所有成员
9. SMOVE source destination member 
>将 member 元素从 source 集合移动到 destination 集合
10. SPOP key 
>移除并返回集合中的一个随机元素
11. SRANDMEMBER key [count] 
>返回集合中一个或多个随机数
12. SREM key member1 [member2] 
>移除集合中一个或多个成员
13. SUNION key1 [key2] 
>返回所有给定集合的并集
14. SUNIONSTORE destination key1 [key2] 
>所有给定集合的并集存储在 destination 集合中
15. SSCAN key cursor [MATCH pattern] [COUNT count] 
>迭代集合中的元素

## 有序集合(sorted set)

> 排序方式，先按照添加的scores 排序，再按照名称排序，数字，字母

1. ZADD key score1 member1 [score2 member2] 
>向有序集合添加一个或多个成员，或者更新已存在成员的分数
2. ZCARD key 
>获取有序集合的成员数
3. ZCOUNT key min max 
>计算在有序集合中指定区间分数的成员数
4. ZINCRBY key increment member 
>有序集合中对指定成员的分数加上增量 increment
5. ZINTERSTORE destination numkeys key [key ...] 
>计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
6. ZLEXCOUNT key min max 
>在有序集合中计算指定字典区间内成员数量
7. ZRANGE key start stop [WITHSCORES] 
>通过索引区间返回有序集合成指定区间内的成员
8. ZRANGEBYLEX key min max [LIMIT offset count] 
>通过字典区间返回有序集合的成员
9. ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT] 
>通过分数返回有序集合指定区间内的成员
10. ZRANK key member 
>返回有序集合中指定成员的索引
11. ZREM key member [member ...] 
>移除有序集合中的一个或多个成员
12. ZREMRANGEBYLEX key min max 
>移除有序集合中给定的字典区间的所有成员
13. ZREMRANGEBYRANK key start stop 
>移除有序集合中给定的排名区间的所有成员
14. ZREMRANGEBYSCORE key min max 
>移除有序集合中给定的分数区间的所有成员
15. ZREVRANGE key start stop [WITHSCORES] 
>返回有序集中指定区间内的成员，通过索引，分数从高到底
16. ZREVRANGEBYSCORE key max min [WITHSCORES] 
>返回有序集中指定分数区间内的成员，分数从高到低排序
17. ZREVRANK key member 
>返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
18. ZSCORE key member 
>返回有序集中，成员的分数值
19. ZUNIONSTORE destination numkeys key [key ...] 
>计算给定的一个或多个有序集的并集，并存储在新的 key 中
20. ZSCAN key cursor [MATCH pattern] [COUNT count] 
>迭代有序集合中的元素（包括元素成员和元素分值)

## 发布订阅
1. PSUBSCRIBE pattern [pattern ...] 
>订阅一个或多个符合给定模式的频道。
2. PUBSUB subcommand [argument [argument ...]] 
>查看订阅与发布系统状态。
3. PUBLISH channel message 
>将信息发送到指定的频道。
4. PUNSUBSCRIBE [pattern [pattern ...]] 
>退订所有给定模式的频道。
5. SUBSCRIBE channel [channel ...] 
>订阅给定的一个或多个频道的信息。
6. UNSUBSCRIBE [channel [channel ...]] 
>指退订给定的频道。
## 事务
1. DISCARD 
>取消事务，放弃执行事务块内的所有命令。
2. EXEC 
>执行所有事务块内的命令。
3. MULTI 
>标记一个事务块的开始。
4. UNWATCH 
>取消 WATCH 命令对所有 key 的监视。
5. WATCH key [key ...] 
>监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。


## Redis 持久化
## redis的持久化方式RDB和AOF的区别
2、二者的区别
RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘，实际操作过程是fork一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。

AOF持久化以日志的形式记录服务器所处理的每一个写、删除操作，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。

3、二者优缺点
RDB存在哪些优势呢？
1). 一旦采用该方式，那么你的整个Redis数据库将只包含一个文件，这对于文件备份而言是非常完美的。比如，你可能打算每个小时归档一次最近24小时的数据，同时还要每天归档一次最近30天的数据。通过这样的备份策略，一旦系统出现灾难性故障，我们可以非常容易的进行恢复。

2). 对于灾难恢复而言，RDB是非常不错的选择。因为我们可以非常轻松的将一个单独的文件压缩后再转移到其它存储介质上。

3). 性能最大化。对于Redis的服务进程而言，在开始持久化时，它唯一需要做的只是fork出子进程，之后再由子进程完成这些持久化的工作，这样就可以极大的避免服务进程执行IO操作了。

4). 相比于AOF机制，如果数据集很大，RDB的启动效率会更高。

RDB又存在哪些劣势呢？

1). 如果你想保证数据的高可用性，即最大限度的避免数据丢失，那么RDB将不是一个很好的选择。因为系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。

2). 由于RDB是通过fork子进程来协助完成数据持久化工作的，因此，如果当数据集较大时，可能会导致整个服务器停止服务几百毫秒，甚至是1秒钟。

AOF的优势有哪些呢？
1). 该机制可以带来更高的数据安全性，即数据持久性。Redis中提供了3中同步策略，即每秒同步、每修改同步和不同步。事实上，每秒同步也是异步完成的，其效率也是非常高的，所差的是一旦系统出现宕机现象，那么这一秒钟之内修改的数据将会丢失。而每修改同步，我们可以将其视为同步持久化，即每次发生的数据变化都会被立即记录到磁盘中。可以预见，这种方式在效率上是最低的。至于无同步，无需多言，我想大家都能正确的理解它。

2). 由于该机制对日志文件的写入操作采用的是append模式，因此在写入过程中即使出现宕机现象，也不会破坏日志文件中已经存在的内容。然而如果我们本次操作只是写入了一半数据就出现了系统崩溃问题，不用担心，在Redis下一次启动之前，我们可以通过redis-check-aof工具来帮助我们解决数据一致性的问题。

3). 如果日志过大，Redis可以自动启用rewrite机制。即Redis以append模式不断的将修改数据写入到老的磁盘文件中，同时Redis还会创建一个新的文件用于记录此期间有哪些修改命令被执行。因此在进行rewrite切换时可以更好的保证数据安全性。

4). AOF包含一个格式清晰、易于理解的日志文件用于记录所有的修改操作。事实上，我们也可以通过该文件完成数据的重建。

AOF的劣势有哪些呢？

1). 对于相同数量的数据集而言，AOF文件通常要大于RDB文件。RDB 在恢复大数据集时的速度比 AOF 的恢复速度要快。

2). 根据同步策略的不同，AOF在运行效率上往往会慢于RDB。总之，每秒同步策略的效率是比较高的，同步禁用策略的效率和RDB一样高效。

二者选择的标准，就是看系统是愿意牺牲一些性能，换取更高的缓存一致性（aof），还是愿意写操作频繁的时候，不启用备份来换取更高的性能，待手动运行save的时候，再做备份（rdb）。rdb这个就更有些 eventually consistent的意思了。

4、常用配置
RDB持久化配置
Redis会将数据集的快照dump到dump.rdb文件中。此外，我们也可以通过配置文件来修改Redis服务器dump快照的频率，在打开6379.conf文件之后，我们搜索save，可以看到下面的配置信息：

save 900 1              #在900秒(15分钟)之后，如果至少有1个key发生变化，则dump内存快照。

save 300 10            #在300秒(5分钟)之后，如果至少有10个key发生变化，则dump内存快照。

save 60 10000        #在60秒(1分钟)之后，如果至少有10000个key发生变化，则dump内存快照。

AOF持久化配置
在Redis的配置文件中存在三种同步方式，它们分别是：

appendfsync always     #每次有数据修改发生时都会写入AOF文件。

appendfsync everysec  #每秒钟同步一次，该策略为AOF的缺省策略。

appendfsync no          #从不同步。高效但是数据不会被持久化。