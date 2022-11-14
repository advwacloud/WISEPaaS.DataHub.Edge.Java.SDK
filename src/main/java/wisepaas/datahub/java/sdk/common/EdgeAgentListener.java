package wisepaas.datahub.java.sdk.common;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.model.event.*;

public interface EdgeAgentListener {
    void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args);

    void Disconnected(EdgeAgent agent, DisconnectedEventArgs args);

    void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs args);
}