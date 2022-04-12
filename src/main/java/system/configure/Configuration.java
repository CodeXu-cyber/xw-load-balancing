package system.configure;

import system.entity.Server;
import system.random.BalanceService;
import system.random.imp.*;
import utils.XmlUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Configuration
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Configuration {
    private volatile static Configuration configuration;
    private List<Server> serverList;
    private BalanceService balanceService;
    private Integer port;

    public static Configuration getConfiguration(String fileName) {
        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = new Configuration(fileName);
                }
            }
        }
        return configuration;
    }

    private Configuration(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            Map<String, Object> config = XmlUtil.analysisConfig(file);
            this.port = Integer.valueOf((String) config.get("port"));
            this.serverList = (List<Server>) config.get("servers");
            String random = (String) config.get("random");
            switch (random){
                case "WeightRandomServer":
                    balanceService = new WeightRandomServerImpl(serverList);
                    break;
                case "PollServer":
                    balanceService = new PollServerImpl(serverList);
                    break;
                case "WeightPollServer":
                    balanceService = new WeightPollServerImpl(serverList);
                    break;
                case "HashServer":
                    balanceService = new HashServerImpl(serverList);
                    break;
                case "RandomServer":
                default:
                    balanceService = new RandomServerImpl(serverList);
                    break;
            }
        }
    }

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public Integer getPort() {
        return port;
    }

    public List<Server> getServerList() {
        return serverList;
    }
}
