package system.configure;

/**
 * 常量类
 *
 * @author XuWei
 * @since 2023/02/08
 **/
public class ConfigConstants {
    /**
     * 节点属性name
     */
    public static final String NAME = "name";
    /**
     * 节点属性value
     */
    public static final String VALUE = "value";
    /**
     * 节点属性address
     */
    public static final String ADDRESS = "address";
    /**
     * 节点属性port
     */
    public static final String PORT = "port";
    /**
     * 节点属性weight
     */
    public static final String WEIGHT = "weight";
    /**
     * 节点属性vnnNodeCount
     */
    public static final String VNN_NODE_COUNT = "vnnNodeCount";
    /**
     * 节点属性random
     */
    public static final String RANDOM = "random";
    /**
     * random值,加权随机算法
     */
    public static final String WEIGHT_RANDOM_SERVER = "WeightRandomServer";
    /**
     * random值,完全轮询算法
     */
    public static final String POLL_SERVER = "PollServer";
    /**
     * random值,加权轮询算法
     */
    public static final String WEIGHT_POLL_SERVER = "WeightPollServer";
    /**
     * random值,余数Hash算法
     */
    public static final String HASH_SERVER = "HashServer";
    /**
     * random值,一致性Hash算法
     */
    public static final String CONSISTENT_HASH = "ConsistentHash";
    /**
     * random值,完全随机算法
     */
    public static final String RANDOM_SERVER = "RandomServer";
    /**
     * 节点命名空间settings
     */
    public static final String SETTINGS = "settings";
    /**
     * 节点命名空间servers
     */
    public static final String SERVERS = "servers";
    /**
     * 节点属性openServerMonitor
     */
    public static final String OPEN_SERVER_MONITOR = "openServerMonitor";
    /**
     * 节点属性openRedis
     */
    public static final String OPEN_REDIS = "openRedis";
    /**
     * 节点属性redisHost
     */
    public static final String REDIS_HOST = "redisHost";
    /**
     * 节点属性redisPort
     */
    public static final String REDIS_PORT = "redisPort";
    /**
     * 节点属性redisPassword
     */
    public static final String REDIS_PASSWORD = "redisPassword";
    /**
     * 节点属性redisChannel
     */
    public static final String REDIS_CHANNEL = "redisChannel";
    /**
     * redis默认订阅频道
     */
    public static final String REDIS_DEFAULT_CHANNEL = "balance";
    public static final String TRUE = "true";
    public static final String DEFAULT_STRING = "";
    /**
     * 默认虚拟节点数量
     */
    public static final Integer DEFAULT_VNN_NODE_COUNT = 3;
    /**
     * server默认权重
     */
    public static final Integer SERVER_DEFAULT_WEIGHT = 0;
    /**
     * server默认端口
     */
    public static final Integer SERVER_DEFAULT_PORT = 80;
    /**
     * 默认端口
     */
    public static final Integer DEFAULT_PORT = 8088;
}
