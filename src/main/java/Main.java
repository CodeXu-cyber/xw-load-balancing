import system.configure.Configuration;
import system.random.BalanceService;

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
    private static int requestNumber = 0;
    private static final int BUFFER_SIZE = 8092;
    /**
     * 一分钟超时时间
     */
    private static final int SET_TIME_OUT = 60000;

    public static void main(String[] args) {
        BalanceService balanceService = CONFIGURATION.getBalanceService();

    }
}
