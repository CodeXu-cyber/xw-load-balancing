package system.balance;

import system.entity.Server;

/**
 * 负载均衡接口
 *
 * @author xuwei
 * @date 2022/07/18 10:41
 **/
public interface BalanceService {

    /**
     * 得到服务器
     *
     * @param requestNumber  请求数
     * @param requestAddress 请求地址
     * @return {@link Server}
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
