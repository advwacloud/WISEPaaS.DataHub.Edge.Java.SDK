package wisepaas.datahub.java.sdk.model.message;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

import wisepaas.datahub.java.sdk.common.Const.ActionType;

public class ConfigMessage extends BaseMessage {
    @SerializedName("d")
    public DObject D;

    public ConfigMessage() {
        D = new DObject();
    }

    public class DObject {
        @SerializedName("Action")
        public Integer Action;

        @SerializedName("Scada")
        public HashMap<String, NodeObject> NodeList;

        DObject() {
            Action = ActionType.Create;
        }
    }

    public static class NodeObject {
        @SerializedName("Hbt")
        public Integer Heartbeat;

        @SerializedName("Type")
        public Integer Type;

        @SerializedName("Device")
        public HashMap<String, DeviceObject> DeviceList;

        public NodeObject() {
        }
    }

    public static class DeviceObject {
        @SerializedName("Name")
        public String Name;

        @SerializedName("Type")
        public String Type;

        @SerializedName("Desc")
        public String Description;

        @SerializedName("RP")
        public String RetentionPolicyName;

        @SerializedName("Tag")
        public HashMap<String, TagObject> TagList;

        public DeviceObject() {
        }
    }

    public static class TagObject {
        @SerializedName("Type")
        public Integer Type;

        @SerializedName("Desc")
        public String Description;

        @SerializedName("RO")
        public Integer ReadOnly;

        @SerializedName("Ary")
        public Integer ArraySize;

    }

    public static class AnalogTagObject extends TagObject {
        @SerializedName("SH")
        public Double SpanHigh;

        @SerializedName("SL")
        public Double SpanLow;

        @SerializedName("EU")
        public String EngineerUnit;

        @SerializedName("IDF")
        public Integer IntegerDisplayFormat;

        @SerializedName("FDF")
        public Integer FractionDisplayFormat;

        @SerializedName("SCALE")
        public Integer ScalingType;

        @SerializedName("SF1")
        public Double ScalingFactor1;

        @SerializedName("SF2")
        public Double ScalingFactor2;

        public AnalogTagObject() {
        }
    }

    public static class DiscreteTagObject extends TagObject {
        @SerializedName("S0")
        public String State0;

        @SerializedName("S1")
        public String State1;

        @SerializedName("S2")
        public String State2;

        @SerializedName("S3")
        public String State3;

        @SerializedName("S4")
        public String State4;

        @SerializedName("S5")
        public String State5;

        @SerializedName("S6")
        public String State6;

        @SerializedName("S7")
        public String State7;

        public DiscreteTagObject() {
        }
    }

    public static class TextTagObject extends TagObject {

        public TextTagObject() {
        }
    }
}
