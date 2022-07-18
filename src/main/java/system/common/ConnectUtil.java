package system.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 连接测试工具类
 *
 * @author xuwei
 * @date 2022/07/18 15:45
 **/
public class ConnectUtil {
    private static final Logger logger = Logger.getLogger(ConnectUtil.class);

    /**
     * 测试telnet 机器端口的连通性
     *
     * @param hostname 地址
     * @param port     端口
     * @param timeout  超时时间
     * @return 是否连通
     */
    public static boolean telnet(String hostname, int port, int timeout) {
        Socket socket = new Socket();
        boolean isConnected = false;
        try {
            socket.connect(new InetSocketAddress(hostname, port), timeout);
            isConnected = socket.isConnected();
        } catch (IOException ignored) {
            logger.info(hostname + ":" + port + " connect failed!");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
                isConnected = false;
                logger.info(hostname + ":" + port + " connect failed!");
            }
        }
        return isConnected;
    }
}
