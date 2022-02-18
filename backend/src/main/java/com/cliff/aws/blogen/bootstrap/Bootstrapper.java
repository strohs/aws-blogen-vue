package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.cliff.aws.blogen.config.CognitoConfig;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import com.cliff.aws.blogen.repositories.BlogenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * Bootstrap the blogen embedded JPA database with data
 *
 *  @author Cliff
 */
@Slf4j
@Component
@Profile({"dev"})
public class Bootstrapper implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${blogen.bootstrap.data:false}")
    private boolean shouldBootstrap;

    private final CognitoConfig cognitoConfig;
    private final DynamoDbBootstrapper dynamoDbBootstrapper;
    private final UserPoolBootstrapper userPoolBootstrapper;
    private final IdentityPoolBootstrapper identityPoolBootstrapper;

    @Autowired
    public Bootstrapper( CognitoConfig cognitoConfig,
                         UserPoolBootstrapper userPoolBootstrapper,
                         IdentityPoolBootstrapper identityPoolBootstrapper,
                         DynamoDbBootstrapper dynamoDbBootstrapper) {

        this.cognitoConfig = cognitoConfig;
        this.userPoolBootstrapper = userPoolBootstrapper;
        this.identityPoolBootstrapper = identityPoolBootstrapper;
        this.dynamoDbBootstrapper = dynamoDbBootstrapper;
    }


    @Override
    public void onApplicationEvent( ContextRefreshedEvent event ) {
        if ( shouldBootstrap ) {
            // bootstrap cognito data
            userPoolBootstrapper.bootstrap();

            // bootstrap identity pool
            identityPoolBootstrapper.bootstrap(
                    cognitoConfig.getRegion(),
                    userPoolBootstrapper.userPoolId,
                    userPoolBootstrapper.userPoolArn,
                    userPoolBootstrapper.appClientId);

            // bootstrap a dynamodb table and fill it with sample data
            dynamoDbBootstrapper.bootstrap();

            log.info("cognito region is: {}", this.cognitoConfig.getRegion());
            log.info("cognito user pool id is: {}", this.cognitoConfig.getUserPoolId());
            log.info("cognito identity pool id is: {}", this.cognitoConfig.getIdentityPoolId());
            log.info("cognito web application client id is: {}", this.userPoolBootstrapper.appClientId);
        } else {
            log.info("skipping sample data bootstrap");
        }
    }
}
