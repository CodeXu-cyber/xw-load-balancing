# xw-load-balancing

基于Socket的简单的负载均衡服务

- 可配置四种负载均衡方式
    - 完全随机算法
    - 加权随机算法
    - 完全轮询算法
    - 加权轮询算法
    - 简单的一致性hash算法
- 模块对应:
    - system
        - configure
            - Configuration
                - 配置类
        - entity
            - Server
                - 封装被代理服务器
        - random
            - BalanceService
                - 根据负载均衡接口获取被代理服务器
            - imp
                - HashServerImpl
                    - 一致性哈希
                - RandomServerImpl
                    - 随机算法
                - WeightRandomServerImpl
                    - 加权随机算法
                - PollServerImpl
                    - 轮询算法
                - WeightPollServerImpl
                    - 加权轮询算法
        - socket
            - SocketThread
                - Socket线程类
    - utils
        - XmlUtil
            - 解析xml工具类
            

- xml配置文件如下定义
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <servers>
        <server name="sever1" address="127.0.0.1" port="3306" weight="1"/>
        <server name="sever2" address="127.0.0.1" port="3306" weight="2"/>
        <server name="sever3" address="127.0.0.1" port="3306" weight="2"/>
        <server name="sever4" address="127.0.0.1" port="3306" weight="1"/>
    </servers>
    <settings>
        <!--六种负载均衡方式可选-->
        <!--RandomServer-完全随机算法-->
        <!--WeightRandomServer-加权随机算法-->
        <!--PollServer-完全轮询算法-->
        <!--WeightPollServer-加权轮询算法-->
        <!--SmoothWeightPollServer-平滑加权轮询算法-->
        <!--HashServer-哈希负载算法-->
        <setting name="random" value="HashServer"/>
        <!--监听端口-->
        <setting name="port" value="8088"/>
    </settings>
</configuration>
```