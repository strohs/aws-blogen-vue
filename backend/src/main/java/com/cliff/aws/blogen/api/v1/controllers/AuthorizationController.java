package com.cliff.aws.blogen.api.v1.controllers;

import com.cliff.aws.blogen.api.v1.model.PostListDTO;
import com.cliff.aws.blogen.api.v1.services.PostService;
import com.cliff.aws.blogen.api.v1.services.RegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling new-user sign-ups and user log-ins
 *
 * Author: Cliff
 */
@Api
@Slf4j
@RestController
@RequestMapping( AuthorizationController.BASE_URL )
public class AuthorizationController {

    public static final String BASE_URL = "/api/v1/auth";

    private PostService postService;

    private RegistrationService registrationService;

    @Autowired
    public AuthorizationController( PostService postService,
                                    RegistrationService registrationService ) {
        this.postService = postService;
        this.registrationService = registrationService;
    }


    @ApiOperation( value = "get the latest posts", produces = "application/json")
    @GetMapping( "/latestPosts" )
    public PostListDTO latestPosts( @RequestParam( name = "limit", defaultValue = "9") int limit ) {
        log.debug( "get latest posts limit={}", limit );
        return postService.getPosts( "ALL", 0, limit );
    }


    @ApiOperation( value = "completes registration of a newly signed up user")
    @GetMapping("/register/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@PathVariable("userId") String id) {
        log.debug("register user {}", id);
        registrationService.addUserToGroup(id, "User");
    }

}
