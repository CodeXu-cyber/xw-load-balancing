# xw-load-balancing

基于Socket的简单的负载均衡服务

- 项目结构

```
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   ├── Main.java   启动类，ServerSocket绑定监听端口接收请求
    │   │   └── system
    │   │       ├── common
    │   │       │   ├── ConnectUtil.java    连接工具类，可测试是否与指定IP端口连接成功
    │   │       │   └── GetHashCode.java   重计算Hash值工具类
    │   │       ├── configure
    │   │       │   ├── ConfigConstants.java  配置常量类
    │   │       │   └── Configuration.java   配置类，解析xml配置文件并封装为配置类
    │   │       ├── entity
    │   │       │   └── Server.java   服务器类，包含serverName，ip，port，wight属性
    │   │       ├── redis
    │   │       │   ├── JedisConstants.java   redis常量类
    │   │       │   └── Subscriber.java   redis订阅实现类
    │   │       ├── random
    │   │       │   ├── BalanceService.java   负载均衡接口，包含获取server，增加server，删除server方法
    │   │       │   └── imp
    │   │       │       ├── ConsistentHashServerImpl.java  一致性Hash负载均衡实现类
    │   │       │       ├── HashServerImpl.java   余数Hash负载均衡实现类
    │   │       │       ├── PollServerImpl.java  完全轮询负载均衡实现类
    │   │       │       ├── RandomServerImpl.java   完全随机负载均衡实现类
    │   │       │       ├── ServerMonitorImpl.java   服务监视器，装饰者模式，为其他实现类增加服务监控、动态增减服务器功能
    │   │       │       ├── WeightPollServerImpl.java   加权轮询负载均衡实现类
    │   │       │       └── WeightRandomServerImpl.java   加权随机负载均衡实现类
    │   │       └── socket
    │   │           └── SocketThread.java   客户端Socket请求线程，每个客户端请求对应一个线程对象，提交到线程池
    │   └── resources
    │       ├── log4j.properties   日志配置文件
    │       └── xw-load-balancing.xml   项目配置文件
    └── test
        └── java
```

- 可配置六种负载均衡方式
    - 完全随机算法
    - 加权随机算法
    - 完全轮询算法
    - 加权轮询算法
    - 余数Hash算法
    - 简单的一致性Hash算法
- 可配置开启Redis订阅服务,通过发布订阅来上线或下线服务节点
- 可配置服务监控线程监控服务状态,自动下线不健康节点,并后台不断重试,如果节点恢复健康则重新上线
- xml配置文件如下定义
```

<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <servers>
        <!--服务节点,name-服务名称,address-服务地址,port-服务端口,weight-服务权重,默认权重1-->
        <server name="sever1" address="127.0.0.1" port="3306" weight="1"/>
        <server name="sever2" address="127.0.0.1" port="3306" weight="2"/>
        <server name="sever3" address="127.0.0.1" port="3306" weight="2"/>
        <server name="sever4" address="127.0.0.1" port="3306" weight="1"/>
    </servers>
    <settings>
        <!--虚拟节点数量,默认1-->
        <setting name="vnnNodeCount" value="3"/>
        <!--六种负载均衡方式可选,默认RandomServer-->
        <!--RandomServer-完全随机算法-->
        <!--WeightRandomServer-加权随机算法-->
        <!--PollServer-完全轮询算法-->
        <!--WeightPollServer-加权轮询算法-->
        <!--HashServer-余数Hash-->
        <!--ConsistentHash-一致性Hash-->
        <setting name="random" value="RandomServer"/>
        <!--是否打开服务监视器实现服务动态增减-->
        <setting name="openServerMonitor" value="true"/>
        <!--监听端口,默认8088-->
        <setting name="port" value="8088"/>
        <!--是否打开redis-->
        <setting name="openRedis" value="true"/>
        <!--redis地址-->
        <setting name="redisHost" value="localhost"/>
        <!--redis端口-->
        <setting name="redisPort" value="6379"/>
        <!--redis密码-->
        <setting name="redisPassword" value=""/>
        <!--redis订阅频道,默认频道balance-->
        <setting name="redisChannel" value="balance"/>
    </settings>
</configuration>
```
