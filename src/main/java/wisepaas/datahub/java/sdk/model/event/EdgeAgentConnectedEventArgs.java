package wisepaas.datahub.java.sdk.model.event;

public class EdgeAgentConnectedEventArgs {
    private Boolean isSessionPresent;

    public EdgeAgentConnectedEventArgs(Boolean isSessionPresent) {
        this.isSessionPresent = isSessionPresent;
    }

    public Boolean getIsSessionPresent() {
        return this.isSessionPresent;
    }
}