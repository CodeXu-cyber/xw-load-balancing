package system.configure;

import system.entity.Server;
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
    public List<Server> servers;
    public String random;

    public Configuration(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            Map<String, Object> config = XmlUtil.analysisConfig(file);
            this.servers = (List<Server>) config.get("servers");
            this.random = (String) config.get("random");
        }
    }
}
