package wisepaas.datahub.java.sdk.model.event;

public class DisconnectedEventArgs {
    private Boolean clientWasConnected;
    private Throwable cause;

    public DisconnectedEventArgs(Boolean clientWasConnected, Throwable cause) {
        this.clientWasConnected = clientWasConnected;
        this.cause = cause;
    }

    public Boolean getClientWasConnected() {
        return this.clientWasConnected;
    }

    public Throwable getCause() {
        return this.cause;
    }
}