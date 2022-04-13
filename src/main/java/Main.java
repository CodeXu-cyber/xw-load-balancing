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
            ServerSocket serverSocket = new ServerSocket(CONFIGURATION.getPort());
            logger.info("The service runs successfully on port " + CONFIGURATION.getPort());
            // 一直监听，接收到新连接，则开启新线程去处理
            while (true) {
                Socket localSocket = serverSocket.accept();
                requestNumber = requestNumber == Integer.MAX_VALUE ? 0 : ++requestNumber;
                Server server = balanceService.getServer(requestNumber, localSocket.getInetAddress().getHostAddress());
                //5分钟内无数据传输、关闭链接
                localSocket.setSoTimeout(SO_TIME_OUT);
                logger.info(localSocket.getRemoteSocketAddress().toString().replace("/","") + "  connected");
                new SocketThread(localSocket, server.getAddress(), server.getPort()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
