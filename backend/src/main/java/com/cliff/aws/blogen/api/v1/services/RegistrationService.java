package com.cliff.aws.blogen.api.v1.services;

/**
 * Interface for performing user registration tasks with cognito.
 * Namely, assigning newly registered users to an appropriate Group within the cognito user-pool.
 * These groups are included (by cognito) in the claims of every JWT issued by them.
 * Spring security will examine the tokens for these groups and then map them into roles.
 */
public interface RegistrationService {

    /**
     * adds the specified user to the specified Cognito Group.
     * These groups map to Spring security roles and are used to determine a user's authorizations.
     *
     * @param userId - the user's internal Cognito 'id', sometimes called the 'username' within cognito
     * @param roleName - the role to add the user to, i.e. 'User' or 'Admin'
     */
    void addUserToGroup(String userId, String roleName);
}
