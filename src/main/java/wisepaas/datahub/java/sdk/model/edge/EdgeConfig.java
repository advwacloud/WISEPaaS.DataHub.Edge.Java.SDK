package wisepaas.datahub.java.sdk.model.edge;

import java.util.ArrayList;

public class EdgeConfig {
    public NodeConfig Node;

    public EdgeConfig() {
        Node = new NodeConfig();
    }

    public static class NodeConfig {
        public Integer Type;

        public ArrayList<DeviceConfig> DeviceList;

        public NodeConfig() {
        }
    }

    public static class DeviceConfig {
        public String Id;
        public String Name;
        public String Type;
        public String Description;
        public String RetentionPolicyName;

        public ArrayList<AnalogTagConfig> AnalogTagList;
        public ArrayList<DiscreteTagConfig> DiscreteTagList;
        public ArrayList<TextTagConfig> TextTagList;

        public DeviceConfig() {
        }
    }

    public static class TagConfig {
        public String Name;
        public String Description;
        public Boolean ReadOnly;
        public Integer ArraySize;

        public TagConfig() {
            Name = "";
        }
    }

    public static class AnalogTagConfig extends TagConfig {
        public Double SpanHigh;
        public Double SpanLow;
        public String EngineerUnit;
        public Integer IntegerDisplayFormat;
        public Integer FractionDisplayFormat;

        public Integer ScalingType;
        public Double ScalingFactor1;
        public Double ScalingFactor2;

        public AnalogTagConfig() {
        }
    }

    public static class DiscreteTagConfig extends TagConfig {
        public String State0;
        public String State1;
        public String State2;
        public String State3;
        public String State4;
        public String State5;
        public String State6;
        public String State7;

        public DiscreteTagConfig() {
        }
    }

    public static class TextTagConfig extends TagConfig {
        public TextTagConfig() {
        }
    }
}