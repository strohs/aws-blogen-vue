
package com.cliff.aws.lambda.blogen.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CognitoEvent {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("userPoolId")
    @Expose
    private String userPoolId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("callerContext")
    @Expose
    private CallerContext callerContext;
    @SerializedName("triggerSource")
    @Expose
    private String triggerSource;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("response")
    @Expose
    private Response response;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public CallerContext getCallerContext() {
        return callerContext;
    }

    public void setCallerContext(CallerContext callerContext) {
        this.callerContext = callerContext;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource) {
        this.triggerSource = triggerSource;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
