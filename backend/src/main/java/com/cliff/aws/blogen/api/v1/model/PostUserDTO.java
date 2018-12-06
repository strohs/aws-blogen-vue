package com.cliff.aws.blogen.api.v1.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing {@link com.cliff.aws.blogen.domain.Blogen} user-id and username of the user that made the post.
 * PostUserDTO is meant to transfer details of the user that made a particular post
 *
 * @author Cliff
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUserDTO {

    @ApiModelProperty( value = "User ID", readOnly = true, example = "ad23-25cd-263f")
    private String id;

    @ApiModelProperty(value = "user name", readOnly = true, example = "superCool2049")
    private String userName;

    @ApiModelProperty(value = "relative url to the users avatar image", readOnly = true, example = "/avatars/avatar.jpg")
    private String avatarUrl;

}
