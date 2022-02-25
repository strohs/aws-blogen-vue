package com.cliff.aws.blogen.config;

import com.cliff.aws.blogen.bootstrap.Bootstrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 *  This implementation of CognitoConfig is designed to hold the dynamically bootstrapped
 *  Cognito configuration(s). If the "blogen.bootstrap.data" property is set equal to true, then
 *  it will use the Bootstrapper class to create cognito resources AND to create "dummy" DynamoDB data.
 */
@Component
@Slf4j
@Profile("dev")
public class BootstrappedCognitoConfig implements CognitoConfig {

    private String userPoolId;

    private String identityPoolId;

    private String appClientId;

    private String region;

    private Bootstrapper bootstrapper;

    public String getUserPoolId() {
        return this.userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
        log.debug("cognito userPoolId set to {}", userPoolId);
    }

    public String getIdentityPoolId() {
        return this.identityPoolId;
    }

    public void setIdentityPoolId(String identityPoolId) {
        this.identityPoolId = identityPoolId;
        log.debug("cognito identityPoolId set to {}", identityPoolId);
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
        log.debug("cognito region set to {}", region);
    }

    public String getAppClientId() {
        return appClientId;
    }

    @Autowired
    public BootstrappedCognitoConfig(
            @Value("${blogen.security.jwt.aws.region:us-east-1}") String region,
            Bootstrapper bootstrapper
    ) {
        this.region = region;
        this.bootstrapper = bootstrapper;

        // Bootstrap ze data!!
        this.bootstrapper.bootstrap();

        this.userPoolId = bootstrapper.getUserPoolBootstrapper().getUserPoolId();
        this.identityPoolId = bootstrapper.getIdentityPoolBootstrapper().getIdentityPoolId();
        this.appClientId = bootstrapper.getUserPoolBootstrapper().getAppClientId();

        log.info("COGNITO REGION " + this.getRegion());
        log.info("COGNITO USER POOL ID: " + this.getUserPoolId());
        log.info("COGNITO IDENTITY POOL ID: " + this.getIdentityPoolId());
        log.info("COGNITO APPLICATION CLIENT ID: " + this.getAppClientId());
    }
}
