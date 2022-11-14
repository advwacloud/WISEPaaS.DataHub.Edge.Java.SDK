package wisepaas.datahub.java.sdk.model.message;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

public class DeviceStatusMessage extends BaseMessage {
    public DObject d;

    public DeviceStatusMessage() {
        d = new DObject();
    }

    public class DObject {
        @SerializedName("Dev")
        public HashMap<String, Integer> DeviceList;

        DObject() {
            DeviceList = new HashMap<String, Integer>();
        }
    }
}
