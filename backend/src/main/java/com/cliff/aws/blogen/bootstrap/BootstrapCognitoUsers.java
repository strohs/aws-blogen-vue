package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;

/**
 * This is a standalone utility class that creates five 'fake' Blogen users in the Blogen Cognito user pool using the
 * Cognito admin* api from the AWS Java SDK. The Blogen user pool must already be created.
 * <p>
 * The created users will be added to the user group, have a fake e-mail address and phone number, and their
 * passwords will all be set to 'tempPassword'. The passwords must be changed when the user logs into the website.
 *
 * You can provide AWS credentials within this class (i.e. accessKey and secretAccessKey) that will override the default
 * provider chain, but it would be safer to set accessKey and secretKey to null in order to have your credentials
 * pulled from the default provider chain.
 * <p>
 * You must configure the USER_POOL_ID below
 * <p>
 * Author: Cliff
 */
public class BootstrapCognitoUsers {

    // the name of the "User" group in cognito
    private static final String USER_GROUP = "User";

    // the name of the "Admin" group in cognito
    private static final String ADMIN_GROUP = "Admin";

    // provide both keys to override any crednetials configured locally on your machine
    private static final String accessKey = null;
    private static final String secretKey = null;

    // blogen user pool id
    private static String USER_POOL_ID = "USER-POOL-ID-HERE";


    private AWSCredentials buildStaticCredentials (String accessKey, String secretKey) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    // build the Cognito Client
    private AWSCognitoIdentityProvider buildClient () {
        if ( accessKey != null && secretKey != null ) {
            return AWSCognitoIdentityProviderClientBuilder
                    .standard()
                    .withCredentials(
                            new AWSStaticCredentialsProvider(buildStaticCredentials(accessKey, secretKey))
                    )
                    .build();
        } else {
            return AWSCognitoIdentityProviderClientBuilder.defaultClient();
        }
    }

    private AdminCreateUserResult createAdmin (AWSCognitoIdentityProvider client, String email, String firstName, String lastName,
                                              String preferredName, String avatarFileName) {
        // create the user Attributes required by the Blogen User Pool
        AttributeType attFirstName = new AttributeType().withName("given_name").withValue(firstName);
        AttributeType attLastName = new AttributeType().withName("family_name").withValue(lastName);
        AttributeType attPrefName = new AttributeType().withName("preferred_username").withValue(preferredName);
        AttributeType attEmail = new AttributeType().withName("email").withValue(email);
        AttributeType attAvatar = new AttributeType().withName("custom:avatar_file_name").withValue(avatarFileName);
        AttributeType attEmailVer = new AttributeType().withName("email_verified").withValue("True");
        AdminCreateUserRequest request = new AdminCreateUserRequest()
                .withUsername(email)
                .withTemporaryPassword("tempPassword")
                .withUserAttributes(attFirstName, attLastName, attPrefName, attAvatar, attEmail, attEmailVer)
                .withUserPoolId(USER_POOL_ID)
                .withMessageAction(MessageActionType.SUPPRESS);

        AdminCreateUserResult userRes = client.adminCreateUser(request);
        addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), USER_GROUP);
        addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), ADMIN_GROUP);
        return userRes;
    }

    /**
     * creates a blogen user with the specified attributes. The cognito username will be the user's email address.
     * the user will be in the FORCE_CHANGE_PASSWORD state
     */
    private AdminCreateUserResult createUser (AWSCognitoIdentityProvider client, String email, String firstName, String lastName,
                                              String preferredName, String avatarFileName) {
        // create the user Attributes required by the Blogen User Pool
        AttributeType attFirstName = new AttributeType().withName("given_name").withValue(firstName);
        AttributeType attLastName = new AttributeType().withName("family_name").withValue(lastName);
        AttributeType attPrefName = new AttributeType().withName("preferred_username").withValue(preferredName);
        AttributeType attEmail = new AttributeType().withName("email").withValue(email);
        AttributeType attAvatar = new AttributeType().withName("custom:avatar_file_name").withValue(avatarFileName);
        AttributeType attEmailVer = new AttributeType().withName("email_verified").withValue("True");
        AdminCreateUserRequest request = new AdminCreateUserRequest()
                .withUsername(email)
                .withTemporaryPassword("tempPassword")
                .withUserAttributes(attFirstName, attLastName, attPrefName, attAvatar, attEmail, attEmailVer)
                .withUserPoolId(USER_POOL_ID)
                .withMessageAction(MessageActionType.SUPPRESS);
        return client.adminCreateUser(request);
    }

    /**
     * add the specified user to the specified group name, in the specified userPool
     */
    private AdminAddUserToGroupResult addUserToGroup (AWSCognitoIdentityProvider client, String userPoolId,
                                                      String userName, String groupName) {
        return client.adminAddUserToGroup(
                new AdminAddUserToGroupRequest()
                        .withGroupName(groupName)
                        .withUserPoolId(userPoolId)
                        .withUsername(userName));
    }

    public static void main (String[] args) {

        BootstrapCognitoUsers boot = new BootstrapCognitoUsers();
        AWSCognitoIdentityProvider client = boot.buildClient();
        AdminCreateUserResult userRes = null;
        AdminAddUserToGroupResult groupRes = null;

        // create a blogen admin
        userRes = boot.createAdmin(client, "admin@example.com", "Admin", "Admin", "admin", "avatar7.jpg");
        adminPrintUserInfo(client, userRes.getUser().getUsername());

        // create user "mcgill"
//        userRes = boot.createUser(client, "mcgill@example.com", "Maggie", "McGill", "mcgill", "avatar1.jpg");
//        adminPrintUserInfo(client, userRes.getUser().getUsername());
//        groupRes = boot.addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), USER_GROUP);
//
//        // create user "scotsman"
//        userRes = boot.createUser(client, "scotsman@example.com", "William", "Wallace", "scotsman", "avatar2.jpg");
//        adminPrintUserInfo(client, userRes.getUser().getUsername());
//        groupRes = boot.addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), USER_GROUP);
//
//        //create user "johndoe"
//        userRes = boot.createUser(client, "johndoe@example.com", "John", "Doe", "johndoe", "avatar3.jpg");
//        adminPrintUserInfo(client, userRes.getUser().getUsername());
//        groupRes = boot.addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), USER_GROUP);
//
//        //create user "lizreed"
//        userRes = boot.createUser(client, "lizreed@example.com", "Elizabeth", "Reed", "lizreed", "avatar4.jpg");
//        adminPrintUserInfo(client, userRes.getUser().getUsername());
//        groupRes = boot.addUserToGroup(client, USER_POOL_ID, userRes.getUser().getUsername(), USER_GROUP);
    }

    // uses 'admin' api calls to print a users information
    private static void adminPrintUserInfo (AWSCognitoIdentityProvider client, String userName) {
        AdminGetUserResult getUserResult = client.adminGetUser(
                new AdminGetUserRequest()
                        .withUserPoolId(USER_POOL_ID)
                        .withUsername(userName));
        System.out.println("USER INFO===============================:"+ getUserResult);
        getUserResult.getUserAttributes().forEach(att -> System.out.println(String.format("name:%s  val:%s", att.getName(), att.getValue())));
    }

}
