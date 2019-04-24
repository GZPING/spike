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

