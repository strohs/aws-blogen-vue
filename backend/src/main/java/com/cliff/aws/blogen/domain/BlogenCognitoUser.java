package com.cliff.aws.blogen.domain;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * A Spring Security User class that stores details specific to a Blogen user.
 *
 * Information from the Cognito ID-Token is mapped into this class and then stored in the Spring Authentication
 * object (as the principal).
 *
 * Since we allow users to log in using their email address, cognito generates a UUID and stores that as the username.
 *
 * Author: Cliff
 */
public class BlogenCognitoUser extends User {

    private static final String PREFERRED_USERNAME = "preferred_username";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "given_name";
    private static final String LAST_NAME = "family_name";
    private static final String AVATAR_FILE_NAME = "custom:avatar_file_name";

    // these are the claims that are needed (specific to) for a Blogen user
    private String firstName;
    private String lastName;
    private String email;
    private String preferredUsername;
    private String avatarFileName;

    public BlogenCognitoUser( String username, String password,
                              Collection<? extends GrantedAuthority> authorities, JWTClaimsSet claimsSet ) {
        super(username, password, authorities);
        this.firstName = claimsSet.getClaim( FIRST_NAME ).toString();
        this.lastName = claimsSet.getClaim( LAST_NAME ).toString();
        this.email = claimsSet.getClaim( EMAIL ).toString();
        this.preferredUsername = claimsSet.getClaim( PREFERRED_USERNAME ).toString();
        this.avatarFileName = claimsSet.getClaim(AVATAR_FILE_NAME).toString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername( String preferredUsername ) {
        this.preferredUsername = preferredUsername;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public void setAvatarFileName( String avatarFileName ) {
        this.avatarFileName = avatarFileName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BlogenCognitoUser{");
        sb.append("username='").append( this.getUsername() ).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", preferredUsername='").append(preferredUsername).append('\'');
        sb.append(", avatarFileName='").append(avatarFileName).append('\'');
        sb.append("GRANTED_ROLES:  ");
        this.getAuthorities().forEach( grant -> sb.append(grant.toString() ));

        sb.append('}');
        return sb.toString();
    }
}
