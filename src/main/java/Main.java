import org.apache.log4j.Logger;
import system.configure.Configuration;
import system.entity.Server;
import system.random.BalanceService;
import system.socket.SocketThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName Main
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Main {
    private static final Configuration CONFIGURATION = Configuration.getConfiguration("src/main/resources/xw-load-balancing.xml");
    private static final Logger logger = Logger.getLogger(Main.class);
    public static final int SO_TIME_OUT = 300000;
    private static int requestNumber = 0;
    public static void main(String[] args) {
        BalanceService balanceService = CONFIGURATION.getBalanceService();
        try {
            //启动ServerSocket监听配置文件中的端口
            ServerSocket serverSocket = new ServerSocket(CONFIGURATION.getPort());
            logger.info("The service runs successfully on port " + CONFIGURATION.getPort());
            // 一直监听，接收到新连接，则开启新线程去处理
            while (true) {
                Socket localSocket = serverSocket.accept();
                //判断请求次数是否将要溢出
                requestNumber = requestNumber == Integer.MAX_VALUE ? 0 : ++requestNumber;
                //根据负载均衡算法获取转发服务器
                Server server = balanceService.getServer(requestNumber, localSocket.getInetAddress().getHostAddress());
                //5分钟内无数据传输、关闭链接
                localSocket.setSoTimeout(SO_TIME_OUT);
                logger.info(localSocket.getRemoteSocketAddress().toString().replace("/","") + "  connected");
                //启动线程处理本连接
                new SocketThread(localSocket, server.getAddress(), server.getPort()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
