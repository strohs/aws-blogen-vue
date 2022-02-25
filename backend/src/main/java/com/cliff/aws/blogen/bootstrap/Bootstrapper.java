package com.cliff.aws.blogen.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Kicks off the bootstrapping process for Cognito User Pool, Cognito Identity Pool and DynamoDB
 */
@Slf4j
@Component
@Profile("dev")
public class Bootstrapper {

    private final UserPoolBootstrapper userPoolBootstrapper;
    private final IdentityPoolBootstrapper identityPoolBootstrapper;
    private final DynamoDbBootstrapper dynamoDbBootstrapper;

    @Value("${blogen.bootstrap.data:false}")
    boolean shouldBootstrap;

    @Autowired
    public Bootstrapper(
            UserPoolBootstrapper userPoolBootstrapper,
            IdentityPoolBootstrapper identityPoolBootstrapper,
            DynamoDbBootstrapper dynamoDbBootstrapper) {
        this.userPoolBootstrapper = userPoolBootstrapper;
        this.identityPoolBootstrapper = identityPoolBootstrapper;
        this.dynamoDbBootstrapper = dynamoDbBootstrapper;
    }

    public void bootstrap() {
        if (shouldBootstrap) {
            log.info("BOOTSTRAP Started...");
            // bootstrap cognito data
            userPoolBootstrapper.bootstrap();

            // bootstrap identity pool
            identityPoolBootstrapper.bootstrap(
                    userPoolBootstrapper.getUserPoolId(),
                    userPoolBootstrapper.getUserPoolArn(),
                    userPoolBootstrapper.getAppClientId()
            );

            // bootstrap a dynamodb table and fill it with sample data
            dynamoDbBootstrapper.bootstrap();

            log.info("BOOTSTRAP Finished...");
        } else {
            log.info("BOOTSTRAP Skipped....");
        }
    }

    public UserPoolBootstrapper getUserPoolBootstrapper() {
        return userPoolBootstrapper;
    }

    public IdentityPoolBootstrapper getIdentityPoolBootstrapper() {
        return identityPoolBootstrapper;
    }

    public DynamoDbBootstrapper getDynamoDbBootstrapper() {
        return dynamoDbBootstrapper;
    }
}
