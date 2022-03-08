package com.cliff.aws.blogen.domain;

/**
 * BlogenGroup contains the cognito Groups that blogen users can be assigned to.
 */
public enum BlogenGroup {
    // the basic role for all Blogen users, this will give them access to the REST API
    User,
    // the admin role can edit and delete any posts, and create new categories
    Admin
}