package wisepaas.datahub.java.sdk.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;

import org.apache.commons.lang3.BooleanUtils;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const.ActionType;
import wisepaas.datahub.java.sdk.common.Const.NODEConfigType;
import wisepaas.datahub.java.sdk.common.Const.TagType;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig.AnalogTagConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig.DeviceConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig.DiscreteTagConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig.TextTagConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.edge.EdgeData.Tag;
import wisepaas.datahub.java.sdk.model.edge.EdgeDeviceStatus;
import wisepaas.datahub.java.sdk.model.edge.EdgeDeviceStatus.Device;
import wisepaas.datahub.java.sdk.model.message.ConfigCacheMessage;
import wisepaas.datahub.java.sdk.model.message.ConfigMessage;
import wisepaas.datahub.java.sdk.model.message.DataMessage;
import wisepaas.datahub.java.sdk.model.message.DeviceStatusMessage;

public class Converter {
    public static Boolean ConvertWholeConfig(int action, String nodeId, EdgeConfig config, StringBuilder payload,
            int heartbeat) {
        try {
            if (config == null) {
                return false;
            }

            ConfigMessage msg = new ConfigMessage();
            msg.D.Action = action;
            msg.D.NodeList = new HashMap<String, ConfigMessage.NodeObject>();

            ConfigMessage.NodeObject nodeObj = new ConfigMessage.NodeObject();
            nodeObj.Heartbeat = heartbeat / 1000;
            nodeObj.Type = NODEConfigType.NODE;

            if (config.Node.DeviceList != null) {
                nodeObj.DeviceList = new HashMap<String, ConfigMessage.DeviceObject>();

                for (DeviceConfig device : config.Node.DeviceList) {
                    ConfigMessage.DeviceObject deviceObj = new ConfigMessage.DeviceObject();
                    deviceObj.Name = device.Name;
                    deviceObj.Type = device.Type;
                    deviceObj.Description = device.Description;
                    deviceObj.RetentionPolicyName = device.RetentionPolicyName;

                    if (device.AnalogTagList != null) {
                        for (AnalogTagConfig analogTag : device.AnalogTagList) {
                            ConfigMessage.AnalogTagObject analogTagObject = new ConfigMessage.AnalogTagObject();
                            analogTagObject.Type = TagType.Analog;
                            analogTagObject.Description = analogTag.Description;
                            analogTagObject.ReadOnly = (analogTag.ReadOnly != null)
                                    ? BooleanUtils.toInteger(analogTag.ReadOnly)
                                    : null;
                            analogTagObject.ArraySize = analogTag.ArraySize;
                            analogTagObject.SpanHigh = analogTag.SpanHigh;
                            analogTagObject.SpanLow = analogTag.SpanLow;
                            analogTagObject.EngineerUnit = analogTag.EngineerUnit;
                            analogTagObject.IntegerDisplayFormat = analogTag.IntegerDisplayFormat;
                            analogTagObject.FractionDisplayFormat = analogTag.FractionDisplayFormat;
                            analogTagObject.ScalingType = analogTag.ScalingType;
                            analogTagObject.ScalingFactor1 = analogTag.ScalingFactor1;
                            analogTagObject.ScalingFactor2 = analogTag.ScalingFactor2;

                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }
                            deviceObj.TagList.put(analogTag.Name, analogTagObject);
                        }
                    }

                    if (device.DiscreteTagList != null) {
                        for (DiscreteTagConfig discreteTag : device.DiscreteTagList) {
                            ConfigMessage.DiscreteTagObject discreteTagObject = new ConfigMessage.DiscreteTagObject();
                            discreteTagObject.Type = TagType.Discrete;
                            discreteTagObject.Description = discreteTag.Description;
                            discreteTagObject.ReadOnly = (discreteTag.ReadOnly != null)
                                    ? BooleanUtils.toInteger(discreteTag.ReadOnly)
                                    : null;
                            discreteTagObject.ArraySize = discreteTag.ArraySize;
                            discreteTagObject.State0 = discreteTag.State0;
                            discreteTagObject.State1 = discreteTag.State1;
                            discreteTagObject.State2 = discreteTag.State2;
                            discreteTagObject.State3 = discreteTag.State3;
                            discreteTagObject.State4 = discreteTag.State4;
                            discreteTagObject.State5 = discreteTag.State5;
                            discreteTagObject.State6 = discreteTag.State6;
                            discreteTagObject.State7 = discreteTag.State7;

                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }
                            deviceObj.TagList.put(discreteTag.Name, discreteTagObject);
                        }

                    }

                    if (device.TextTagList != null) {
                        for (TextTagConfig textTag : device.TextTagList) {
                            ConfigMessage.TextTagObject textTagObject = new ConfigMessage.TextTagObject();
                            textTagObject.Type = TagType.Text;
                            textTagObject.Description = textTag.Description;
                            textTagObject.ReadOnly = (textTag.ReadOnly != null)
                                    ? BooleanUtils.toInteger(textTag.ReadOnly)
                                    : null;
                            textTagObject.ArraySize = textTag.ArraySize;

                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }

                            deviceObj.TagList.put(textTag.Name, textTagObject);
                        }
                    }

                    nodeObj.DeviceList.put(device.Id, deviceObj);
                }
            }
            msg.D.NodeList.put(nodeId, nodeObj);
            String payloadResult = new Gson().toJson(msg);
            payload.append(payloadResult);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return true;

    }

    public static Boolean ConvertData(EdgeData data, ArrayList<String> payloads, EdgeAgent agent) {
        try {
            if (data == null) {
                return false;
            }

            // todo: sort by device id
            ArrayList<Tag> list = data.TagList;

            DataMessage msg = null;

            for (int i = 0; i < list.size(); i++) {
                Tag tag = list.get(i);

                if (msg == null) {
                    msg = new DataMessage();
                }

                if (msg.d.containsKey(tag.DeviceId) == false) {
                    msg.d.put(tag.DeviceId, new HashMap<String, Object>());
                }

                ConfigCacheMessage.TagObject tagInfo = GetTagFromCfgCache(agent.cfgCache, agent.Options.NodeId,
                        tag.DeviceId, tag.TagName);

                if(tagInfo == null) {
                    Object ConvertVal;
                    ConvertVal = tag.Value;

                    ((HashMap<String, Object>) msg.d.get(tag.DeviceId)).put(tag.TagName, ConvertVal);
                } else {
                    Integer tagType = (tagInfo == null) ? null : tagInfo.Type;
                    Integer arraySize = (tagInfo == null) ? 0 : tagInfo.ArraySize;
    
                    Boolean checkTypeResult = checkValByType(tag.Value, tagType);
    
                    if (checkTypeResult == false) {
                        System.out.println("Type error of tag value, nodeId:" + agent.Options.NodeId + ", deviceId:"
                                + tag.DeviceId + ", tagName:" + tag.TagName + ", value:" + tag.Value + ", type:" + tagType);
                        return false;
                    }
    
                    Integer Fdf = (tagInfo == null) ? null : tagInfo.FractionDisplayFormat;
    
                    Object ConvertVal;
    
                    if (Fdf != null) {
                        if (arraySize > 0) { // analog Array tag
                            for (Map.Entry value : ((HashMap<String, Object>) tag.Value).entrySet()) {
                                Object _val = GetRoundDownValByFDF(Double.valueOf(value.getValue().toString()), Fdf);
                                ((HashMap<String, Object>) tag.Value).put(value.getKey().toString(), _val);
                            }
                            ConvertVal = tag.Value;
                        } else {
                            ConvertVal = GetRoundDownValByFDF(Double.valueOf(tag.Value.toString()), Fdf);
                        }
    
                    } else {
                        ConvertVal = tag.Value;
                    }
    
                    ((HashMap<String, Object>) msg.d.get(tag.DeviceId)).put(tag.TagName, ConvertVal);
                }



                if (i == list.size() - 1) {
                    DateFormat df = new SimpleDateFormat(Const.TimeFormat);
                    df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    msg.ts = df.format(data.Timestamp);

                    String payloadResult = new Gson().toJson(msg);

                    payloads.add(payloadResult);

                    msg = null;
                }
            }

            return true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }

    }

    public static Boolean ConvertDeviceStatus(EdgeDeviceStatus deviceStatus, StringBuilder payload) {
        try {
            if (deviceStatus == null) {
                return false;
            }
            DeviceStatusMessage msg = new DeviceStatusMessage();

            DateFormat df = new SimpleDateFormat(Const.TimeFormat);
            df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

            msg.ts = df.format(deviceStatus.Timestamp);

            for (Device device : deviceStatus.DeviceList) {
                msg.d.DeviceList.put(device.Id, device.Status);
            }

            String payloadResult = new Gson().toJson(msg);
            payload.append(payloadResult);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean ConvertDeleteConfig(String nodeId, EdgeConfig config, StringBuilder payload) {
        try {
            if (config == null) {
                return false;
            }

            ConfigMessage msg = new ConfigMessage();
            msg.D.Action = ActionType.Delete;
            msg.D.NodeList = new HashMap<String, ConfigMessage.NodeObject>();

            ConfigMessage.NodeObject nodeObj = new ConfigMessage.NodeObject();

            if (config.Node.DeviceList != null) {
                nodeObj.DeviceList = new HashMap<String, ConfigMessage.DeviceObject>();
                for (DeviceConfig device : config.Node.DeviceList) {
                    ConfigMessage.DeviceObject deviceObj = new ConfigMessage.DeviceObject();

                    if (device.AnalogTagList != null) {
                        for (AnalogTagConfig analogTag : device.AnalogTagList) {
                            ConfigMessage.AnalogTagObject analogTagObject = new ConfigMessage.AnalogTagObject();
                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }

                            deviceObj.TagList.put(analogTag.Name, analogTagObject);
                        }
                    }

                    if (device.DiscreteTagList != null) {
                        for (DiscreteTagConfig discreteTag : device.DiscreteTagList) {
                            ConfigMessage.DiscreteTagObject discreteTagObject = new ConfigMessage.DiscreteTagObject();
                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }
                            deviceObj.TagList.put(discreteTag.Name, discreteTagObject);
                        }
                    }

                    if (device.TextTagList != null) {
                        for (TextTagConfig textTag : device.TextTagList) {
                            ConfigMessage.TextTagObject textTagObject = new ConfigMessage.TextTagObject();
                            if (deviceObj.TagList == null) {
                                deviceObj.TagList = new HashMap<String, ConfigMessage.TagObject>();
                            }
                            deviceObj.TagList.put(textTag.Name, textTagObject);
                        }
                    }
                    nodeObj.DeviceList.put(device.Id, deviceObj);
                }
            }
            msg.D.NodeList.put(nodeId, nodeObj);
            String payloadResult = new Gson().toJson(msg);
            payload.append(payloadResult);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    private static ConfigCacheMessage.TagObject GetTagFromCfgCache(ConfigCacheMessage cfgCache, String nodeId,
            String deviceId, String tagName) {
        try {
            ConfigCacheMessage.TagObject tagInfo = cfgCache.D.NodeList.get(nodeId).DeviceList.get(deviceId).TagList
                    .get(tagName);
            return tagInfo;
        } catch (Exception e) {
            // TODO: handle exception
            // e.printStackTrace();
            return null;
        }
    }

    private static Double GetRoundDownValByFDF(Double val, Integer FDF) {
        Double result = val;

        if (FDF < 0) {
            System.out.println("FractionDisplayFormat error, the fdf can not lower than zero");
            return result;
        }

        Double handleNum = Math.pow(10, FDF);
        result = Math.floor(val * handleNum) / handleNum;
        return result;
    }

    private static Boolean checkValByType(Object val, Integer type) {
        Boolean result = false;
        if (type == null) {
            System.out.println("type is missing when checkValByType");
            return result;
        } else {
            switch (type) {
                case Const.TagType.Analog:
                case Const.TagType.Discrete:
                    if (val instanceof HashMap) { // Array Tag
                        for (Object value : ((HashMap<String, Object>) val).values()) {
                            if (!(value instanceof Number)) {
                                return false;
                            }
                        }
                        return true;
                    } else if (val instanceof Number) {
                        return true;
                    }
                    break;
                case Const.TagType.Text:
                    if (val instanceof HashMap) { // Array Tag
                        for (Object value : ((HashMap<String, Object>) val).values()) {
                            if (!(value instanceof String)) {
                                return false;
                            }
                        }
                        return true;
                    } else if (val instanceof String) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
