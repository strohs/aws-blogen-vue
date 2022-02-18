package com.cliff.aws.blogen.config;

import com.amazonaws.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the AWS credentials to use for all aws-sdk calls used throughout
 * this application. If the properties: awsAccessKey  and  awsSecretKey
 * are configured in application.properties, they will be used to override
 * the accessKey and secretKey that may exist in a user's home directory, or that
 * may be set as Environment variables.
 * If these keys are not explicitly set (or set to blank) in application.properties,
 * then the AWS default provider chain will be searched. If these keys cannot be found there,
 * then this application will fail to start
 */
@Configuration
@Slf4j
public class AwsCredentialsConfig {

    @Value("${aws.accesskey:}")
    private String awsAccessKey;

    @Value("${aws.secretkey:}")
    private String awsSecretKey;

    @Bean
    public AWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials( awsAccessKey, awsSecretKey );
    }

    @Bean
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        AWSCredentialsProvider credentialsProvider;
        AWSCredentialsProvider defaultProvider = new DefaultAWSCredentialsProviderChain();
        AWSCredentialsProvider staticProvider = new AWSStaticCredentialsProvider( basicAWSCredentials() );

        //if accessKey AND secretAccessKey are configured in application.properties, then they will override the default
        // credential provider chain
        if ( !staticProvider.getCredentials().getAWSAccessKeyId().equals("") && !staticProvider.getCredentials().getAWSSecretKey().equals("") ) {
            log.info("using accessKey {} and secretKey configured in application.properties", awsAccessKey);
            credentialsProvider = new AWSCredentialsProviderChain( staticProvider, defaultProvider );
        } else {
            credentialsProvider = new AWSCredentialsProviderChain( defaultProvider );
            log.info("using accessKey {} and secretKey from the default provider chain",
                    credentialsProvider.getCredentials().getAWSAccessKeyId() );
        }
        return credentialsProvider;
    }
}
