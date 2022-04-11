import system.configure.Configuration;
import system.random.BalanceService;

/**
 * @ClassName Main
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Main {
    private static final Configuration CONFIGURATION = Configuration.getConfiguration("src/main/resources/xw-load-balancing.xml");
    private static int requestNumber = 0;

    public static void main(String[] args) {
        BalanceService balanceService = CONFIGURATION.getBalanceService();
        for (int i = 0; i < 20; i++) {
            if (requestNumber==Integer.MAX_VALUE){
                requestNumber = 0;
            }
            System.out.println(balanceService.getServer(requestNumber++,"http:127.0.0.1:8080"));
        }

    }
}
