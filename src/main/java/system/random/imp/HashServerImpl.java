package system.random.imp;

import system.entity.Server;
import system.random.BalanceService;

import java.util.List;

/**
 * @ClassName HashServer
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class HashServerImpl implements BalanceService {
    private final List<Server> serverList;

    public HashServerImpl(List<Server> serverList) {
        this.serverList = serverList;
    }

    /**
     * 获取服务器
     * hash直接取余法,利用绝对值,后续会改善
     * @param requestNumber
     * @param requestAddress
     * @return
     */
    @Override
    public Server getServer(int requestNumber, String requestAddress) {
        return serverList.get(Math.abs(requestAddress.hashCode()%serverList.size()));
    }
}
