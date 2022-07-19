package system.random.imp;

import org.apache.log4j.Logger;
import system.common.ConnectUtil;
import system.entity.Server;
import system.random.BalanceService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 完全随机实现类
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public class RandomServerImpl implements BalanceService {

    /**
     * 服务器列表
     */
    private final List<Server> serverList;
    /**
     * 连接失败服务器列表
     */
    private final List<Server> failServer = Collections.synchronizedList(new LinkedList<>());

    /**
     * 伪随机数生成器
     */
    private final Random random = new Random();

    private static final Logger logger = Logger.getLogger(RandomServerImpl.class);

    public RandomServerImpl(List<Server> serverList) {
        this.serverList = Collections.synchronizedList(serverList);
        Runnable runnable = () -> {
            logger.info("Server Monitor start!");
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (serverList.isEmpty()){
                    logger.info("Server Monitor stop!");
                    return;
                }
                //对错误服务列表一直监控
                for (Server server : failServer) {
                    boolean isConnected = ConnectUtil.telnet(server.getAddress(), server.getPort(), 200);
                    if (isConnected) {
                        failServer.removeIf(server1 -> server1.getAddress().equals(server.getAddress()));
                        addServerNode(server);
                    }
                }
            }
        };
        Thread serverMonitor = new Thread(runnable);
        serverMonitor.setName("server-monitor");
        serverMonitor.start();
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
        while (true) {
            Server server1 = serverList.get(random.nextInt(serverList.size()));
            // 测试连接
            boolean isConnected = ConnectUtil.telnet(server1.getAddress(), server1.getPort(), 200);
            if (isConnected) {
                server = server1;
                break;
            } else {
                //失败则加入到失效服务器列表并删除此节点
                failServer.add(server1);
                delServerNode(server1);
            }
        }
        return server;
    }

    /**
     * 添加服务器节点
     *
     * @param server server
     */
    @Override
    public void addServerNode(Server server) {
        serverList.add(server);
    }

    /**
     * 删除服务器节点
     *
     * @param server server
     */
    @Override
    public void delServerNode(Server server) {
        serverList.removeIf(server1 -> server1.getAddress().equals(server.getAddress())&&server1.getPort().equals(server.getPort()));
    }


}
