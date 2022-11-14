package wisepaas.datahub.java.sdk.model.message;

public class LastWillMessage extends BaseMessage {
    public DObject d;

    public LastWillMessage() {
        d = new DObject();
    }

    class DObject {
        int UeD;

        DObject() {
            UeD = 1;
        }
    }
}
