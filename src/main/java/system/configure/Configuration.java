package system.configure;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import system.balance.BalanceService;
import system.balance.imp.*;
import system.entity.Server;
import system.redis.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置类
 *
 * @author xuwei
 * @date 2022/07/18 11:30
 **/
public class Configuration {
    private static final Logger logger = Logger.getLogger(Configuration.class);
    private volatile static Configuration configuration;
    private BalanceService balanceService;
    private Integer port;

    private Configuration(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            SAXReader reader = new SAXReader();
            List<Server> serverList = new ArrayList<>();
            Document document = null;
            try {
                document = reader.read(file);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            int vnnNodeCount = ConfigConstants.DEFAULT_VNN_NODE_COUNT;
            boolean openRedis = false;
            String redisPort = ConfigConstants.DEFAULT_STRING;
            String redisHost = ConfigConstants.DEFAULT_STRING;
            String redisPassword = ConfigConstants.DEFAULT_STRING;
            String redisChannel = ConfigConstants.DEFAULT_STRING;
            assert document != null;
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                if (!child.elements().isEmpty()) {
                    for (Element c : child.elements()) {
                        switch (child.getName()) {
                            case ConfigConstants.SERVERS:
                                serverList.add(new Server(c.attributeValue(ConfigConstants.NAME),
                                        c.attributeValue(ConfigConstants.ADDRESS),
                                        StringUtils.hasText(c.attributeValue(ConfigConstants.PORT)) ?
                                                Integer.parseInt(c.attributeValue(ConfigConstants.PORT)) : ConfigConstants.SERVER_DEFAULT_PORT,
                                        StringUtils.hasText(c.attributeValue(ConfigConstants.WEIGHT)) ?
                                                Integer.parseInt(c.attributeValue(ConfigConstants.WEIGHT)) : ConfigConstants.SERVER_DEFAULT_WEIGHT));
                                break;
                            case ConfigConstants.SETTINGS:
                                switch (c.attributeValue(ConfigConstants.NAME)) {
                                    case ConfigConstants.PORT:
                                        this.port = StringUtils.hasText(c.attributeValue(ConfigConstants.VALUE)) ?
                                                Integer.parseInt(c.attributeValue(ConfigConstants.VALUE)) : ConfigConstants.DEFAULT_PORT;
                                        break;
                                    case ConfigConstants.VNN_NODE_COUNT:
                                        vnnNodeCount = StringUtils.hasText(c.attributeValue(ConfigConstants.VALUE)) ?
                                                Integer.parseInt(c.attributeValue(ConfigConstants.VALUE)) : vnnNodeCount;
                                        break;
                                    case ConfigConstants.RANDOM:
                                        String random = StringUtils.hasText(c.attributeValue(ConfigConstants.VALUE)) ?
                                                c.attributeValue(ConfigConstants.VALUE) : ConfigConstants.RANDOM_SERVER;
                                        switch (random) {
                                            case ConfigConstants.WEIGHT_RANDOM_SERVER:
                                                balanceService = new WeightRandomServerImpl(serverList);
                                                break;
                                            case ConfigConstants.POLL_SERVER:
                                                balanceService = new PollServerImpl(serverList);
                                                break;
                                            case ConfigConstants.WEIGHT_POLL_SERVER:
                                                balanceService = new WeightPollServerImpl(serverList);
                                                break;
                                            case ConfigConstants.HASH_SERVER:
                                                balanceService = new HashServerImpl(serverList);
                                                break;
                                            case ConfigConstants.CONSISTENT_HASH:
                                                balanceService = new ConsistentHashServerImpl(serverList, vnnNodeCount);
                                                break;
                                            case ConfigConstants.RANDOM_SERVER:
                                            default:
                                                balanceService = new RandomServerImpl(serverList);
                                                break;
                                        }
                                    case ConfigConstants.OPEN_SERVER_MONITOR:
                                        if (ConfigConstants.TRUE.equals(c.attributeValue(ConfigConstants.VALUE))) {
                                            balanceService = new ServerMonitorImpl(balanceService);
                                        }
                                        break;
                                    case ConfigConstants.OPEN_REDIS:
                                        if (ConfigConstants.TRUE.equals(c.attributeValue(ConfigConstants.VALUE))) {
                                            openRedis = true;
                                        }
                                        break;
                                    case ConfigConstants.REDIS_HOST:
                                        redisHost = c.attributeValue(ConfigConstants.VALUE);
                                        break;
                                    case ConfigConstants.REDIS_PORT:
                                        redisPort = c.attributeValue(ConfigConstants.VALUE);
                                        break;
                                    case ConfigConstants.REDIS_PASSWORD:
                                        redisPassword = c.attributeValue(ConfigConstants.VALUE);
                                        break;
                                    case ConfigConstants.REDIS_CHANNEL:
                                        redisChannel = StringUtils.hasText(c.attributeValue(ConfigConstants.VALUE)) ?
                                                ConfigConstants.REDIS_DEFAULT_CHANNEL : c.attributeValue(ConfigConstants.VALUE);
                                    default:
                                        break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (openRedis) {
                if (StringUtils.hasText(redisPort) && StringUtils.hasText(redisHost)) {
                    try (Jedis jedis = new Jedis(redisHost, Integer.parseInt(redisPort));) {
                        if (StringUtils.hasText(redisPassword)) {
                            jedis.auth(redisPassword);
                        }
                        Subscriber subscriber = new Subscriber(balanceService, redisChannel);
                        String finalRedisChannel = redisChannel;
                        Runnable runnable = () -> {
                            logger.info("Redis Monitor start!");
                            jedis.subscribe(subscriber, finalRedisChannel);
                        };
                        Thread thread = new Thread(runnable);
                        thread.setName("redis-monitor");
                        thread.start();
                    } catch (Exception e) {
                        logger.warn(e);
                    }
                }
            }
        }
    }

    public static Configuration getConfiguration(String fileName) {
        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = new Configuration(fileName);
                }
            }
        }
        return configuration;
    }

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public Integer getPort() {
        return port;
    }

}
