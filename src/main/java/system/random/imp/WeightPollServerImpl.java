package system.random.imp;

import system.entity.Server;
import system.random.BalanceService;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WeightPollServer
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class WeightPollServerImpl implements BalanceService {
    private final List<Server> serverList;

    public WeightPollServerImpl(List<Server> serverList) {
        List<Server> servers = new ArrayList<>();
        for (Server server : serverList) {
            for (int i = 0; i < server.getWeight(); i++) {
                servers.add(server);
            }
        }
        this.serverList = servers;
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
        return serverList.get(requestNumber % serverList.size());
    }

    /**
     * 添加服务器节点
     *
     * @param serverNodeName nodeName
     */
    @Override
    public void addServerNode(String serverNodeName) {

    }

    /**
     * 删除服务器节点
     *
     * @param serverNodeName nodeName
     */
    @Override
    public void delServerNode(String serverNodeName) {

    }
}
