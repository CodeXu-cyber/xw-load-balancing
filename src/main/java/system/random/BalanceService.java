package system.random;

import system.entity.Server;

public interface BalanceService {
    /**
     * 获取服务器
     * @param requestNumber
     * @param requestAddress
     * @return
     */
    Server getServer(int requestNumber,String requestAddress);
}
