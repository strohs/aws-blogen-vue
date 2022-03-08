package com.cliff.aws.blogen.services.security;

import org.springframework.security.core.Authentication;

/**
 * A Facade for retrieving the currently authenticated principal from spring security.
 */
public interface AuthenticationFacade {

    Authentication getAuthentication();

}
