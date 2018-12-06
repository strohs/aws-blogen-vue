package com.cliff.aws.blogen.api.v1.services;

import com.cliff.aws.blogen.api.v1.controllers.PostController;
import com.cliff.aws.blogen.api.v1.model.PostDTO;
import com.cliff.aws.blogen.api.v1.model.PostListDTO;
import com.cliff.aws.blogen.api.v1.model.PostRequestDTO;
import com.cliff.aws.blogen.domain.Blogen;

/**
 * Service interface for REST methods that operate on {@link com.cliff.aws.blogen.domain.Blogen} Post(s)
 *
 * @author Cliff
 */
public interface PostService {


    /**
     * get all posts containing the specified categoryName, for the specified pageNum, with  up to pageSize posts
     * per page
     * @param categoryName categoryName of posts to retrieve, set to "ALL" to get all posts in all categories
     * @param pageNum the page number of posts to retrieve
     * @param pageSize the number of posts, per page, to retrieve.
     * @return
     */
    PostListDTO getPosts( String categoryName, int pageNum, int pageSize );

    /**
     * get a specific post by its ID
     *
     * @param compositeId - should be a composite id with fields separated by Blogen.ATT_SEP
     *                    example: THREAD_ID # 'POST' # CREATED_TIMESTAMP # POST_ID
     * @return
     */
    PostDTO getPost( String compositeId );

    /**
     * get posts for the specified user and category
     * @param userId
     * @param categoryName the categoryName of the posts to retrieve
     * @param pageNum the page number of posts to return
     * @param limit max posts per page to return
     * @return
     */
    PostListDTO getPostsForUser( String userId, String categoryName, int pageNum, int limit );


    /**
     * Creates a new thread. Any extraneous child posts sent in the DTO will be ignored.
     *
     * @param requestDTO contains post data to create.
     * @return a {@link PostDTO} representing the newly created post
     */
    PostDTO createNewPost( PostRequestDTO requestDTO );

    /**
     * Creates a new child post. The child post will be associated with the threadId
     * @param threadId the threadId that this post belongs to
     * @return a {@link PostDTO} representing the newly created child post
     */
    PostDTO createNewChildPost( String threadId, PostRequestDTO requestDTO );

    /**
     * Saves/updates an existing Post
     * @param id the id of the Post to update
     * @param requestDTO data to update the post with
     * @return a {@link PostDTO} containing the newly updated fields
     */
    PostDTO saveUpdatePost( String id, PostRequestDTO requestDTO );

    /**
     * search posts title and text for the specified searchStr
     * @param search text string to search for
     * @return
     */
    PostListDTO searchPosts( String search, int limit );


    /**
     * Delete the post with the parentId
     * @param id ID of the post to delete
     */
    void deletePost( String id );

    //helper method that builds a URL String to a particular post
    default String buildPostUrl( Blogen post ) {
        return PostController.BASE_URL + "/" + post.getPostId();
    }


}
