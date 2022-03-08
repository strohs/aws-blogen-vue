package com.cliff.aws.blogen.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures an AWS Cognito Identity Provider Client.
 * These clients can then be used to interact with a Cognito User Pool
 */
@Configuration
@Slf4j
public class CognitoIdpClientConfig {

    private final AWSCredentialsProvider awsCredentialsProvider;

    private final CognitoConfig cognitoConfig;

    @Autowired
    public CognitoIdpClientConfig(CognitoConfig cognitoConfig,
                                  AWSCredentialsProvider awsCredentialsProvider) {

        this.awsCredentialsProvider = awsCredentialsProvider;
        this.cognitoConfig = cognitoConfig;
    }

    @Bean
    public AWSCognitoIdentityProvider buildCognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withRegion(this.cognitoConfig.getRegion())
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}
