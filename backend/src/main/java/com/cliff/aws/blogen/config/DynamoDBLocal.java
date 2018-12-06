package com.cliff.aws.blogen.config;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Starts an instance of a DynamoDB Local server
 *
 * NOTICE make sure you run mvn test-compile at least once in order to initialize and created the sqlite4java
 * native-libs directory
 *
 * Author: Cliff
 */
@Component
@Slf4j
@Profile( "dynamodb-local" )
public class DynamoDBLocal {

    @Value( "${aws.dynamodb-local.port:8000}" )
    private String port;

    private DynamoDBProxyServer server;


    @PostConstruct
    private void createProxyServer() {
        // dynamoDB-Local needs sqlite native libs
        System.setProperty("sqlite4java.library.path", "native-libs");

        try {
            log.info( "attempting to start dynamodb-local ....." );
            this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", port});
            server.start();
            log.info( "dynamodb-local started on port {}", port );
        } catch ( Exception e ) {
            log.error("failed to start dynamodb-local: {}", e.getMessage() );
        }

    }

    @PreDestroy
    private void shutdownServer() {
        try {
            log.info( "attempting to stop dynamodb-local...." );
            server.stop();
            log.info( "dynamodb-local stopped" );
        } catch (Exception e) {
            log.error("failed to stop dynamodb-local, exception is {}", e.getMessage());
        }
    }

}
