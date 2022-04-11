package system.entity;

/**
 * @ClassName Server
 * @Author xuwei
 * @DATE 2022/4/11
 */
public class Server {
    private String serverName;
    private String address;
    private Integer weight;

    public Server() {
    }

    public Server(String serverName, String address, Integer weight) {
        this.serverName = serverName;
        this.address = address;
        this.weight = weight;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverName='" + serverName + '\'' +
                ", address='" + address + '\'' +
                ", weight=" + weight +
                '}';
    }
}
