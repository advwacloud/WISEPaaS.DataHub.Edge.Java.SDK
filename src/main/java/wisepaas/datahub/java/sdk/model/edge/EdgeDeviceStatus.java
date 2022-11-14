package wisepaas.datahub.java.sdk.model.edge;

import java.util.ArrayList;
import java.util.Date;

import wisepaas.datahub.java.sdk.common.Const;

public class EdgeDeviceStatus {
    public ArrayList<Device> DeviceList;
    public Date Timestamp;

    public EdgeDeviceStatus() {
        DeviceList = new ArrayList<Device>();
        Timestamp = new Date();
    }

    public static class Device {
        public String Id;
        public Integer Status;

        public Device() {
            Id = "";
            this.Status = Const.Status.Offline;
        }
    }
}