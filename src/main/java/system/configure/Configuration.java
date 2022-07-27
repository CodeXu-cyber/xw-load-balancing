package system.configure;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import system.entity.Server;
import system.random.BalanceService;
import system.random.imp.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置类
 *
 * @author xuwei
 * @date 2022/07/18 11:30
 **/
public class Configuration {
    private volatile static Configuration configuration;
    private BalanceService balanceService;
    private Integer port;

    private Configuration(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            SAXReader reader = new SAXReader();
            List<Server> serverList = new ArrayList<>();
            Document document = null;
            try {
                document = reader.read(file);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Integer vnnNodeCount = 3;
            assert document != null;
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                if (!child.elements().isEmpty()) {
                    for (Element c : child.elements()) {
                        switch (child.getName()) {
                            case "servers":
                                serverList.add(new Server(c.attributeValue("name"), c.attributeValue("address"), Integer.valueOf("".equals(c.attributeValue("port")) ? "80" : c.attributeValue("port")), Integer.valueOf("".equals(c.attributeValue("weight")) ? "0" : c.attributeValue("weight"))));
                                break;
                            case "settings":
                                switch (c.attributeValue("name")) {
                                    case "port":
                                        this.port = Integer.valueOf(c.attributeValue("value") == null ? "8088" : "".equals(c.attributeValue("value")) ? "8088" : c.attributeValue("value"));
                                        break;
                                    case "vnnNodeCount":
                                        vnnNodeCount = c.attributeValue("value") == null ? vnnNodeCount : "".equals(c.attributeValue("value")) ? vnnNodeCount : Integer.valueOf(c.attributeValue("value"));
                                        break;
                                    case "random":
                                        String random = c.attributeValue("value") == null ? "RandomServer" : "".equals(c.attributeValue("value")) ? "RandomServer" : c.attributeValue("value");
                                        switch (random) {
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
                                            case "ConsistentHash":
                                                balanceService = new ConsistentHashServerImpl(serverList, vnnNodeCount);
                                                break;
                                            case "RandomServer":
                                            default:
                                                balanceService = new RandomServerImpl(serverList);
                                                break;
                                        }
                                    case "openServerMonitor":
                                        if ("true".equals(c.attributeValue("value"))) {
                                            balanceService = new ServerMonitorImpl(balanceService);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

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

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public Integer getPort() {
        return port;
    }

}
