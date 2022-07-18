package system.random.imp;

import system.entity.Server;
import system.random.BalanceService;

import java.util.List;
import java.util.Random;

/**
 * @ClassName RandomServer
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class RandomServerImpl implements BalanceService {
    private final List<Server> serverList;
    private final Random random = new Random();

    public RandomServerImpl(List<Server> serverList) {
        this.serverList = serverList;
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
        return serverList.get(random.nextInt(serverList.size()));
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
