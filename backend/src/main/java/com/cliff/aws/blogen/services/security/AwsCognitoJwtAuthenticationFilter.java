package com.cliff.aws.blogen.services.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AwsCognitoJwtAuthenticationFilter extends OncePerRequestFilter {

    private AwsCognitoIdTokenProcessor awsCognitoIdTokenProcessor;

    public AwsCognitoJwtAuthenticationFilter(AwsCognitoIdTokenProcessor awsCognitoIdTokenProcessor) {
        this.awsCognitoIdTokenProcessor = awsCognitoIdTokenProcessor;
    }


    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
        Authentication authentication = null;
        try {
            authentication = awsCognitoIdTokenProcessor.getAuthentication( request );

            if (authentication!=null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                JwtAuthentication jauth = (JwtAuthentication) authentication;
                authentication.getAuthorities().forEach( sga -> log.info( sga.toString() )  );
            }

        } catch (Exception e) {
            log.error("Error occured while processing Cognito ID Token",e);
            SecurityContextHolder.clearContext();
            //return;
            //throw new ServletException("Error occured while processing Cognito ID Token",e);
        }

        filterChain.doFilter(request,response);
    }
}
