# xw-load-balancing

基于Socket的简单的负载均衡服务

- 可配置五种负载均衡方式
    - 完全随机算法
    - 加权随机算法
    - 完全轮询算法
    - 加权轮询算法
    - 余数Hash算法
    - 简单的一致性Hash算法
- xml配置文件如下定义
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <servers>
        <server name="sever1" address="127.0.0.1" port="8083" weight="1"/>
        <server name="sever2" address="127.0.0.1" port="8082" weight="2"/>
        <server name="sever3" address="127.0.0.1" port="8081" weight="2"/>
        <server name="sever4" address="127.0.0.1" port="8080" weight="1"/>
    </servers>
    <settings>
        <!--虚拟节点数量-->
        <setting name="vnnNodeCount" value="3"/>
        <!--六种负载均衡方式可选,默认RandomServer-->
        <!--RandomServer-完全随机算法-->
        <!--WeightRandomServer-加权随机算法-->
        <!--PollServer-完全轮询算法-->
        <!--WeightPollServer-加权轮询算法-->
        <!--HashServer-余数Hash-->
        <!--ConsistentHash-一致性Hash-->
        <setting name="random" value="ConsistentHash"/>
        <!--监听端口,默认8088-->
        <setting name="port" value="8088"/>
    </settings>
</configuration>
```
