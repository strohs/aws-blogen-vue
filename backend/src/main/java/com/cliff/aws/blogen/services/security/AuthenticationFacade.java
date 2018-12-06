package com.cliff.aws.blogen.services.security;

import org.springframework.security.core.Authentication;

/**
 * Facade for Spring Security Authentication
 */
public interface AuthenticationFacade {

    Authentication getAuthentication();

}
