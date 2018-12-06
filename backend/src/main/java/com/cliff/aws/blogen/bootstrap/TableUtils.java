package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.*;
import com.cliff.aws.blogen.domain.Blogen;
import lombok.extern.slf4j.Slf4j;

/**
 * Methods to create and delete DynamoDB Tables
 *
 * Author: Cliff
 */
@Slf4j
public class TableUtils {

    /**
     * creates a Blogen table using a {@link Blogen} domain object
     *
     * @return CreateTableRequest for creating the Blogen table
     */
    public static CreateTableRequest createBlogenTableRequest( DynamoDBMapper dbMapper,
                                                        DynamoDBMapperConfig dbMapperConfig,
                                                        Long rcu, Long wcu) {
        ProvisionedThroughput base_tp = new ProvisionedThroughput( rcu, wcu );
        ProvisionedThroughput gsi_tp = new ProvisionedThroughput( rcu, wcu );

        CreateTableRequest tableRequest = dbMapper.generateCreateTableRequest( Blogen.class, dbMapperConfig );

        tableRequest.withProvisionedThroughput( base_tp );
        // set through-puts and projections for all GSIs
        tableRequest.getGlobalSecondaryIndexes()
                .forEach( gsi -> {
                    gsi.setProvisionedThroughput( gsi_tp );
                    if (gsi.getIndexName().equals( "rangeIndex" ))
                        gsi.setProjection( new Projection().withProjectionType( ProjectionType.ALL ) );
                    if (gsi.getIndexName().equals( "categoryNameIndex" )) {
                        gsi.setProjection(
                                new Projection()
                                        .withProjectionType( ProjectionType.INCLUDE )
                                        .withNonKeyAttributes( "threadId","postId","userId" ) );
                    }
                    if (gsi.getIndexName().equals( "userIdIndex" )) {
                        gsi.setProjection(
                                new Projection()
                                        .withProjectionType( ProjectionType.INCLUDE )
                                        .withNonKeyAttributes( "threadId","postId","categoryName" ) );
                    }
                } );
        return tableRequest;
    }

    /**
     * Creates a table if it doesn't already exist
     * @param dynamoDB dynamodb client
     * @param createTableRequest contains the table creation specifications
     * @param deleteTableIfExists set this to true to delete the table first, and then create it
     * @param waitForTable set this to true if you want to wait for the table to become active/deleted, this is
     *                             useful if you are working against a live (web based) dynamodb residing within
     *                             AWS. Set this to false if you want to skip the wait, or you are working with
     *                             DynamoDB local
     */
    public static void checkOrCreateTable(AmazonDynamoDB dynamoDB,
                                          CreateTableRequest createTableRequest,
                                          boolean deleteTableIfExists,
                                          boolean waitForTable) {


        String tableName = createTableRequest.getTableName();
        try {
            dynamoDB.describeTable(tableName);
            if ( deleteTableIfExists ) {
                log.info("Table {} exists, deleting....", tableName);
                deleteTable( dynamoDB, tableName, waitForTable );
            }
            log.info("Table {} exists...skipping table creation", tableName);
            return;
        } catch ( ResourceNotFoundException rnfe) {
            log.info("Table {} doesn't exist - Creating", tableName);
        }
        dynamoDB.createTable( createTableRequest );
        if ( waitForTable )
            waitForDynamoDBTable(dynamoDB, tableName, TableStatus.ACTIVE );
        DescribeTableResult describeTableResult = dynamoDB.describeTable( tableName );
        log.info("Table Description: ", describeTableResult.toString() );
    }

    public static void waitForDynamoDBTable(AmazonDynamoDB dynamoDB, String tableName, TableStatus tableStatus) {
        do {
            try {
                log.info( "waiting 5 seconds for table......" );
                Thread.sleep(5 * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException("Couldn't wait detect table " + tableName);
            }
        } while (!dynamoDB.describeTable(tableName).getTable().getTableStatus()
                .equals( tableStatus.name() ));
        log.info( "Done... table {} is now {}", tableName, tableStatus.name() );
    }

    public static void deleteTable( AmazonDynamoDB dynamoDB, String tableName, boolean waitForTable ) {
        // delete the table if it exists
        dynamoDB.deleteTable( tableName );
        if (waitForTable)
            waitForDynamoDBTable( dynamoDB, tableName, TableStatus.DELETING );
    }

    public static boolean tableExists( AmazonDynamoDB dynamoDB, String tableName ) {
        try {
            DescribeTableResult describeTableResult = dynamoDB.describeTable( tableName );
            return true;
        } catch ( ResourceNotFoundException rne ) {
            return false;
        }
    }
}
