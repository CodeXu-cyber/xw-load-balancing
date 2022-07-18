package system.random.imp;

import org.apache.log4j.Logger;
import system.common.ConnectUtil;
import system.common.GetHashCode;
import system.entity.Server;
import system.random.BalanceService;

import java.util.*;

/**
 * 一致性Hash实现类
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public class ConsistentHashServerImpl implements BalanceService {
    /**
     * 虚拟节点数
     */
    private final Integer vnnNodeCount;
    /**
     * 一致性hash环
     */
    private final TreeMap<Integer, Server> treeMapHash;

    /**
     * 连接失败服务器列表
     */
    private final List<Server> failServer = Collections.synchronizedList(new LinkedList<>());

    private static final Logger logger = Logger.getLogger(ConsistentHashServerImpl.class);


    public ConsistentHashServerImpl(List<Server> serverList, Integer vnnNodeCount) {
        this.vnnNodeCount = vnnNodeCount;
        TreeMap<Integer, Server> treeMapHash = new TreeMap<>();
        for (Server server : serverList) {
            int hash = GetHashCode.getHashCode(server.getAddress());
            treeMapHash.put(hash, server);
            for (int i = 1; i <= this.vnnNodeCount; i++) {
                treeMapHash.put(GetHashCode.getHashCode(server.getAddress() + "&&" + i), server);
            }
        }
        this.treeMapHash = treeMapHash;
        Runnable runnable = () -> {
            logger.info("Server Monitor start!");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        new Thread(runnable).start();
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
            int hash = GetHashCode.getHashCode(requestAddress);
            // 向右寻找第一个 key
            Map.Entry<Integer, Server> subEntry = treeMapHash.ceilingEntry(hash);
            // 设置成一个环，如果超过尾部，则取第一个点
            subEntry = subEntry == null ? treeMapHash.firstEntry() : subEntry;
            Server server1 = subEntry.getValue();
            // 测试连接
            boolean isConnected = ConnectUtil.telnet(server1.getAddress(), server1.getPort(), 200);
            if (isConnected) {
                server = server1;
                break;
            } else {
                //失败则加入到失效服务器列表并删除此节点
                logger.info("server " + server1.getAddress() + " connect fail!");
                failServer.add(server1);
                delServerNode(server1.getAddress());
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
        int hash = GetHashCode.getHashCode(server.getAddress());
        treeMapHash.put(hash, server);
        for (int i = 1; i <= vnnNodeCount; i++) {
            int vnnNodeHash = GetHashCode.getHashCode(server.getAddress() + "&&" + i);
            treeMapHash.put(vnnNodeHash, server);
        }
    }

    /**
     * 删除服务器节点
     *
     * @param serverAddress serverAddress
     */
    @Override
    public void delServerNode(String serverAddress) {
        int hash = GetHashCode.getHashCode(serverAddress);
        treeMapHash.remove(hash);
        for (int i = 1; i <= vnnNodeCount; i++) {
            int vnnNodeHash = GetHashCode.getHashCode(serverAddress + "&&" + i);
            treeMapHash.remove(vnnNodeHash);
        }

    }


}
