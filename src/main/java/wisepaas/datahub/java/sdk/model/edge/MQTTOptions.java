package wisepaas.datahub.java.sdk.model.edge;

import wisepaas.datahub.java.sdk.common.Enum;

public class MQTTOptions {
    public String HostName;
    public int Port;
    public String Username;
    public String Password;
    public Enum.Protocol ProtocolType;

    public MQTTOptions() {
        HostName = "";
        Port = 1883;
        Username = "";
        Password = "";
        ProtocolType = Enum.Protocol.TCP;
    }

    public MQTTOptions(String hostName, int port, String username, String password, Enum.Protocol protocolType) {
        this.HostName = hostName;
        this.Port = port;
        this.Username = username;
        this.Password = password;
        this.ProtocolType = protocolType;
    }
}