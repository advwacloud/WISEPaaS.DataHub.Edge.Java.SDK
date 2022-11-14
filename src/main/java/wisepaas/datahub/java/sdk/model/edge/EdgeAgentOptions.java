package wisepaas.datahub.java.sdk.model.edge;

import wisepaas.datahub.java.sdk.common.Const.Agent;
import wisepaas.datahub.java.sdk.common.Const.EdgeType;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.Enum;

public class EdgeAgentOptions {
    public boolean AutoReconnect;
    public int ReconnectInterval;
    public String NodeId;
    public String DeviceId;
    public int Type;
    public int Heartbeat;
    public boolean DataRecover;
    public Enum.ConnectType ConnectType;
    public boolean UseSecure;
    public String AndroidPackageName;
    public int OS;
    public int MaxInflight;

    public MQTTOptions MQTT;
    public DCCSOptions DCCS;

    public EdgeAgentOptions() {
        this.AutoReconnect = true;
        this.ReconnectInterval = Agent.RECONNECT_INTERVAL;
        this.NodeId = "";
        this.DeviceId = "";
        this.Type = EdgeType.Gateway;
        this.Heartbeat = Const.Heartbeat.DEAFAULT_HEARTBEAT_INTERVAL;
        this.DataRecover = true;
        this.ConnectType = Enum.ConnectType.DCCS;
        this.UseSecure = false;
        this.AndroidPackageName = "";
        this.OS = 0;
        this.MaxInflight = Const.Agent.MAX_INFLIGHT;

        this.MQTT = new MQTTOptions();
        this.DCCS = new DCCSOptions();
    }
}
