package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import system.entity.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName XmlUtil
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class XmlUtil {
    public static Map<String, Object> analysisConfig(File file) {
        Map<String, Object> config = new HashMap<>();
        SAXReader reader = new SAXReader();
        List<Server> servers = new ArrayList<>();
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        assert document != null;
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();
        for (Element child : childElements) {
            if (!child.elements().isEmpty()) {
                for (Element c : child.elements()) {
                    switch (child.getName()) {
                        case "servers":
                            servers.add(new Server(c.attributeValue("name"), c.attributeValue("address"), Integer.valueOf("".equals(c.attributeValue("weight")) ? "0" : c.attributeValue("weight"))));
                            break;
                        case "settings":
                            config.put(c.attributeValue("name"), c.attributeValue("value"));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        config.put("servers", servers);
        return config;
    }
}
