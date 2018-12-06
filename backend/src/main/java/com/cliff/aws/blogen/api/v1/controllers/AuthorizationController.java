package com.cliff.aws.blogen.api.v1.controllers;

import com.cliff.aws.blogen.api.v1.model.PostListDTO;
import com.cliff.aws.blogen.api.v1.services.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    public AuthorizationController( PostService postService ) {
        this.postService = postService;
    }


    @ApiOperation( value = "get the latest posts", produces = "application/json")
    @GetMapping( "/latestPosts" )
    public PostListDTO latestPosts( @RequestParam( name = "limit", defaultValue = "9") int limit ) {
        log.debug( "get latest posts limit={}", limit );
        return postService.getPosts( "ALL", 0, limit );
    }


}
