package system.random;

import system.entity.Server;

/**
 * 负载均衡接口
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public interface BalanceService {
    /**
     * 获取服务器
     *
     * @param requestNumber  请求量
     * @param requestAddress 请求地址
     * @return
     */
    Server getServer(int requestNumber, String requestAddress);

    /**
     * 添加服务器节点
     *
     * @param server server
     */
    void addServerNode(Server server);

    /**
     * 删除服务器节点
     *
     * @param server server
     */
    void delServerNode(Server server);
}
