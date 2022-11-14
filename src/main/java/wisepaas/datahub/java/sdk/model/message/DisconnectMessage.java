package wisepaas.datahub.java.sdk.model.message;

public class DisconnectMessage extends BaseMessage {
    public DObject d;

    public DisconnectMessage() {
        d = new DObject();
    }

    class DObject {
        int DsC;

        DObject() {
            DsC = 1;
        }
    }
}
