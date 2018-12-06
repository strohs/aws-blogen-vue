
package com.cliff.aws.lambda.blogen.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAttributes {

    @SerializedName("sub")
    @Expose
    private String sub;
    @SerializedName("cognito:email_alias")
    @Expose
    private String cognitoEmailAlias;
    @SerializedName("cognito:user_status")
    @Expose
    private String cognitoUserStatus;
    @SerializedName("custom:avatar_file_name")
    @Expose
    private String customAvatarFileName;
    @SerializedName("email_verified")
    @Expose
    private String emailVerified;
    @SerializedName("preferred_username")
    @Expose
    private String preferredUsername;
    @SerializedName("given_name")
    @Expose
    private String givenName;
    @SerializedName("family_name")
    @Expose
    private String familyName;
    @SerializedName("email")
    @Expose
    private String email;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getCognitoEmailAlias() {
        return cognitoEmailAlias;
    }

    public void setCognitoEmailAlias(String cognitoEmailAlias) {
        this.cognitoEmailAlias = cognitoEmailAlias;
    }

    public String getCognitoUserStatus() {
        return cognitoUserStatus;
    }

    public void setCognitoUserStatus(String cognitoUserStatus) {
        this.cognitoUserStatus = cognitoUserStatus;
    }

    public String getCustomAvatarFileName() {
        return customAvatarFileName;
    }

    public void setCustomAvatarFileName(String customAvatarFileName) {
        this.customAvatarFileName = customAvatarFileName;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
