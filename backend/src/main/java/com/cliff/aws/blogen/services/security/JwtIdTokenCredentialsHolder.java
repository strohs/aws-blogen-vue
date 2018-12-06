package com.cliff.aws.blogen.services.security;

/**
 *
 * Request scoped bean that holds the IDToken associated with the user.
 *
 */
public class JwtIdTokenCredentialsHolder {

    public String getIdToken() {
        return idToken;
    }

    public JwtIdTokenCredentialsHolder setIdToken(String idToken) {
        this.idToken = idToken;
        return this;
    }

    private String idToken;

}
