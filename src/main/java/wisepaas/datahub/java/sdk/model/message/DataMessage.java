package wisepaas.datahub.java.sdk.model.message;

import java.util.HashMap;

public class DataMessage extends BaseMessage {
    public HashMap<String, Object> d;

    public DataMessage() {
        d = new HashMap<String, Object>();
    }
}