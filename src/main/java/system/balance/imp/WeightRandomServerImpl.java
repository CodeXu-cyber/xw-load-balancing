package system.balance.imp;

import org.apache.log4j.Logger;
import system.entity.Server;
import system.balance.BalanceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 加权随机实现类
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public class WeightRandomServerImpl implements BalanceService {
    private static final Logger logger = Logger.getLogger(WeightRandomServerImpl.class);
    /**
     * 服务器列表
     */
    private final List<Server> serverList;
    /**
     * 伪随机数生成器
     */
    private final Random random = new Random();

    public WeightRandomServerImpl(List<Server> serverList) {
        List<Server> servers = new ArrayList<>();
        for (Server server : serverList) {
            for (int i = 0; i < server.getWeight(); i++) {
                servers.add(server);
            }
        }
        this.serverList = Collections.synchronizedList(servers);
    }

    /**
     * 获取服务器
     *
     * @param requestNumber
     * @param requestAddress
     * @return
     */
    @Override
    public Server getServer(int requestNumber, String requestAddress) {
        Server server;
        if (serverList.isEmpty()) {
            logger.warn("Don not have server available!");
            return null;
        }
        server = serverList.get(random.nextInt(serverList.size()));
        return server;
    }

    /**
     * 添加服务器节点
     *
     * @param server server
     */
    @Override
    public void addServerNode(Server server) {
        for (int i = 0; i < server.getWeight(); i++) {
            serverList.add(server);
        }
    }

    /**
     * 删除服务器节点
     *
     * @param server server
     */
    @Override
    public void delServerNode(Server server) {
        serverList.removeIf(server1 -> server1.getAddress().equals(server.getAddress()) && server1.getPort().equals(server.getPort()));
    }


}
