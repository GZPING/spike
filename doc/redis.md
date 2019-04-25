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
>返回给定所有集合的差集
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