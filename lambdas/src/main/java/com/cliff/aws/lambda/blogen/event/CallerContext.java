
package com.cliff.aws.lambda.blogen.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallerContext {

    @SerializedName("awsSdkVersion")
    @Expose
    private String awsSdkVersion;
    @SerializedName("clientId")
    @Expose
    private String clientId;

    public String getAwsSdkVersion() {
        return awsSdkVersion;
    }

    public void setAwsSdkVersion(String awsSdkVersion) {
        this.awsSdkVersion = awsSdkVersion;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
