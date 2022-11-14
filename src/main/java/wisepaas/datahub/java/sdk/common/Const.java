package wisepaas.datahub.java.sdk.common;

public class Const {
    public static final class Topic {
        public static final String ConfigTopic = "/wisepaas/scada/%s/cfg";
        public static final String DataTopic = "/wisepaas/scada/%s/data";
        public static final String NodeConnTopic = "/wisepaas/scada/%s/conn";
        public static final String DeviceConnTopic = "/wisepaas/scada/%s/%s/conn";
        public static final String NodeCmdTopic = "/wisepaas/scada/%s/cmd";
        public static final String DeviceCmdTopic = "/wisepaas/scada/%s/%s/cmd";
        public static final String AckTopic = "/wisepaas/scada/%s/ack";
        public static final String CfgAckTopic = "/wisepaas/scada/%s/cfgack";
    }

    public static class QosMapping {
        public static final int AtMostOnce = 0;
        public static final int AtLeastOnce = 1;
        public static final int ExactlyOnce = 2;
    }

    public static class DataRecover {
        public static final String DatabaseFileName = "recover.sqlite";
        public static final int DEAFAULT_DATARECOVER_INTERVAL = 3000;
        public static final int DEAFAULT_DATARECOVER_COUNT = 10;
        public static final int SQLITE_CONN_TIMEOUT = 15000;
    }

    public static class CfgCache {
        public static final String CfgCacheFileName = "cfgCache.json";
    }

    public static class Agent {
        public static final int RECONNECT_INTERVAL = 5000;
        public static final int GET_DCCS_JSON_TIMEOUT = 10000;
        public static final int MAX_INFLIGHT = 10000;
    }

    public static class Heartbeat {
        public static final int DEAFAULT_HEARTBEAT_INTERVAL = 60000;
    }

    public static class Limit {
        public static final int DataMaxTagCount = 100;
    }

    public static class Status {
        public static final int Offline = 0;
        public static final int Online = 1;

    }

    public static class EdgeType {
        public static final int Gateway = 0;
        public static final int Device = 1;
    }

    public static class MessageType {
        public static final int WriteValue = 0;
        public static final int WriteConfig = 1;
        public static final int TimeSync = 2;
        public static final int ConfigAck = 3;
    }

    public static class ActionType {
        public static final int Create = 1;
        public static final int Update = 2;
        public static final int Delete = 3;
        public static final int Delsert = 4;
    }

    public static class NODEConfigType {
        public static final int NODE = 1;
        public static final int Gateway = 2;
        public static final int VirtualGroup = 3;
    }

    public static class TagType {
        public static final int Analog = 1;
        public static final int Discrete = 2;
        public static final int Text = 3;
    }

    public static final String TimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static class OS {
        public static final int Android = 1;
    }
}