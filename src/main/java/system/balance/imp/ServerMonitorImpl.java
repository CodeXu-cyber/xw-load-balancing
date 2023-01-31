package system.balance.imp;

import org.apache.log4j.Logger;
import system.common.ConnectUtil;
import system.entity.Server;
import system.balance.BalanceService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * 装饰器,实现服务监控,动态增减服务
 *
 * @author xuwei
 * @date 2022/07/27 16:58
 **/
public class ServerMonitorImpl implements BalanceService {
    private static final Logger logger = Logger.getLogger(ServerMonitorImpl.class);
    private final BalanceService balanceService;
    /**
     * 连接失败服务器列表
     */
    private final List<Server> failServer = Collections.synchronizedList(new LinkedList<>());
    private final Thread serverMonitor;

    public ServerMonitorImpl(BalanceService balanceService) {
        this.balanceService = balanceService;
        Runnable runnable = () -> {
            logger.info("Server Monitor start!");
            while (true) {
                LockSupport.parkNanos(1000 * 1000 * 1000 * 3L);
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Server Monitor stop!");
                    return;
                }
                //对错误服务列表一直监控
                failServer.removeIf(server -> {
                    if (ConnectUtil.telnet(server.getAddress(), server.getPort(), 200)) {
                        addServerNode(server);
                        return true;
                    }
                    return false;
                });
            }
        };
        this.serverMonitor = new Thread(runnable);
        this.serverMonitor.setName("server-monitor");
        this.serverMonitor.start();
    }

    /**
     * 获取服务器
     *
     * @param requestNumber  请求量
     * @param requestAddress 请求地址
     * @return
     */
    @Override
    public Server getServer(int requestNumber, String requestAddress) {
        Server server;
        while (true) {
            Server server1 = balanceService.getServer(requestNumber, requestAddress);
            if (server1 == null) {
                this.serverMonitor.interrupt();
                return null;
            }
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
        balanceService.addServerNode(server);
    }

    /**
     * 删除服务器节点
     *
     * @param server server
     */
    @Override
    public void delServerNode(Server server) {
        balanceService.delServerNode(server);
    }
}
