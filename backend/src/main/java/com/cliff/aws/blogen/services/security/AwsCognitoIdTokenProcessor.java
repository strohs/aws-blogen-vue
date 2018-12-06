package com.cliff.aws.blogen.services.security;

import com.cliff.aws.blogen.domain.BlogenCognitoUser;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AwsCognitoIdTokenProcessor {


    private static final String ROLE_PREFIX = "ROLE_";
    private static final String EMPTY_PWD = "";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private ConfigurableJWTProcessor configurableJWTProcessor;

    @Autowired
    private JwtIdTokenCredentialsHolder jwtIdTokenCredentialsHolder;

    public Authentication getAuthentication( HttpServletRequest request) throws Exception {

        String idToken = request.getHeader(jwtConfiguration.getHttpHeader());
        if (idToken != null) {

            JWTClaimsSet claimsSet = null;

            claimsSet = configurableJWTProcessor.process(stripBearerToken(idToken), null);

            if (!isIssuedCorrectly(claimsSet)) {
                throw new Exception(String.format("Issuer %s in JWT token doesn't match cognito idp %s", claimsSet.getIssuer(), jwtConfiguration.getCognitoIdentityPoolUrl()));
            }

            if (!isIdToken(claimsSet)) {
                throw new Exception("JWT Token doesn't seem to be an ID Token");
            }

            String username = claimsSet.getClaims().get(jwtConfiguration.getUserNameField()).toString();

            if (username != null) {

                List<String> groups = (List<String>) claimsSet.getClaims().get(jwtConfiguration.getGroupsField());
                List<GrantedAuthority> grantedAuthorities = convertList(groups, group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()));
                BlogenCognitoUser user = new BlogenCognitoUser(username, EMPTY_PWD, grantedAuthorities, claimsSet);

                jwtIdTokenCredentialsHolder.setIdToken(stripBearerToken(idToken));
                return new JwtAuthentication(user, claimsSet, grantedAuthorities);
            }

        }

        log.debug("No idToken found in HTTP Header");
        return null;
    }

    private String stripBearerToken(String token) {
        return token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()) : token;
    }

    private boolean isIssuedCorrectly(JWTClaimsSet claimsSet) {
        return claimsSet.getIssuer().equals(jwtConfiguration.getCognitoIdentityPoolUrl());
    }

    private boolean isIdToken(JWTClaimsSet claimsSet) {
        return claimsSet.getClaim("token_use").equals("id");
    }

    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect( Collectors.toList());
    }
}
