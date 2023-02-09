package system.redis;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPubSub;
import system.balance.BalanceService;
import system.entity.Server;

/**
 * @author XuWei
 * @since 2023/02/03
 **/
public class Subscriber extends JedisPubSub {
    private static final Logger logger = Logger.getLogger(Subscriber.class);
    private final BalanceService balanceService;
    private final String channel;

    public Subscriber(BalanceService balanceService, String channel) {
        this.balanceService = balanceService;
        this.channel = channel;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(this.channel)) {
            if (StringUtils.hasText(message)) {
                String[] command = message.split(" ");
                Server server = new Server(command[1], command[2], Integer.parseInt(command[3]), Integer.parseInt(command[4]));
                if (JedisConstants.ADD_SERVER.equals(command[0])) {
                    logger.info("Received the Redis remote command:[" + message + "]");
                    balanceService.addServerNode(server);
                    logger.info("Add server success! -> serverName is [" + server.getServerName() + "]");
                } else if (JedisConstants.REMOVE_SERVER.equals(command[0])) {
                    logger.info("Received the Redis remote command:[" + message + "]");
                    balanceService.delServerNode(server);
                    logger.info("Remove server success! -> serverName is [" + server.getServerName() + "]");
                } else {
                    logger.warn("Redis Command is not available! [" + message + "]");
                }
            }
        }
    }
}
