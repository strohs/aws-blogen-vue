package com.cliff.aws.blogen.repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.cliff.aws.blogen.domain.Blogen;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This repository contains methods to query and scan the Blogen DynamoDB table. These are methods that couldn't
 * be expressed using the Spring-Data-DynamoDB libraries or that needed some optimization to use specific indices
 *
 * Author: Cliff
 */
@Slf4j
public class BlogenCustomRepositoryImpl implements BlogenCustomRepository {

    private DynamoDBTemplate dynamoDBTemplate;
    private DynamoDBMapper mapper;
    private DynamoDBMapperConfig mapperConfig;
    private AmazonDynamoDB dbClient;

    private ExecutorService executorService = Executors.newWorkStealingPool();

    @Autowired
    public BlogenCustomRepositoryImpl( DynamoDBTemplate dynamoDBTemplate, DynamoDBMapper mapper,
                                       DynamoDBMapperConfig mapperConfig, AmazonDynamoDB dbClient ) {
        this.dynamoDBTemplate = dynamoDBTemplate;
        this.mapper = mapper;
        this.mapperConfig = mapperConfig;
        this.dbClient = dbClient;
    }

    @Override
    public Set<String> findAllAvatarFileNames() {
        SortedSet<String> fileNames = new TreeSet<>();
        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put( ":primaryRange", new AttributeValue( Blogen.RANGE_AVATAR) );

        DynamoDBQueryExpression<Blogen> qexp = new DynamoDBQueryExpression<Blogen>()
                .withIndexName("rangeIndex")
                .withConsistentRead(false)
                .withExpressionAttributeValues(valMap)
                .withKeyConditionExpression("primaryRange = :primaryRange");

        PaginatedQueryList<Blogen> blogens = dynamoDBTemplate.query( Blogen.class, qexp );
        blogens.forEach( blogen -> fileNames.add( blogen.getPrimaryHash() ));
        return fileNames;
    }

    @Override
    // a Blogen starting thread is indicated by a threadId equal to the postId
    public Optional<Blogen> fetchThreadStart( String threadId ) {
        Optional<Blogen> result = Optional.empty();
        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put( ":threadId", new AttributeValue( threadId ) );

        DynamoDBQueryExpression<Blogen> qexp = new DynamoDBQueryExpression<Blogen>()
                .withExpressionAttributeValues( valMap )
                .withKeyConditionExpression( "primaryHash = :threadId" )
                .withFilterExpression( "postId = :threadId" );
        PaginatedQueryList<Blogen> blogens = dynamoDBTemplate.query( Blogen.class, qexp );
        for ( Blogen b : blogens ) {
            result = Optional.of( b );
            break;
        }
        return result;
    }

    //todo expensive table scan, possibly add separate table or separate hash/range for recent posts
    @Override
    // this will fetch all recent threads ordered by the creationTimestamp in the primaryRange attribute
    public List<Blogen> fetchAllRecentThreads() {
        List<Blogen> results = new ArrayList<>();
        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put( ":postPrefix", new AttributeValue( Blogen.RANGE_POST ));
        DynamoDBScanExpression exp = new DynamoDBScanExpression()
                .withExpressionAttributeValues( valMap )
                .withConsistentRead(false)
                .withIndexName("rangeIndex")
                .withFilterExpression( "begins_with(primaryRange, :postPrefix) AND primaryHash = postId" );
        
        PaginatedScanList<Blogen> blogens = dynamoDBTemplate.scan( Blogen.class, exp );
        blogens.forEach( blogen -> results.add(blogen) );
        return results;
    }


    //todo expensive table scan, does a count scan, than fetches multiple threads
    @Override
    public Page<List<Blogen>> fetchRecentThreads( Pageable pageable ) {

        Map<String, AttributeValue> valMap = new HashMap<>();
        //valMap.put( ":now", new AttributeValue( rangeStr ) );
        valMap.put( ":postPrefix", new AttributeValue( Blogen.RANGE_POST));

        // 1. get a scan of recent threads
        ScanRequest scanRequest = new ScanRequest()
                .withTableName( Blogen.class.getAnnotation( DynamoDBTable.class ).tableName() )
                .withConsistentRead( false )
                .withExpressionAttributeValues( valMap )
                .withIndexName("rangeIndex")
                .withFilterExpression( "begins_with(primaryRange, :postPrefix) AND primaryHash = postId" );

        ScanResult result = dbClient.scan(scanRequest);

        return buildPageOfThreadsWithPosts( result.getItems(), result.getCount(), pageable );
    }

    @Override
    public Page<List<Blogen>> fetchThreadsByCategoryName( String catName, Pageable pageable ) {

        Map<String,AttributeValue> valMap = new HashMap<>();
        valMap.put( ":categoryName", new AttributeValue( catName ) );

        QueryRequest queryRequest = new QueryRequest()
                .withTableName( Blogen.class.getAnnotation( DynamoDBTable.class ).tableName() )
                .withExpressionAttributeValues( valMap )
                .withIndexName( "categoryNameIndex" )
                .withConsistentRead( false )
                .withKeyConditionExpression( "categoryName = :categoryName" )
                .withFilterExpression( "threadId = postId" );
        QueryResult queryResult = dbClient.query( queryRequest );

        return buildPageOfThreadsWithPosts( queryResult.getItems(), queryResult.getCount(), pageable );
    }

    @Override
    public List<Blogen> fetchAllPostsByCategoryName( String catName ) {
        List<Blogen> posts = new ArrayList<>();
        Map<String,AttributeValue> valMap = new HashMap<>();
        valMap.put( ":categoryName", new AttributeValue( catName ) );
        valMap.put( ":postPrefix", new AttributeValue( Blogen.RANGE_POST));

        DynamoDBQueryExpression<Blogen> qexp = new DynamoDBQueryExpression<Blogen>()
                .withExpressionAttributeValues( valMap )
                .withIndexName( "categoryNameIndex" )
                .withConsistentRead( false )
                .withKeyConditionExpression( "categoryName = :categoryName AND begins_with(primaryRange, :postPrefix)" );
        
        PaginatedQueryList<Blogen> blogens = dynamoDBTemplate.query( Blogen.class, qexp );
        blogens.forEach( posts::add );

        return posts;
    }

    @Override
    public Page<List<Blogen>> fetchThreadsByUserId( String userId, Pageable pageable ) {

        Map<String,AttributeValue> valMap = new HashMap<>();
        valMap.put( ":userId", new AttributeValue( userId ) );

        QueryRequest queryRequest = new QueryRequest()
                .withTableName( Blogen.class.getAnnotation( DynamoDBTable.class ).tableName() )
                .withExpressionAttributeValues( valMap )
                .withIndexName( "userIdIndex" )
                .withConsistentRead( false )
                .withKeyConditionExpression( "userId = :userId" )
                .withFilterExpression( "threadId = postId" );
        QueryResult queryResult = dbClient.query( queryRequest );

        return buildPageOfThreadsWithPosts( queryResult.getItems(), queryResult.getCount(), pageable );
    }

    @Override
    public Page<List<Blogen>> fetchThreadsByUserIdAndCategoryName( String userId, String categoryName, Pageable pageable ) {

        Map<String,AttributeValue> valMap = new HashMap<>();
        valMap.put( ":userId", new AttributeValue( userId ) );
        valMap.put( ":categoryName", new AttributeValue( categoryName ) );

        QueryRequest queryRequest = new QueryRequest()
                .withTableName( Blogen.class.getAnnotation( DynamoDBTable.class ).tableName() )
                .withExpressionAttributeValues( valMap )
                .withIndexName( "userIdIndex" )
                .withConsistentRead( false )
                .withKeyConditionExpression( "userId = :userId" )
                .withFilterExpression( "threadId = postId AND categoryName = :categoryName" );
        QueryResult queryResult = dbClient.query( queryRequest );

        return buildPageOfThreadsWithPosts( queryResult.getItems(), queryResult.getCount(), pageable );
    }


    @Override
    public Page<Blogen> searchPostTextAndTitle( String searchStr, Pageable pageable ) {
        List<Blogen> results = new ArrayList<>();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put( "#text","text");
        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put( ":searchStr", new AttributeValue( searchStr ));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName( Blogen.class.getAnnotation( DynamoDBTable.class ).tableName() )
                .withConsistentRead( false )
                .withExpressionAttributeValues( valMap )
                .withExpressionAttributeNames( nameMap )
                .withFilterExpression( "contains(#text, :searchStr) OR contains(title, :searchStr)" );

        ScanResult result = dbClient.scan(scanRequest);
        int totalCount = result.getCount();
        List<Map<String, AttributeValue>> items = result.getItems();
        for ( int i = 0; i < result.getCount(); i++ ) {
            if( isWithinPage( i, pageable ) ) {
                Blogen b = mapper.marshallIntoObject( Blogen.class, items.get( i ), mapperConfig );
                results.add( b );
            }
        }
        return new PageImpl<Blogen>(results, pageable, totalCount);
    }

    @Override
    public void update( Blogen blogen, DynamoDBMapperConfig.SaveBehavior saveBehavior ) {
        DynamoDBMapperConfig config = DynamoDBMapperConfig
                .builder()
                .withSaveBehavior(saveBehavior)
                .build();
        mapper.save(blogen, config);
    }

    @Override
    public void updateItems( List<Blogen> blogens, DynamoDBMapperConfig.SaveBehavior saveBehavior ) {
        DynamoDBMapperConfig config = DynamoDBMapperConfig
                .builder()
                .withSaveBehavior(saveBehavior)
                .build();

        blogens.forEach(blogen -> {
            executorService.submit(() -> mapper.save(blogen, config) );
        });
    }

    private Page<List<Blogen>> buildPageOfThreadsWithPosts( List<Map<String,AttributeValue>> itemsMap,
                                                            int totalThreads,
                                                            Pageable pageable) {
        List<List<Blogen>> postList = new ArrayList<>();
        // sort the threads in items map by their creation timestamp which is contained within primaryRange
        sortThreadsByCreatedTimestamp( itemsMap, byPrimaryRangeDesc );
        for ( int i = 0; i < totalThreads; i++ ) {
            String threadId = itemsMap.get( i ).get( "threadId" ).getS();
            if ( isWithinPage( i, pageable ) ) {
                List<Blogen> posts = fetchPosts( threadId );
                postList.add( posts );
            }
        }
        Page<List<Blogen>> page = new PageImpl<List<Blogen>>( postList, pageable, totalThreads );
        return page;
    }

    // fetch all posts belonging to the thread with the threadId. The 'start', or first post of the thread
    // will be at index 0 of the list
    private List<Blogen> fetchPosts(String threadId) {
        List<Blogen> results = new ArrayList<>();
        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put( ":threadId", new AttributeValue( threadId ));
        DynamoDBQueryExpression<Blogen> exp = new DynamoDBQueryExpression<Blogen>()
                .withKeyConditionExpression( "primaryHash = :threadId" )
                .withExpressionAttributeValues( valMap )
                .withScanIndexForward(true)
                .withConsistentRead( false );

        PaginatedQueryList<Blogen> pql = mapper.query( Blogen.class, exp, mapperConfig );
        pql.forEach( results::add );
        return results;
    }

    /**
     * returns true if the index is within the current boundaries described by the pageable
     * @param index
     * @param pageable
     * @return
     */
    private boolean isWithinPage(int index, Pageable pageable) {
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = startIndex + pageable.getPageSize();
        return ( index >= startIndex && index < endIndex );
    }

    private void sortThreadsByCreatedTimestamp( List<Map<String,AttributeValue>> itemsMap,
                                                Comparator<Map<String,AttributeValue>> comparator ) {
        itemsMap.sort( comparator );
    }

    private Comparator<Map<String,AttributeValue>> byPrimaryRangeAsc = ( Map<String,AttributeValue> m1,
                                                                         Map<String,AttributeValue> m2) -> {
        String ts1 = m1.get("primaryRange").getS();
        String ts2 = m2.get("primaryRange").getS();
        return ts1.compareTo(ts2);
    };

    private Comparator<Map<String,AttributeValue>> byPrimaryRangeDesc = ( Map<String,AttributeValue> m1,
                                                                          Map<String,AttributeValue> m2) -> {
        String ts1 = m1.get("primaryRange").getS();
        String ts2 = m2.get("primaryRange").getS();
        return ts2.compareTo(ts1);
    };

}
