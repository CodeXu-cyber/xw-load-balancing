package system.entity;

/**
 * @ClassName Server
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Server {
    private String serverName;
    private String address;
    private Integer port;
    private Integer weight;

    public Server() {
    }

    public Server(String serverName, String address, Integer port, Integer weight) {
        this.serverName = serverName;
        this.address = address;
        this.port = port;
        this.weight = weight;
    }

    public String getServerName() {
        return serverName;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverName='" + serverName + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                '}';
    }
}
