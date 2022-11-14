package wisepaas.datahub.java.sdk.model.event;

public class MessageReceivedEventArgs {
    public Integer Type;
    public Object Message;

    public MessageReceivedEventArgs(Integer messageType, Object message) {
        this.Type = messageType;
        this.Message = message;
    }
}