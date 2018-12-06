package com.cliff.aws.blogen.api.v1.controllers;

import com.cliff.aws.blogen.api.v1.model.PostListDTO;
import com.cliff.aws.blogen.api.v1.model.UserDTO;
import com.cliff.aws.blogen.api.v1.services.PostService;
import com.cliff.aws.blogen.domain.BlogenCognitoUser;
import com.cliff.aws.blogen.services.security.AuthenticationFacade;
import com.cliff.aws.blogen.services.security.JwtAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for REST operations in {@link com.cliff.aws.blogen.domain.Blogen} users
 *
 * @author Cliff
 */
@Api
@Slf4j
@RestController
@RequestMapping( UserController.BASE_URL )
public class UserController {

    public static final String BASE_URL = "/api/v1/users";

    private PostService postService;
    private AuthenticationFacade af;

    @Autowired
    public UserController( PostService postService, AuthenticationFacade af ) {
        this.postService = postService;
        this.af = af;
    }

    // update user now done on clients with AWS.Amplify.Auth.updateUserAttributes()

    // chang password now done on clients with AWS.Amplify.Auth.changePassword()

    @ApiOperation( value = "get posts made by a user", produces = "application/json", authorizations = { @Authorization(value="apiKey") })
    @GetMapping( "/{id}/posts" )
    @ResponseStatus( HttpStatus.OK )
    public PostListDTO getUserPosts( @PathVariable("id") String id,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "limit", defaultValue = "5") Integer limit,
                                     @RequestParam(value = "category", defaultValue = "ALL") String category) {
        log.debug( "getUserPosts id={} page={} limit={} category={}", id, page, limit, category );
        return postService.getPostsForUser( id, category, page, limit );
    }

//    @GetMapping( "/creds" )
//    @ResponseStatus( HttpStatus.OK )
//    public String getCreds() {
//
//        JwtAuthentication auth = (JwtAuthentication ) af.getAuthentication();
//        // principal is Spring User
//        BlogenCognitoUser principal = (BlogenCognitoUser) af.getAuthentication().getPrincipal();
//        log.debug( principal.toString() );
//        return "Authenticated?: " + af.getAuthentication().isAuthenticated();
//    }

    
}
