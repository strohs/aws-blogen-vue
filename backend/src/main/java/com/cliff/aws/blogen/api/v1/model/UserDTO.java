package com.cliff.aws.blogen.api.v1.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for {@link com.blogen.domain.Blogen} user. This domain object is used to transfer the personal
 * details of a user
 *
 * @author Cliff
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @ApiModelProperty( value = "User ID", readOnly = true, example = "340sd-dfdfd-34234-dfsdff")
    private String id;

    @ApiModelProperty(value = "first name of the user", example = "Mary")
    private String firstName;

    @ApiModelProperty(value = "last name of the user", example = "Jones")
    private String lastName;

    @ApiModelProperty(value = "user name of the user", example = "superCool2049")
    private String userName;

    @ApiModelProperty(value = "users email address", example = "crazy@gmail.com")
    private String email;

    @ApiModelProperty(value = "users password", example="superSecretPassword")
    private String password = "";

    @ApiModelProperty(value = "users avatar image file name", example="avatar43.jpg")
    private String avatarImage;

    @ApiModelProperty(value = "the roles this user has within the website", readOnly = true, example = "USER,ADMIN")
    private Set<String> roles;

    @ApiModelProperty(value = "url that identifies this user", readOnly = true, example = "/api/v1/users/23")
    private String userUrl;

}
