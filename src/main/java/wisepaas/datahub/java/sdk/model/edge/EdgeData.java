package wisepaas.datahub.java.sdk.model.edge;

import java.util.ArrayList;
import java.util.Date;

public class EdgeData {
    public ArrayList<Tag> TagList;

    public Date Timestamp;

    public EdgeData() {
        TagList = new ArrayList<Tag>();
        Timestamp = new Date();
    }

    public static class Tag {
        public String DeviceId;
        public String TagName;
        public Object Value;

        public Tag() {
            DeviceId = "";
            TagName = "";
            Value = new Object();
        }
    }
}