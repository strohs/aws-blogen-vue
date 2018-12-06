package com.cliff.aws.blogen.services.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        return authentication;
    }

    @Override
    public boolean supports( Class<?> authentication ) {
        return true;
    }
}
