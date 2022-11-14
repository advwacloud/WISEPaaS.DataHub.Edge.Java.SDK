package wisepaas.datahub.java.sdk.model.edge;

import com.google.gson.annotations.SerializedName;

public class DCCS {
    public String serviceHost;
    public Credential credential;

    public class Credential {
        public Protocols protocols;
    }

    public class Protocols {
        @SerializedName("mqtt+ssl")
        public MqttSsl mqttSsl;
        public Mqtt mqtt;
    }

    public class MqttSsl {
        public int port;
        public String username;
        public String password;
    }

    public class Mqtt {
        public int port;
        public String username;
        public String password;
    }
}
