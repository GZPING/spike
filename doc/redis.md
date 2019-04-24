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

### 