package com.cliff.aws.blogen.config;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * DynamoDB configuration
 * If the awsAccessKey and awsSecretKey are configured in a application.properties file,
 * they will override any keys configured in the AWS default provider chain.
 * Default provider chain search order is described here:
 *   <a src=https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html>DefaultProviderChain</a>
 *
 */
@Configuration
@Slf4j
@EnableDynamoDBRepositories
        (basePackages = "com.cliff.aws.blogen.repositories")
public class DynamoDBConfig {

    @Value("${aws.dynamodb-local.port:8000}")
    private String dynamoDBLocalPort;

    @Value("${aws.signing.region:us-east-1}")
    private String awsSigningRegion;

    private final AWSCredentialsProvider credentialsProvider;

    @Autowired
    public DynamoDBConfig(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
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
                .withCredentials( this.credentialsProvider )
                .withRegion( awsSigningRegion )
                .build();
        return amazonDynamoDB;
    }

    @Bean("amazonDynamoDB")
    @Profile("dynamodb-local")
    public AmazonDynamoDB localDynamoDB(DynamoDBLocal dynamoDBLocal) {
        String dynamoDBEndpoint = "http://localhost:" + dynamoDBLocalPort + "/";
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials( this.credentialsProvider )
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
