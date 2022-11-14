package wisepaas.datahub.java.sdk.model.edge;

public class DCCSOptions {
    public String CredentialKey;
    public String APIUrl;

    public DCCSOptions() {
        this.CredentialKey = "";
        this.APIUrl = "";
    }

    public DCCSOptions(String credentialKey, String apiUrl) {
        this.CredentialKey = credentialKey;
        this.APIUrl = apiUrl;
    }
}