package wisepaas.datahub.java.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import wisepaas.datahub.java.sdk.common.Const.ActionType;
import wisepaas.datahub.java.sdk.common.Const.Agent;
import wisepaas.datahub.java.sdk.common.Const.DataRecover;
import wisepaas.datahub.java.sdk.common.Const.EdgeType;
import wisepaas.datahub.java.sdk.common.Const.MessageType;
import wisepaas.datahub.java.sdk.common.Const.QosMapping;
import wisepaas.datahub.java.sdk.common.Const.Topic;
import wisepaas.datahub.java.sdk.common.CfgCacheHelper;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.Converter;
import wisepaas.datahub.java.sdk.common.DataRecoverHelper;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;
import wisepaas.datahub.java.sdk.common.Helpers;
import wisepaas.datahub.java.sdk.model.edge.ConfigAck;
import wisepaas.datahub.java.sdk.model.edge.DCCS;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.edge.EdgeDeviceStatus;
import wisepaas.datahub.java.sdk.model.edge.TimeSyncCommand;
import wisepaas.datahub.java.sdk.model.edge.WriteValueCommand;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;
import wisepaas.datahub.java.sdk.model.message.ConfigCacheMessage;
import wisepaas.datahub.java.sdk.model.message.ConnectMessage;
import wisepaas.datahub.java.sdk.model.message.DisconnectMessage;
import wisepaas.datahub.java.sdk.model.message.HeartbeatMessage;
import wisepaas.datahub.java.sdk.model.message.LastWillMessage;

public class EdgeAgent {
    static Logger logger = Logger.getLogger(EdgeAgent.class.getName());

    class HeartbeatTask extends TimerTask {
        public void run() {
            String hbm = new Gson().toJson(new HeartbeatMessage());
            String topic = (EdgeAgent.this.Options.Type == EdgeType.Gateway) ? _nodeConnTopic : _deviceConnTopic;

            MqttMessage msg = new MqttMessage();
            msg.setQos(QosMapping.AtLeastOnce);
            msg.setRetained(true);
            msg.setPayload(hbm.getBytes());

            try {
                mqttClient.publish(topic, msg);
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class DataRecoverTask extends TimerTask {
        public void run() {
            if (_mqttConnected() == false) {
                return;
            }
            try {
                if (_recoverHelper != null && _recoverHelper.DataAvailable()) {
                    ArrayList<String> records = _recoverHelper.Read(DataRecover.DEAFAULT_DATARECOVER_COUNT);
                    for (String record : records) {
                        MqttMessage msg = new MqttMessage();
                        msg.setQos(QosMapping.AtLeastOnce);
                        msg.setRetained(false);
                        msg.setPayload(record.getBytes());

                        mqttClient.publish(_dataTopic, msg);

                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public EdgeAgentOptions Options;

    public ConfigCacheMessage cfgCache;

    private MqttAsyncClient mqttClient;
    private EdgeAgentListener agentListener;
    private DataRecoverHelper _recoverHelper;
    private CfgCacheHelper cfgCacheHelper;

    private HeartbeatTask heartbeatTask;
    private Timer heartbeatTimer;

    private DataRecoverTask dataRecoverTask;
    private Timer dataRecoverTimer;

    private String _configTopic;
    private String _dataTopic;
    private String _nodeConnTopic;
    private String _deviceConnTopic;
    private String _cmdTopic;
    private String _ackTopic;

    public EdgeAgent(EdgeAgentOptions options, EdgeAgentListener listener) {
        Options = options;
        agentListener = listener;

        Helpers.osSetter(Options.OS);

        if (Helpers.isAndroid() == true && options.AndroidPackageName == "") {
            throw new Error("If OS is Android, then AndroidPackageName must be set");
        }

        if (options.DataRecover) {
            if (Helpers.isAndroid() == true) {
                _recoverHelper = new DataRecoverHelper(options.AndroidPackageName);
            } else {
                _recoverHelper = new DataRecoverHelper(null);
            }
        }

        if (Helpers.isAndroid() == true) {
            cfgCacheHelper = new CfgCacheHelper(options.AndroidPackageName);
        } else {
            cfgCacheHelper = new CfgCacheHelper(null);
        }

        ConfigCacheMessage cfgFromFile = cfgCacheHelper.getCfgFromFile();
        cfgCache = cfgFromFile;
    }

    // Create a trust manager that does not validate certificate chains
    private static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    } };

    // Create all-trusting host name verifier
    private static HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static String getJSON(String url, int timeout) throws IOException {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            int status;
            Boolean useHttps = url.toLowerCase().startsWith("https");

            if (useHttps) {
                HttpsURLConnection secureConn = (HttpsURLConnection) connection;
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                secureConn.setSSLSocketFactory(sc.getSocketFactory());

                secureConn.setHostnameVerifier(allHostsValid);

                secureConn.connect();
                status = secureConn.getResponseCode();
            } else {
                connection.connect();
                status = connection.getResponseCode();
            }

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
            }

            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    private Boolean _getCredentialFromDCCS() {
        try {
            String dccsUrl = Options.DCCS.APIUrl + "v1/serviceCredentials/" + Options.DCCS.CredentialKey;
            String dccsJson = getJSON(dccsUrl, Agent.GET_DCCS_JSON_TIMEOUT);

            Gson gson = new Gson();
            DCCS dccs = gson.fromJson(dccsJson, DCCS.class);

            Options.MQTT.HostName = dccs.serviceHost;

            if (Options.UseSecure) {
                Options.MQTT.Port = dccs.credential.protocols.mqttSsl.port;
                Options.MQTT.Username = dccs.credential.protocols.mqttSsl.username;
                Options.MQTT.Password = dccs.credential.protocols.mqttSsl.password;
            } else {
                Options.MQTT.Port = dccs.credential.protocols.mqtt.port;
                Options.MQTT.Username = dccs.credential.protocols.mqtt.username;
                Options.MQTT.Password = dccs.credential.protocols.mqtt.password;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private MqttConnectOptions getOptions() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMaxInflight(Options.MaxInflight);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);
            options.setAutomaticReconnect(false);

            // mqtt lib v1.2.0 not support setMaxReconnectDelay, it support from v1.2.1, but
            // for mqtt secure conn issue, we need to use v1.2.0
            // options.setMaxReconnectDelay(Options.ReconnectInterval);

            if (Options.MQTT.Username != "") {
                options.setUserName(Options.MQTT.Username);
            }
            if (Options.MQTT.Password != "") {
                options.setPassword(Options.MQTT.Password.toCharArray());
            }

            if (Options.UseSecure == true) {
                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                options.setSocketFactory(sc.getSocketFactory());

                // Install the all-trusting host verifier
                options.setSSLHostnameVerifier(allHostsValid);

                // if we use mqtt lib v1.2.1 or upper, we need this function
                // options.setHttpsHostnameVerificationEnabled(false);
            }

            String willTopic = String.format(Topic.NodeConnTopic, Options.NodeId);
            String lastWillPayload = new Gson().toJson(new LastWillMessage());

            options.setWill(willTopic, lastWillPayload.getBytes(), QosMapping.AtLeastOnce, true);
            return options;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    private void _connect(Boolean _reconnect) {
        final Boolean __reconnect = _reconnect;

        MqttCallback mMqttCallback = new MqttCallbackExtended() {
            @Override
            public void connectionLost(Throwable cause) {
                agentListener.Disconnected(EdgeAgent.this, new DisconnectedEventArgs(true, cause));

                // stop heartbeat timer
                if (heartbeatTimer != null) {
                    heartbeatTimer.cancel();
                }

                // stop datarecover timer
                if (dataRecoverTimer != null) {
                    dataRecoverTimer.cancel();
                }

                try {
                    mqttClient.close();
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mqttClient = null;

                if (Options.AutoReconnect == true) {
                    _connect(__reconnect);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try {
                    String payload = new String(message.getPayload(), "UTF-8");
                    JsonObject jsonObject = new Gson().fromJson(payload, JsonObject.class);

                    if (jsonObject == null || jsonObject.has("d") == false) {
                        return;
                    }

                    if (jsonObject.get("d").getAsJsonObject().get("Cmd") != null) {
                        String cmd = jsonObject.get("d").getAsJsonObject().get("Cmd").getAsString();
                        switch (cmd) {
                            case "WV":
                                WriteValueCommand wvcMsg = new WriteValueCommand();
                                JsonObject devices = jsonObject.get("d").getAsJsonObject().get("Val").getAsJsonObject();

                                for (Map.Entry<String, JsonElement> deviceObj : devices.entrySet()) {
                                    WriteValueCommand.Device device = new WriteValueCommand.Device();
                                    device.Id = deviceObj.getKey();
                                    JsonObject tags = deviceObj.getValue().getAsJsonObject();

                                    for (Map.Entry<String, JsonElement> tagObj : tags.entrySet()) {
                                        WriteValueCommand.Tag tag = new WriteValueCommand.Tag();
                                        tag.Name = tagObj.getKey();

                                        if(tagObj.getValue().isJsonPrimitive()) {
                                            tag.Value = Helpers.primitive(tagObj.getValue().getAsJsonPrimitive());
                                        } else {
                                            tag.Value = tagObj.getValue().getAsJsonObject();
                                        }

                                        device.TagList.add(tag);
                                    }
                                    wvcMsg.DeviceList.add(device);
                                }

                                agentListener.MessageReceived(EdgeAgent.this,
                                        new MessageReceivedEventArgs(MessageType.WriteValue, wvcMsg));
                                break;
                            case "TSyn":
                                int addedTs = jsonObject.get("d").getAsJsonObject().get("UTC").getAsInt();
                                TimeSyncCommand tscMsg = new TimeSyncCommand();

                                Date miniDateTime = new Date(0);
                                tscMsg.UTCTime = new Date(miniDateTime.getTime() + (addedTs * 1000L));

                                agentListener.MessageReceived(EdgeAgent.this,
                                        new MessageReceivedEventArgs(MessageType.TimeSync, tscMsg));
                                break;
                        }
                    } else if (jsonObject.get("d").getAsJsonObject().get("Cfg") != null) {
                        ConfigAck ackMsg = new ConfigAck();
                        ackMsg.Result = BooleanUtils
                                .toBoolean(jsonObject.get("d").getAsJsonObject().get("Cfg").getAsInt());

                        agentListener.MessageReceived(EdgeAgent.this,
                                new MessageReceivedEventArgs(MessageType.ConfigAck, ackMsg));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                try {
                    // init the topics with params
                    if (EdgeAgent.this.Options.NodeId.isEmpty() == false) {
                        _configTopic = String.format(Topic.ConfigTopic, EdgeAgent.this.Options.NodeId);
                        _dataTopic = String.format(Topic.DataTopic, EdgeAgent.this.Options.NodeId);
                        _nodeConnTopic = String.format(Topic.NodeConnTopic, EdgeAgent.this.Options.NodeId);
                        _ackTopic = String.format(Topic.AckTopic, EdgeAgent.this.Options.NodeId);

                        if (EdgeAgent.this.Options.Type == EdgeType.Gateway) {
                            _cmdTopic = String.format(Topic.NodeCmdTopic, EdgeAgent.this.Options.NodeId);
                        } else {
                            _cmdTopic = String.format(Topic.DeviceCmdTopic, EdgeAgent.this.Options.NodeId,
                                    EdgeAgent.this.Options.DeviceId);
                        }
                    }

                    // subscribe
                    mqttClient.subscribe(_cmdTopic, QosMapping.AtLeastOnce);
                    mqttClient.subscribe(_ackTopic, QosMapping.AtLeastOnce);

                    // publish
                    ConnectMessage connectMsg = new ConnectMessage();
                    String payload = new Gson().toJson(connectMsg);
                    String topic = (EdgeAgent.this.Options.Type == EdgeType.Gateway) ? _nodeConnTopic
                            : _deviceConnTopic;
                    MqttMessage msg = new MqttMessage();
                    msg.setQos(QosMapping.AtLeastOnce);
                    msg.setRetained(true);
                    msg.setPayload(payload.getBytes());
                    mqttClient.publish(topic, msg);

                    // start heartbeat
                    if (EdgeAgent.this.Options.Heartbeat > 0) {
                        heartbeatTimer = new Timer();
                        heartbeatTask = new HeartbeatTask();
                        heartbeatTimer.schedule(heartbeatTask, 0, Options.Heartbeat);
                    }

                    // start datarecover
                    if (Options.DataRecover) {
                        dataRecoverTimer = new Timer();
                        dataRecoverTask = new DataRecoverTask();
                        dataRecoverTimer.schedule(dataRecoverTask, 0, DataRecover.DEAFAULT_DATARECOVER_INTERVAL);
                    }

                    agentListener.Connected(EdgeAgent.this, new EdgeAgentConnectedEventArgs(true));
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        };

        IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                try {
                    if (Options.AutoReconnect == true) {
                        if (__reconnect == true) {
                            Thread.sleep(Const.Agent.RECONNECT_INTERVAL);
                        }
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                try {
                    logger.log(Level.WARNING, exception.getMessage(), exception);

                    Thread.sleep(Const.Agent.RECONNECT_INTERVAL);

                    // destroy current conn thread
                    mqttClient.close();
                    mqttClient = null;

                    if (Options.AutoReconnect == true) {
                        _connect(__reconnect);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            if (mqttClient != null && _mqttConnected()) {
                return;
            }

            if (Options == null) {
                return;
            }

            if (Options.ConnectType == ConnectType.DCCS) {
                _getCredentialFromDCCS();
            }
            UUID uuid = UUID.randomUUID();
            String clientId = "EdgeAgent_" + uuid.toString().replace("-", "");

            String protocol;
            switch (Options.MQTT.ProtocolType) {
                case TCP:
                    if (Options.UseSecure) {
                        protocol = "ssl://";
                    } else {
                        protocol = "tcp://";
                    }
                    break;
                default:
                    if (Options.UseSecure) {
                        protocol = "ssl://";
                    } else {
                        protocol = "tcp://";
                    }
                    break;
            }
            mqttClient = new MqttAsyncClient(protocol + this.Options.MQTT.HostName + ":" + this.Options.MQTT.Port,
                    clientId, new MemoryPersistence());
            mqttClient.setCallback(mMqttCallback);
            mqttClient.connect(getOptions(), null, mIMqttActionListener);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void _disconnect() {
        IMqttActionListener discListener = new IMqttActionListener() {
            public void onSuccess(IMqttToken asyncActionToken) {
                agentListener.Disconnected(EdgeAgent.this, new DisconnectedEventArgs(true, null));
                try {
                    mqttClient.close(true);
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                agentListener.Disconnected(EdgeAgent.this, new DisconnectedEventArgs(true, null));
                try {
                    mqttClient.disconnectForcibly();
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };

        try {
            if (mqttClient == null) {
                return;
            }

            if (mqttClient != null && _mqttConnected() == false) {
                return;
            }

            // publish
            DisconnectMessage connectMsg = new DisconnectMessage();
            String payload = new Gson().toJson(connectMsg);
            String topic = (EdgeAgent.this.Options.Type == EdgeType.Gateway) ? _nodeConnTopic : _deviceConnTopic;
            MqttMessage msg = new MqttMessage();
            msg.setQos(QosMapping.AtLeastOnce);
            msg.setRetained(true);
            msg.setPayload(payload.getBytes());
            IMqttDeliveryToken token = EdgeAgent.this.mqttClient.publish(topic, msg);
            token.waitForCompletion(5000);

            // stop heartbeat timer
            if (heartbeatTimer != null) {
                heartbeatTimer.cancel();
            }

            // stop dataRecoverTimer
            if (dataRecoverTimer != null) {
                dataRecoverTimer.cancel();
            }

            mqttClient.disconnect(this, discListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private Boolean _uploadConfig(Integer action, EdgeConfig edgeConfig) {
        try {
            if (_mqttConnected() == false) {
                return false;
            }

            if (edgeConfig == null) {
                return false;
            }

            // Convert config to msg
            StringBuilder payloadObj = new StringBuilder().append("");
            Boolean result = false;

            switch (action) {
                case ActionType.Create:
                case ActionType.Update:
                case ActionType.Delsert:
                    result = Converter.ConvertWholeConfig(action, Options.NodeId, edgeConfig, payloadObj,
                            Options.Heartbeat);
                    break;
                case ActionType.Delete:
                    result = Converter.ConvertDeleteConfig(Options.NodeId, edgeConfig, payloadObj);
                    break;

            }

            if (result) {
                String payload = payloadObj.toString();

                // Config Cache
                if (action == ActionType.Create || action == ActionType.Delsert) {
                    cfgCacheHelper.addCfgToMemory(payload, this);
                    cfgCacheHelper.addCfgToFile(payload);
                }

                // Publish msg
                String topic = _configTopic;

                MqttMessage msg = new MqttMessage();
                msg.setQos(QosMapping.AtLeastOnce);
                msg.setRetained(false);
                msg.setPayload(payload.getBytes());
                mqttClient.publish(topic, msg);
            }

            return result;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    private Boolean _sendData(EdgeData data) {
        try {
            if (data == null) {
                return false;
            }
            ArrayList<String> payloads = new ArrayList<String>();

            Boolean result = Converter.ConvertData(data, payloads, this);

            if (result) {
                if (_mqttConnected() == false) {
                    if (_recoverHelper != null) {
                        final ArrayList<String> _payloads = payloads;
                        new Thread(new Runnable() {
                            public void run() {
                                _recoverHelper.Write(_payloads);
                            }
                        }).start();
                    }
                    return false;
                }

                for (String payload : payloads) {
                    String topic = _dataTopic;

                    MqttMessage msg = new MqttMessage();
                    msg.setQos(QosMapping.AtLeastOnce);
                    msg.setRetained(false);
                    msg.setPayload(payload.getBytes());
                    mqttClient.publish(topic, msg);
                }
            }

            return result;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    private Boolean _sendDeviceStatus(EdgeDeviceStatus deviceStatus) {
        try {
            if (_mqttConnected() == false) {
                return false;
            }

            if (deviceStatus == null) {
                return false;
            }
            StringBuilder payloadObj = new StringBuilder().append("");
            Boolean result = Converter.ConvertDeviceStatus(deviceStatus, payloadObj);

            if (result) {
                String topic = _nodeConnTopic;
                String payload = payloadObj.toString();

                MqttMessage msg = new MqttMessage();
                msg.setQos(QosMapping.AtLeastOnce);
                msg.setRetained(true);
                msg.setPayload(payload.getBytes());
                mqttClient.publish(topic, msg);
            }

            return result;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    private Boolean _mqttConnected() {
        if (mqttClient == null) {
            return false;
        } else {
            return mqttClient.isConnected();
        }
    }

    // public methods
    public void Connect() {
        this._connect(false);
    }

    public void Disconnect() {
        this._disconnect();
    }

    public Boolean UploadConfig(Integer action, EdgeConfig edgeConfig) {
        return this._uploadConfig(action, edgeConfig);
    }

    public Boolean SendData(EdgeData data) {
        return this._sendData(data);
    }

    public Boolean SendDeviceStatus(EdgeDeviceStatus deviceStatus) {
        return this._sendDeviceStatus(deviceStatus);
    }

    public Boolean IsConnected() {
        return this._mqttConnected();
    }
}
