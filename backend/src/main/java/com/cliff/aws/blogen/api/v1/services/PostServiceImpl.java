package com.cliff.aws.blogen.api.v1.services;

import com.cliff.aws.blogen.api.v1.mappers.PostMapper;
import com.cliff.aws.blogen.api.v1.mappers.PostRequestMapper;
import com.cliff.aws.blogen.api.v1.model.PostDTO;
import com.cliff.aws.blogen.api.v1.model.PostListDTO;
import com.cliff.aws.blogen.api.v1.model.PostRequestDTO;
import com.cliff.aws.blogen.domain.Blogen;
import com.cliff.aws.blogen.domain.BlogenCognitoUser;
import com.cliff.aws.blogen.domain.BlogenPrimaryKey;
import com.cliff.aws.blogen.exceptions.BadRequestException;
import com.cliff.aws.blogen.exceptions.NotFoundException;
import com.cliff.aws.blogen.repositories.BlogenRepository;
import com.cliff.aws.blogen.services.security.AuthenticationFacade;
import com.cliff.aws.blogen.services.utils.PageRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for performing RESTful CRUD operations on Blogen {@link com.cliff.aws.blogen.domain.Blogen} Post(s)
 *
 * @author Cliff
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private PageRequestBuilder pageRequestBuilder;
    private BlogenRepository blogenRepository;
    private CategoryService categoryService;
    private PostMapper postMapper;
    private PostRequestMapper postRequestMapper;
    private AuthenticationFacade authFacade;

    @Autowired
    public PostServiceImpl( PageRequestBuilder pageRequestBuilder,
                            BlogenRepository blogenRepository,
                            CategoryService categoryService,
                            PostMapper postMapper,
                            PostRequestMapper postRequestMapper,
                            AuthenticationFacade authFacade ) {
        this.pageRequestBuilder = pageRequestBuilder;
        this.blogenRepository = blogenRepository;
        this.categoryService = categoryService;
        this.postMapper = postMapper;
        this.postRequestMapper = postRequestMapper;
        this.authFacade = authFacade;
    }


    @Override
    public PostListDTO getPosts( String categoryName, int pageNum, int pageSize ) {
        //create a PageRequest
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest( pageNum, pageSize );
        //retrieve the posts
        Page<List<Blogen>> page;
        if ( categoryName.equalsIgnoreCase( CategoryService.CATEGORY_ALL ) )
            page = blogenRepository.fetchRecentThreads( pageRequest );
        else
            page = blogenRepository.fetchThreadsByCategoryName( categoryName, pageRequest );

        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach( post -> {
            PostDTO dto = postMapper.blogenThreadToPostDTO( post );
            postDTOS.add( dto );
        } );
        return new PostListDTO( postDTOS, PageRequestBuilder.buildPageInfoResponse(page) );
    }

    @Override
    public PostDTO getPost( String compositeId ) {
        BlogenPrimaryKey pk = postMapper.parseCompositePostId( compositeId );
        Blogen post = blogenRepository.findByPrimaryHashAndPrimaryRange( pk.getPrimaryHash(), pk.getPrimaryRange() )
                .orElseThrow( () -> new NotFoundException( "post not found with id:" + compositeId ) );
        return postMapper.blogenToPostDto( post );
    }


    @Override
    public PostListDTO getPostsForUser( String userId, String categoryName, int pageNum, int pageSize ) {
        //TODO return empty results if user id does not exist, may need try catch

        //create a PageRequest
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest( pageNum, pageSize );
        Page<List<Blogen>> page;
        if ( categoryName.equals( CategoryService.CATEGORY_ALL ) ) {
            page = blogenRepository.fetchThreadsByUserId( userId, pageRequest );
        }
        else {
            // check if category name exists
            validateCategoryName( categoryName );
            page = blogenRepository.fetchThreadsByUserIdAndCategoryName( userId, categoryName, pageRequest );
        }

        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach( post -> {
            PostDTO dto = postMapper.blogenThreadToPostDTO( post );
            postDTOS.add( dto );
        } );
        return new PostListDTO( postDTOS, PageRequestBuilder.buildPageInfoResponse(page) );
    }

    @Override
    @Transactional
    //creates a new Parent Post
    public PostDTO createNewPost( PostRequestDTO postDTO ) {
        Blogen post = buildNewThread( postDTO );
        Blogen savedPost = blogenRepository.save( post );
        return postMapper.blogenToPostDto( savedPost );
    }

    @Override
    @Transactional
    public PostDTO createNewChildPost( String compositeId, PostRequestDTO requestDTO ) {
        BlogenPrimaryKey pk = postMapper.parseCompositePostId( compositeId );
        Blogen threadStartingPost = blogenRepository.findByPrimaryHashAndPrimaryRange( pk.getPrimaryHash(), pk.getPrimaryRange())
                .orElseThrow( () -> new BadRequestException( "Post with id " + compositeId + " was not found" ) );
        //make sure the compositeId points to a starting thread
        if ( !threadStartingPost.getThreadId().equals(threadStartingPost.getPostId()) )
            throw new BadRequestException( "Post with id: " + compositeId + " is a child post. Cannot create a new child post onto an existing child post" );
        String postId = UUID.randomUUID().toString();
        Blogen childPost = buildNewPost( threadStartingPost.getThreadId(), postId, requestDTO  );
        Blogen savedPost = blogenRepository.save( childPost );
        return postMapper.blogenToPostDto( savedPost );
    }

    @Override
    public PostDTO saveUpdatePost( String compositeId, PostRequestDTO requestDTO ) {
        BlogenPrimaryKey pk = postMapper.parseCompositePostId( compositeId );
        Blogen postToUpdate = blogenRepository.findByPrimaryHashAndPrimaryRange( pk.getPrimaryHash(), pk.getPrimaryRange())
                .orElseThrow( () -> new BadRequestException( "Post with id " + compositeId + " was not found" ) );
        // validate category name exists
        validateCategoryName( requestDTO.getCategoryName() );
        // merge non-null fields of the DTO into postToUpdate
        postRequestMapper.updatePostFromPostRequestDTO( requestDTO, postToUpdate );
        Blogen savedPost = blogenRepository.save( postToUpdate );
        return postMapper.blogenToPostDto( savedPost );
    }

    @Override
    public PostListDTO searchPosts( String search, int limit ) {
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest( 0, limit );
        Page<Blogen> page = blogenRepository.searchPostTextAndTitle( search, pageRequest );
        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach( post -> {
            PostDTO dto = postMapper.blogenToPostDto( post );
            postDTOS.add( dto );
        } );
        return new PostListDTO( postDTOS, PageRequestBuilder.buildPageInfoResponse(page) );
    }


    @Override
    @Transactional
    public void deletePost( String compositeId ) {
        BlogenPrimaryKey pk = postMapper.parseCompositePostId( compositeId );
        Blogen post = blogenRepository.findByPrimaryHashAndPrimaryRange( pk.getPrimaryHash(), pk.getPrimaryRange() )
                .orElseThrow( () -> new BadRequestException( "Post with id " + compositeId + " was not found" ) );
        // if the post is a thread starting post, then delete all posts in the thread
        if ( post.getThreadId().equals( post.getPostId() ) ) {
            List<Blogen> posts = blogenRepository.findByPrimaryHash( post.getThreadId() );
            posts.forEach( blogenRepository::delete );
        } else {
            blogenRepository.delete( post );
        }
    }

    /**
     * build a new {@link Blogen} Post object, making sure to retrieve existing data from the Category and
     * User repositories.
     *
     * @param requestDTO
     * @return
     */
    private Blogen buildNewThread( PostRequestDTO requestDTO ) {
        //generate a threadId
        String threadId = UUID.randomUUID().toString();
        // NOTE the first post of a thread must have a postId equal to threadId
        return buildNewPost( threadId, threadId, requestDTO );
    }

    private Blogen buildNewPost( String threadId, String postId, PostRequestDTO requestDTO ) {
        //build the 'key' fields of a Blogen post
        Instant created = Instant.now();
        String rangeKey = Blogen.buildPostRangeKey( created, postId );

        // get the details of the current authenticated user from Spring Security
        BlogenCognitoUser user = (BlogenCognitoUser) authFacade.getAuthentication().getPrincipal();

        // map the requestDTO details
        Blogen post = postRequestMapper.postRequestDtoToPost( requestDTO );
        // set the remaining required fields of a Blogen post object that haven't been mapped
        post.setPrimaryHash( threadId );
        post.setPrimaryRange( rangeKey );
        post.setUserId( user.getUsername() );
        post.setUserName( user.getPreferredUsername() );
        post.setAvatarFileName( user.getAvatarFileName() );
        post.setPostId( postId );
        post.setThreadId( threadId );
        post.setUpdatedTimestamp( created );

        return post;
    }



    /**
     * validate that a category name exists in the repository
     * @param name category name to search for
     * @return the Category corresponding to the name
     * @throws BadRequestException if the category name does not exist in the repository
     */
    private void validateCategoryName( String name ) throws NotFoundException {
        if ( !categoryService.categoryNameExists( name ) )
            throw new NotFoundException( "category with name: " + name + " does not exist" );
    }



}
