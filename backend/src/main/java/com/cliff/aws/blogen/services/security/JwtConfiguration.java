package com.cliff.aws.blogen.services.security;

import com.cliff.aws.blogen.config.CognitoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//@ConfigurationProperties(prefix = "blogen.security.jwt.aws")
public class JwtConfiguration {

    private static final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
    private static final String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";

    // cognito config will have the currently configured userPoolId,
    // identityPoolId, and region
    private CognitoConfig cognitoConfig;

    private String jwkUrl;
    private String userNameField = "cognito:username";
    private String groupsField = "cognito:groups";
    private int connectionTimeout = 2000;
    private int readTimeout = 2000;
    private String httpHeader = "Authorization";

    @Autowired
    public JwtConfiguration(CognitoConfig cognitoConfig) {
        this.cognitoConfig = cognitoConfig;
        log.debug("jwkUrl: " + this.getJwkUrl());
    }

    public String getJwkUrl() {
        if (jwkUrl == null || jwkUrl.isEmpty()) {
            return String.format(COGNITO_IDENTITY_POOL_URL + JSON_WEB_TOKEN_SET_URL_SUFFIX, getRegion(), getUserPoolId());
        }
        return jwkUrl;
    }

    public String getCognitoIdentityPoolUrl() {
        return String.format(COGNITO_IDENTITY_POOL_URL, getRegion(), getUserPoolId());
    }

    public void setJwkUrl(String jwkUrl) {
        this.jwkUrl = jwkUrl;
    }

    public String getUserPoolId() {
        return cognitoConfig.getUserPoolId();
    }

//    public void setUserPoolId(String userPoolId) {
//        this.userPoolId = userPoolId;
//    }

    public String getIdentityPoolId() {
        return cognitoConfig.getIdentityPoolId();
    }

//    public JwtConfiguration setIdentityPoolId(String identityPoolId) {
//        this.identityPoolId = identityPoolId;
//        return this;
//    }

    public String getRegion() {
        return cognitoConfig.getRegion();
    }

//    public void setRegion(String region) {
//        this.region = region;
//    }

    public String getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(String httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getUserNameField() {
        return userNameField;
    }

    public void setUserNameField(String userNameField) {
        this.userNameField = userNameField;
    }

    public String getGroupsField() {
        return groupsField;
    }

    public void setGroupsField(String groupsField) {
        this.groupsField = groupsField;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

}
