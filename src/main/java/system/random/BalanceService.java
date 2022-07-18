package system.random;

import system.entity.Server;

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
     * @param serverNodeName nodeName
     */
    void addServerNode(String serverNodeName);

    /**
     * 删除服务器节点
     *
     * @param serverNodeName nodeName
     */
    void delServerNode(String serverNodeName);
}
