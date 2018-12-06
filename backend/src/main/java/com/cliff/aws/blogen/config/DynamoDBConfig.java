package com.cliff.aws.blogen.config;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * DynamoDB configuration
 * If the awsAccessKey and awsSecretKey are configured in application.properties, they will override any keys
 * configured in the default provider chain.
 * Default provider chain search order is here:
 *   <a src=https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html>DefaultProviderChain</a>
 *
 * 
 * Author: Cliff
 */
@Configuration
@Slf4j
@EnableDynamoDBRepositories
        (basePackages = "com.cliff.aws.blogen.repositories")
public class DynamoDBConfig {

    @Value("${aws.dynamodb-local.port:8000}")
    private String dynamoDBLocalPort;

    @Value("${aws.accesskey:}")
    private String awsAccessKey;

    @Value("${aws.secretkey:}")
    private String awsSecretKey;

    @Value("${aws.signing.region:us-east-1}")
    private String awsSigningRegion;

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



    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        // in order to activate lazy loading of results, set PaginationLoadingStrategy to ITERATION_ONLY
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setPaginationLoadingStrategy( DynamoDBMapperConfig.PaginationLoadingStrategy.ITERATION_ONLY);

        return builder.build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper( AmazonDynamoDB dynamoDB, DynamoDBMapperConfig config) {
        return new DynamoDBMapper( dynamoDB, config);
    }

    @Bean("amazonDynamoDB")
    @Profile( "!dynamodb-local" )
    public AmazonDynamoDB productionDynamoDB() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials( amazonAWSCredentialsProvider() )
                .withRegion( awsSigningRegion )
                .build();
        return amazonDynamoDB;
    }

    @Bean("amazonDynamoDB")
    @Profile("dynamodb-local")
    public AmazonDynamoDB localDynamoDB() {
        String dynamoDBEndpoint = "http://localhost:" + dynamoDBLocalPort + "/";
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials( amazonAWSCredentialsProvider() )
                .withEndpointConfiguration( new AwsClientBuilder.EndpointConfiguration( dynamoDBEndpoint, awsSigningRegion ) )
                .build();
        return amazonDynamoDB;
    }

    @Bean
    public DynamoDBTemplate dynamoDBTemplate( AmazonDynamoDB amazonDynamoDB,
                                              DynamoDBMapperConfig dynamoDBMapperConfig,
                                              DynamoDBMapper dynamoDBMapper) {
        return new DynamoDBTemplate( amazonDynamoDB, dynamoDBMapperConfig, dynamoDBMapper );
    }

}
