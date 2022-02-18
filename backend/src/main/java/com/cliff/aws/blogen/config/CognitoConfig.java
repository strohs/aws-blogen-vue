package com.cliff.aws.blogen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class holds the current Cognito userPoolId, identityPoolId, and
 * region configuration. Initially these values are pulled from application.properties
 * but they can be overridden if using the "dev" profile, which bootstraps a new
 * user pool and identity pool each time it is run
 */
@Configuration
@Slf4j
public class CognitoConfig {

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
}
