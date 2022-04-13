import system.configure.Configuration;
import system.entity.Server;
import system.random.BalanceService;
import system.socket.SocketThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static system.socket.SocketThread.log;

/**
 * @ClassName Main
 * @Author xuwei
 * @DATE 2022/4/11
 */

public class Main {
    private static final Configuration CONFIGURATION = Configuration.getConfiguration("src/main/resources/xw-load-balancing.xml");
    public static final int SO_TIME_OUT = 300000;
    private static final int BUFFER_SIZE = 8092;
    private static int requestNumber = 0;

    public static void main(String[] args) {
        BalanceService balanceService = CONFIGURATION.getBalanceService();
        try {
            ServerSocket serverSocket = new ServerSocket(CONFIGURATION.getPort());
            // 一直监听，接收到新连接，则开启新线程去处理
            while (true) {
                Socket localSocket = serverSocket.accept();
                Server server = balanceService.getServer(requestNumber++,localSocket.getInetAddress().getHostAddress());
                //5分钟内无数据传输、关闭链接
                localSocket.setSoTimeout(SO_TIME_OUT);
                log(localSocket.getRemoteSocketAddress() + "  conected");
                new SocketThread(localSocket,server.getAddress(),server.getPort()).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
