

# 技术栈

- 数据库：Mysql
- 缓存 ：Redis、Caffeine
- 网关：Gateway
- 消息队列：RocketMQ
- 注册配置：Nacos
- 基础框架
    - SpringBoot
    - Dubbo
    - MybatisPlus
    - ShardingJDBC
    - Netty
- 容器：Docker

# 环境搭建
尝试在远程建服务器上搭建，但是由于购买的服务器性能有限，经常断连，因此下面展示的是本地环境搭建，不含远程服务器的操作。

## 1. Docker安装

参考官网：https://docs.docker.com/get-started/

对于dockers的Windows安装需要将WSL升级到WSL2，即可通过docker desktop启动。在cmd中执行
```shell
wsl --update
```

## 2. MySQL 安装
参考官网：https://dev.mysql.com/doc/

查找镜像

```shell
docker search mysql
```

拉取镜像
```shell
docker pull mysql:latest
```
查看镜像
```shell
docker images
```
运行容器
```shell
docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=[your password] mysql
```
查看运行状态
```shell
docker ps
```

## 3. Redis 安装
参考官网：https://redis.io/docs/getting-started/installation/

测试发现执行run命令docker会自动检测有无image，因此执行下面命令即可
```shell
docker run --restart=always -p 6379:6379 --name myredis -d redis:7.0.12  --requirepass [your password]
```
进入容器
```shell
docker exec -it [容器名 | 容器ID] bash
```

## 4. RocketMq 安装
查找镜像,一般选取Star多的

```shell
docker search mysql
```
拉取镜像
```shell
docker pull rocketmqinc/rocketmq
```
启动namesrv服务

- Linux下：
  ```shell
  docker run -d -p 9876:9876 -v /tmp/data/rocketmq/namesrv/logs:/root/logs -v /tmp/data/rocketmq/namesrv/store:/root/store --name rmqnamesrv -e "MAX_POSSIBLE_HEAP=100000000" rocketmqinc/rocketmq sh mqnamesrv
  ```
- windows可以将文件直接挂载到本地目录,windows下：
  ```shell
  docker run -d -p 9876:9876 -v D:/docker/mq/other/tmp/data/namesrv/logs:/root/logs -v D:/docker/mq/other/tmp/data/namesrv/store:/root/store --name rmqnamesrv -e "MAX_POSSIBLE_HEAP=100000000" rocketmqinc/rocketmq sh mqnamesrv
  ```
启动broker

在windows中的shell换行是`^`,linux的是 `\ `,但是测试了一下windows没Linux兼容的好，故下面都用`\ `.
```shell
docker run -d -p 10911:10911 -p 10909:10909 -v \
D:/docker/mq/other/tmp/data/broker/logs:/root/logs -v \
D:/docker/mq/other/tmp/data/broker/store:/root/store -v \
D:/docker/mq/other/tmp/conf/broker.conf:/opt/rocketmq/conf/broker.conf \
--name rmqbroker --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" \
-e "MAX_POSSIBLE_HEAP=200000000" \
rocketmqinc/rocketmq sh mqbroker -c /opt/rocketmq/conf/broker.conf
```

创建配置文件broker.conf
```yml
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
# 如果是本地程序调用云主机 mq，这个需要设置成 云主机 IP
# 如果Docker环境需要设置成宿主机IP
brokerIP1 = {docker宿主机IP}
```
本地可以查看宿主机IP,最下面找到`IPAddress`,将IP填入上面的宿主IP，我的是 172.17.0.4
```shell
docker inspect 容器的id/容器名
```
安装RocketMqConsole

```shell
docker pull styletang/rocketmq-console-ng
```

启动控制台

${namesrvIp} 这里是namesrv的ip，也可以 `docker inspect xxx`命令查看修改即可

```shell
docker run -e "JAVA_OPTS=-Drocketmq.config.namesrvAddr=${namesrvIp}:9876 \
-Drocketmq.config.isVIPChannel=false" -p  9999:8080 -t --name rmConsole \
styletang/rocketmq-console-ng
```
此时可查看控制台 http://localhost:9999/#/

可能的问题：
1. 报错This date have't data!  通过更改时间区解决，如未解决自行搜索
   ```shell
   docker run --name mq -d -e \
   "JAVA_OPTS=-Drocketmq.namesrv.addr=[宿主IP]:9876 \
   -Dcom.rocketmq.sendMessageWithVIPChannel=false -Duser.timezone='Asia/Shanghai'" \
   -v /etc/localtime:/etc/localtime -p 9999:8080 -t styletang/rocketmq-console-ng
    ```

## 5. Nacos 安装

参照官网：https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html

需要先下载到本地，如果是源码还需要编译，可以直接下编译后的版本

Linux/Unix/Mac
启动命令(standalone代表着单机模式运行，非集群模式):
```shell
sh startup.sh -m standalone
```
如果您使用的是ubuntu系统，或者运行脚本报错提示[[符号找不到，可尝试如下运行：
```shell
bash startup.sh -m standalone
```

Windows
启动命令(standalone代表着单机模式运行，非集群模式):
```shell
startup.cmd -m standalone
```

控制台：http://localhost:8848/nacos














