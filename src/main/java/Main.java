import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import redis.clients.jedis.Jedis;
import system.configure.Configuration;
import system.entity.Server;
import system.balance.BalanceService;
import system.redis.Subscriber;
import system.socket.SocketThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Main
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Main {
    public static final int SO_TIME_OUT = 8000;
    private static final Configuration CONFIGURATION = Configuration.getConfiguration("src/main/resources/xw-load-balancing.xml");
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 10,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new CustomizableThreadFactory());
    private static final Logger logger = Logger.getLogger(Main.class);
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
                if (server == null) {
                    System.exit(0);
                }
                //8s内无数据传输、关闭链接
                localSocket.setSoTimeout(SO_TIME_OUT);
                logger.info(localSocket.getRemoteSocketAddress().toString().replace("/", "") + "  connect to server : \"" + server.getServerName() + "\"");
                //启动线程处理本连接
                THREAD_POOL_EXECUTOR.submit(new SocketThread(localSocket, server.getAddress(), server.getPort()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
