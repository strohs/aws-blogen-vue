package com.cliff.aws.blogen.api.v1.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for Blogen {@link com.cliff.aws.blogen.domain.Blogen} post data. This is the object returned from
 * the post controller, to client, when they perform CRUD ops on Post(s)
 *
 * @author Cliff
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    @ApiModelProperty( value = "composite id the uniquely identifies this post with a thread", readOnly = true, example = "34af-45cd-ea34-edf4")
    private String id;

    @ApiModelProperty( value = "Thread ID that this post belongs to", readOnly = true, example = "64af-3cd-ea34-4df4")
    private String threadId;

    @ApiModelProperty( value = "Post ID of this post", readOnly = true, example = "34af-45cd-ea34-edf4")
    private String postId;

    @ApiModelProperty( value = "title of the post", required = true, example = "Some Amazing Title")
    private String title;

    @ApiModelProperty( value = "text of the post", required = true, example = "text of the post")
    private String text;

    @ApiModelProperty( value = "url to an image on the web", example = "http://lorempixe/200/400/abstract")
    private String imageUrl;

    @ApiModelProperty( value = "category that this post belongs to", required = true, example = "Web Development")
    private CategoryDTO category;

    @ApiModelProperty(value = "the userName and id of the user who created this post", required = true, example = "coolUserName")
    private PostUserDTO user;

    @ApiModelProperty(value = "ISO8601 date of when this post was created" ,readOnly = true )
    private Instant created;

    @ApiModelProperty(value = "URL that identifies this post", readOnly = true, example = "/api/v1/posts/43")
    private String postUrl;

    @ApiModelProperty(value = "if this is a parent post, contains its child posts", readOnly = true)
    private List<PostDTO> children;
    

}
