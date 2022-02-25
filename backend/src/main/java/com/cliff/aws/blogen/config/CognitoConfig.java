package com.cliff.aws.blogen.config;

/**
 * This class holds the current Cognito userPoolId, identityPoolId, and
 * region configuration. This is class is mainly used by spring security to verify
 * JWT tokens issued by cognito.
 */
public interface CognitoConfig {

    String getUserPoolId();
    void setUserPoolId(String userPoolId);


    String getIdentityPoolId();
    void setIdentityPoolId(String identityPoolId);

    String getRegion();
    void setRegion(String region);
}
