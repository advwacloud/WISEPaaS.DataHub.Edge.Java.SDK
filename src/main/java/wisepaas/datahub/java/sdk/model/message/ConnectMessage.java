package wisepaas.datahub.java.sdk.model.message;

public class ConnectMessage extends BaseMessage {
    public DObject d;

    public ConnectMessage() {
        d = new DObject();
    }

    class DObject {
        int Con;

        DObject() {
            Con = 1;
        }
    }
}
