package system.random.imp;

import org.apache.log4j.Logger;
import system.common.GetHashCode;
import system.entity.Server;
import system.random.BalanceService;

import java.util.Collections;
import java.util.List;

/**
 * 余数Hash实现类
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public class HashServerImpl implements BalanceService {
    private static final Logger logger = Logger.getLogger(HashServerImpl.class);
    /**
     * 服务器列表
     */
    private final List<Server> serverList;

    public HashServerImpl(List<Server> serverList) {
        this.serverList = Collections.synchronizedList(serverList);
    }

    /**
     * 获取服务器
     * hash直接取余法
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
        server = serverList.get(GetHashCode.getHashCode(requestAddress) % serverList.size());
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
        serverList.removeIf(server1 -> server1.getAddress().equals(server.getAddress()) && server1.getPort().equals(server.getPort()));
    }


}
