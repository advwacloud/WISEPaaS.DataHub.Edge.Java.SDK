package wisepaas.datahub.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const.ActionType;
import wisepaas.datahub.java.sdk.common.Const.Status;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.edge.EdgeDeviceStatus;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

public class Testpoint {
    static int deviceCount = 1;

    static int ArrATagCount = 1;
    static int ArrDTagCount = 1;
    static int ArrTTagCount = 1;

    static int ATagCount = 1;
    static int DTagCount = 1;
    static int TTagCount = 1;

    static int startNum =1;

    private static Boolean uploadCfgTest(EdgeAgent agent) {
        EdgeConfig config = new EdgeConfig();

        config.Node = new EdgeConfig.NodeConfig();

        config.Node.DeviceList = new ArrayList<EdgeConfig.DeviceConfig>();

        for (int i = 1; i <= deviceCount; i++) {
            EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
            {
                device.Id = "Device" + i;
                device.Name = "Device " + i;
                device.Type = "Smart Device";
                device.Description = "Device " + i;
                device.RetentionPolicyName = "";
            }

            device.AnalogTagList = new ArrayList<EdgeConfig.AnalogTagConfig>();
            device.DiscreteTagList = new ArrayList<EdgeConfig.DiscreteTagConfig>();
            device.TextTagList = new ArrayList<EdgeConfig.TextTagConfig>();

            for (int j = 1; j <= ArrATagCount; j++) {
                EdgeConfig.AnalogTagConfig ArrAnalogTag = new EdgeConfig.AnalogTagConfig();
                {
                    ArrAnalogTag.Name = "ArrayATag" + j;
                    ArrAnalogTag.Description = "ArrayATag " + j;
                    ArrAnalogTag.ReadOnly = false;
                    ArrAnalogTag.ArraySize = 3;
                    ArrAnalogTag.SpanHigh = 1000.0;
                    ArrAnalogTag.SpanLow = 0.0;
                    ArrAnalogTag.EngineerUnit = "";
                    ArrAnalogTag.IntegerDisplayFormat = 4;
                    ArrAnalogTag.FractionDisplayFormat = 2;

                }
                device.AnalogTagList.add(ArrAnalogTag);
            }

            for (int j = 1; j <= ATagCount; j++) {
                EdgeConfig.AnalogTagConfig analogTag = new EdgeConfig.AnalogTagConfig();
                {
                    analogTag.Name = "中文ATag" + j;
                    analogTag.Description = "ATag " + j;
                    analogTag.ReadOnly = false;
                    analogTag.ArraySize = 0;
                    analogTag.SpanHigh = 1000.0;
                    analogTag.SpanLow = 0.0;
                    analogTag.EngineerUnit = "";
                    analogTag.IntegerDisplayFormat = 4;
                    analogTag.FractionDisplayFormat = 2;

                }
                device.AnalogTagList.add(analogTag);
            }

            for (int j = 1; j <= DTagCount; j++) {
                EdgeConfig.DiscreteTagConfig discreteTag = new EdgeConfig.DiscreteTagConfig();
                {
                    discreteTag.Name = "中文DTag" + j;
                    discreteTag.Description = "DTag " + j;
                    discreteTag.ReadOnly = false;
                    discreteTag.ArraySize = 0;
                    discreteTag.State0 = "0";
                    discreteTag.State1 = "1";
                    discreteTag.State2 = "";
                    discreteTag.State3 = "";
                    discreteTag.State4 = "";
                    discreteTag.State5 = "";
                    discreteTag.State6 = "";
                    discreteTag.State7 = "";

                }
                device.DiscreteTagList.add(discreteTag);

            }

            for (int j = 1; j <= ArrDTagCount; j++) {
                EdgeConfig.DiscreteTagConfig ArrayDiscreteTag = new EdgeConfig.DiscreteTagConfig();
                {
                    ArrayDiscreteTag.Name = "ArrayDTag" + j;
                    ArrayDiscreteTag.Description = "ArrayDTag " + j;
                    ArrayDiscreteTag.ReadOnly = false;
                    ArrayDiscreteTag.ArraySize = 3;
                    ArrayDiscreteTag.State0 = "0";
                    ArrayDiscreteTag.State1 = "1";
                    ArrayDiscreteTag.State2 = "";
                    ArrayDiscreteTag.State3 = "";
                    ArrayDiscreteTag.State4 = "";
                    ArrayDiscreteTag.State5 = "";
                    ArrayDiscreteTag.State6 = "";
                    ArrayDiscreteTag.State7 = "";

                }
                device.DiscreteTagList.add(ArrayDiscreteTag);
            }

            for (int j = 1; j <= ArrTTagCount; j++) {
                EdgeConfig.TextTagConfig ArrayTextTag = new EdgeConfig.TextTagConfig();
                {
                    ArrayTextTag.Name = "ArrayTTag" + j;
                    ArrayTextTag.Description = "ArrayTTag " + j;
                    ArrayTextTag.ReadOnly = false;
                    ArrayTextTag.ArraySize = 3;
                }
                device.TextTagList.add(ArrayTextTag);
            }

            for (int j = 1; j <= TTagCount; j++) {
                EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
                {
                    textTag.Name = "TTag" + j;
                    textTag.Description = "TTag " + j;
                    textTag.ReadOnly = false;
                    textTag.ArraySize = 0;
                }
                device.TextTagList.add(textTag);
            }

            config.Node.DeviceList.add(device);
        }

        Boolean result = agent.UploadConfig(ActionType.Delsert, config);
        return result;
    }

    private static Boolean sendBigDataTest(EdgeAgent agent) {
        Random random = new Random();
        EdgeData data = new EdgeData();
        for (int i = 1; i <= 1; i++) {
            for (int j = 1; j <= 50000; j++) {
                EdgeData.Tag aTag = new EdgeData.Tag();

                aTag.DeviceId = "Device" + i;
                aTag.TagName = "ATag" + j;
                aTag.Value = random.nextInt(100);
                // aTag.Value = 99999.9999;

                data.TagList.add(aTag);

            }
        }
        data.Timestamp = new Date();
        Boolean result = agent.SendData(data);
        return result;
    }

    private static Boolean sendDataTest(EdgeAgent agent) {
        Random random = new Random();
        EdgeData data = new EdgeData();
        for (int i = 1; i <= deviceCount; i++) {
            for (int j = 1; j <= ArrATagCount; j++) {
                // analog array tag
                HashMap<String, Object> mapVal = new HashMap<String, Object>();
                mapVal.put("0", random.nextInt(100));
                mapVal.put("1", random.nextInt(100));
                mapVal.put("2", random.nextInt(100));
                // mapVal.put("3", "fake");
                EdgeData.Tag ArrATag = new EdgeData.Tag();
                {
                    ArrATag.DeviceId = "Device" + i;
                    ArrATag.TagName = "ArrayATag" + j;
                    ArrATag.Value = mapVal;
                }

                data.TagList.add(ArrATag);
            }

            for (int j = 1; j <= ArrDTagCount; j++) {
                // analog array tag
                HashMap<String, Object> mapVal = new HashMap<String, Object>();
                mapVal.put("0", Math.round(Math.random()));
                mapVal.put("1", Math.round(Math.random()));
                mapVal.put("2", Math.round(Math.random()));
                // mapVal.put("3", "fake");
                EdgeData.Tag ArrDTag = new EdgeData.Tag();
                {
                    ArrDTag.DeviceId = "Device" + i;
                    ArrDTag.TagName = "ArrayDTag" + j;
                    ArrDTag.Value = mapVal;
                }

                data.TagList.add(ArrDTag);
            }

            for (int j = 1; j <= ArrTTagCount; j++) {
                // analog array tag
                HashMap<String, Object> mapVal = new HashMap<String, Object>();
                mapVal.put("0", "TEST " + Integer.toString(j) + "(" + Math.round(Math.random()) + ")");
                mapVal.put("1", "TEST " + Integer.toString(j) + "(" + Math.round(Math.random()) + ")");
                mapVal.put("2", "TEST " + Integer.toString(j) + "(" + Math.round(Math.random()) + ")");
                // mapVal.put("3", 123);

                EdgeData.Tag ArrTTag = new EdgeData.Tag();
                {
                    ArrTTag.DeviceId = "Device" + i;
                    ArrTTag.TagName = "ArrayTTag" + j;
                    ArrTTag.Value = mapVal;
                }

                data.TagList.add(ArrTTag);
            }

            for (int j = 1; j <= ATagCount; j++) {
                EdgeData.Tag aTag = new EdgeData.Tag();
                {
                    aTag.DeviceId = "Device" + i;
                    aTag.TagName = "中文ATag" + j;
                    aTag.Value = startNum;
                    // aTag.Value = "fake";
                }

                data.TagList.add(aTag);
            }

            for (int j = 1; j <= DTagCount; j++) {
                EdgeData.Tag dTag = new EdgeData.Tag();
                {
                    dTag.DeviceId = "Device" + i;
                    dTag.TagName = "中文DTag" + j;
                    dTag.Value = Math.round(Math.random());
                    // dTag.Value = "fake";
                }
                data.TagList.add(dTag);
            }

            for (int j = 1; j <= TTagCount; j++) {
                EdgeData.Tag tTag = new EdgeData.Tag();
                {
                    tTag.DeviceId = "Device" + i;
                    tTag.TagName = "TTag" + j;
                    tTag.Value = "TEST " + Integer.toString(j) + "(" + Math.round(Math.random()) + ")";
                    // tTag.Value = 123;
                }
                data.TagList.add(tTag);

            }
        }
        data.Timestamp = new Date();
        Boolean result = agent.SendData(data);
        startNum++;

        return result;
    }

    private static Boolean sendDataInLoop(EdgeAgent agent) {
        final EdgeAgent _agent = agent;
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                // System.out.println("Task performed on: " + new Date() + "n" + "Thread's name:
                // "
                // + Thread.currentThread().getName());
                sendDataTest(_agent);
            }
        }, 1000, 1000);

        return true;
    }

    private static Boolean SendDeviceStatusTest(EdgeAgent agent) {
        EdgeDeviceStatus deviceStatus = new EdgeDeviceStatus();
        for (int i = 1; i <= 2; i++) {
            EdgeDeviceStatus.Device device = new EdgeDeviceStatus.Device();
            {
                device.Id = "Device" + i;
                device.Status = Status.Online;
            }
            deviceStatus.DeviceList.add(device);
        }
        deviceStatus.Timestamp = new Date();
        Boolean result = agent.SendDeviceStatus(deviceStatus);
        return result;
    }

    public static void main(String[] args) {
        EdgeAgentOptions options = new EdgeAgentOptions();
        options.Heartbeat = 5000;

        options.UseSecure = false;
        options.NodeId = "";

        // ### DCCS Connection
        options.DCCS.APIUrl = "";
        options.DCCS.CredentialKey = "";

        // #### MQTT Connection
        // options.ConnectType = ConnectType.MQTT;

        // options.MQTT.HostName = "";
        // options.MQTT.Port = 1883;
        // options.MQTT.Username = "";
        // options.MQTT.Password = "";

        EdgeAgentListener agentListener = new EdgeAgentListener() {
            @Override
            public void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args) {
                System.out.println("Connected");
                //  uploadCfgTest(agent);
                //  sendDataInLoop(agent);
            
            }

            @Override
            public void Disconnected(EdgeAgent agent, DisconnectedEventArgs args) {
                System.out.println("Disconnected");
                System.out.println(args.getCause().toString());
            }

            @Override
            public void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs args) {
                System.out.println("MessageReceived");
            }
        };

        EdgeAgent _edgeAgent = new EdgeAgent(options, agentListener);
        _edgeAgent.Connect();

        // #### Test: big data recover to sqlite
        // Boolean result = sendBigDataTest(_edgeAgent);
        // System.out.println(result);

        // #### Test: Disconnect
        // try {
        // _edgeAgent.Connect();
        // Thread.sleep(5000);
        // _edgeAgent.Disconnect();
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }
}