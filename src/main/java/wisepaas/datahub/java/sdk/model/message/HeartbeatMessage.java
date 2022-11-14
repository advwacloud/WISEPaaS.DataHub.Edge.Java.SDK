package wisepaas.datahub.java.sdk.model.message;

public class HeartbeatMessage extends BaseMessage {
    public DObject d;

    public HeartbeatMessage() {
        d = new DObject();
    }

    class DObject {
        int Hbt;

        DObject() {
            Hbt = 1;
        }
    }
}
