package com.cliff.aws.blogen.api.v1.controllers;

import com.cliff.aws.blogen.api.v1.model.AvatarResponse;
import com.cliff.aws.blogen.api.v1.services.AvatarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving user preferences
 *
 * Author: Cliff
 */
@Api
@Slf4j
@RestController
@RequestMapping( UserPrefsController.BASE_URL )
public class UserPrefsController {

    public static final String BASE_URL = "/api/v1/avatars";

    private AvatarService avatarService;

    @Autowired
    public UserPrefsController( AvatarService avatarService ) {
        this.avatarService = avatarService;
    }

    @ApiOperation( value = "a list of all Avatar file names", produces = "application/json", authorizations = { @Authorization(value="apiKey") })
    @GetMapping
    public AvatarResponse avatarImageFileNames() {
        return AvatarResponse.builder()
                .avatars( avatarService.getAllAvatarImageNames() )
                .build();

    }
}
