package com.cliff.aws.blogen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * This CognitoConfig is for non-development cases. It assumes you have
 * created your Cognito and DynamoDB resources separately, and then configured
 * their IDs in application.properties
 */
@Slf4j
@Component
@Profile({"!dev"})
public class ProductionCognitoConfig implements CognitoConfig {

    @Value("${blogen.security.jwt.aws.userPoolId:}")
    private String userPoolId;

    @Value("${blogen.security.jwt.aws.identityPoolId:}")
    private String identityPoolId;

    @Value("${blogen.security.jwt.aws.region:us-east-1}")
    private String region;

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
        log.debug("cognito userPoolId set to {}", userPoolId);
    }

    public String getIdentityPoolId() {
        return identityPoolId;
    }

    public void setIdentityPoolId(String identityPoolId) {
        this.identityPoolId = identityPoolId;
        log.debug("cognito identityPoolId set to {}", identityPoolId);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
        log.debug("cognito region set to {}", region);
    }

    public ProductionCognitoConfig() {
        log.debug("COGNITO REGION " + this.region);
        log.debug("COGNITO USER POOL ID: " + this.userPoolId);
        log.debug("COGNITO IDENTITY POOL ID: " + this.identityPoolId);
    }
}
