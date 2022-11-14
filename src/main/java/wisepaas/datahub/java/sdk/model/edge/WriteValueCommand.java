package wisepaas.datahub.java.sdk.model.edge;

import java.util.ArrayList;
import java.util.Date;

public class WriteValueCommand {
    public ArrayList<Device> DeviceList;
    public Date Timestamp;

    public WriteValueCommand() {
        DeviceList = new ArrayList<Device>();
        Timestamp = new Date();
    }

    public static class Device {
        public String Id;
        public ArrayList<Tag> TagList;

        public Device() {
            Id = "";
            TagList = new ArrayList<Tag>();
        }
    }

    public static class Tag {
        public String Name;
        public Object Value;

        public Tag() {
            Name = "";
            Value = new Object();
        }
    }
}