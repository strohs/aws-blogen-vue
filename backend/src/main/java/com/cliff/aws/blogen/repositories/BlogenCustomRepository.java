package com.cliff.aws.blogen.repositories;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.cliff.aws.blogen.domain.Blogen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Author: Cliff
 */
public interface BlogenCustomRepository {


    /**
     * fetch the first post of a thread
     * @param threadId the threadId of the thread
     * @return Blogen object containing the details of the post
     */
    Optional<Blogen> fetchThreadStart( String threadId );

    /**
     * fetches all avatar file names currently being used
     * @return  a set containing all avatar file names
     */
    Set<String> findAllAvatarFileNames();


    /**
     * fetches ALL threads. Performs a table SCAN
     * @return a list of {@link Blogen} threads
     */
    List<Blogen> fetchAllRecentThreads();


    /**
     * get a page of Blogen Threads in descending order of creation
     * @return a Page of Lists where each list represents a thread and all the thread's child posts
     */
    Page<List<Blogen>> fetchRecentThreads( Pageable pageable );

    /**
     * get a page of threads (with posts) belonging to the specified category
     * @param pageable
     * @return a Page of posts having the specified category
     */
    Page<List<Blogen>> fetchThreadsByCategoryName( String catName, Pageable pageable );

    /**
     * get all threads (with posts) for the specified userId, using the pageable to control the page of results returned
     * @param userId
     * @param pageable
     * @return a page of threads made by the specified user id
     */
    Page<List<Blogen>> fetchThreadsByUserId( String userId, Pageable pageable );

    /**
     * get a page of parent posts for the specified userId AND having the specified categoryId
     * @param userId user id to search for in posts
     * @param categoryName category name of the post
     * @param pageable Pageable details
     * @return a Page containing Lists of Blogen objects. Each list represent a thread and its associated posts
     */
    Page<List<Blogen>> fetchThreadsByUserIdAndCategoryName( String userId, String categoryName, Pageable pageable );

    /**
     * searches for searchStr in the text or title of a Post. This is a brute force search, and it is case sensitive
     * @param searchStr - the substring to search for in post.text or post.title
     * @return {@link Page} containing Posts matching the searchStr
     */
    Page<Blogen> searchPostTextAndTitle( String searchStr, Pageable pageable );

    List<Blogen> fetchAllPostsByCategoryName( String catName );

    void update( Blogen blogen, DynamoDBMapperConfig.SaveBehavior saveBehavior );

    void updateItems( List<Blogen> blogens, DynamoDBMapperConfig.SaveBehavior saveBehavior );
}
