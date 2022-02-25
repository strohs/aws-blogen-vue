package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.cliff.aws.blogen.domain.Blogen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;

/**
 * This class will bootstrap a cognito user pool that is needed by blogen
 * It will create the following resources:
 * - a cognito user pool
 * - an application client for the pool
 * - a "Admin" group
 * - a "User" group
 * - an admin user with email address: "admin@example.com"
 * - 4 regular users with the following email addresses
 */
@Component
@Slf4j
@Profile("dev")
public class UserPoolBootstrapper {

    // these are the roles that can be assigned to Blogen users
    enum BLOGEN_GROUP {
        // the basic role for all Blogen users, this will give them access to the REST API
        User,
        // admin roles can delete any posts, and create new categories
        Admin
    }

    private String region;

    // the name to assign to Blogen's Cognito User Pool
    private static final String USER_POOL_NAME = "BlogenUserPool-" + BootstrapUtils.currentTime();

    // the Application client name.
    private static final String APP_CLIENT_NAME = "blogen-webapp-" + BootstrapUtils.currentTime();


    // this bean holds AWS credentials from the default provider chain, or else
    // it uses the accessKey and secretKey configured in application.properties
    private final AWSCredentialsProvider credentialsProvider;

    // this client is used to access the cognito user pool
    private final AWSCognitoIdentityProvider userPoolClient;

    protected String userPoolId = null;
    protected String userPoolArn = null;
    protected String appClientId = null;

    // this maps user email addresses to their internal cognito "username"
    protected HashMap<String, Blogen> userMap;

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getUserPoolArn() {
        return userPoolArn;
    }

    public String getAppClientId() {
        return appClientId;
    }

    public HashMap<String, Blogen> getUserMap() {
        return userMap;
    }

    @Autowired
    public UserPoolBootstrapper(
            @Value("${blogen.security.jwt.aws.region:us-east-1}") String region,
            AWSCredentialsProvider credentialsProvider) {
        this.region = region;
        this.credentialsProvider = credentialsProvider;
        this.userPoolClient = buildUserPoolClient();
        log.debug("cognito user pool client created");
        userMap = new HashMap<>();
    }

    // build a Cognito Client to access the user pool, using our configured credentials
    private AWSCognitoIdentityProvider buildUserPoolClient() {
        return AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withRegion(this.region)
                .withCredentials(credentialsProvider)
                .build();
    }

    /**
     * Creates a new Cognito User Pool with the given userPoolName. This method will also create the following:
     * - user's can use their email address as their username
     * - MFA is not enabled
     * - email addresses will be automatically verified once created
     * - user's must use passwords of at least 8 characters, and at least one character must be uppercase and one must
     * be lowercase
     *
     * @param userPoolName
     */
    private CreateUserPoolResult createUserPool(String userPoolName) {
        CreateUserPoolRequest req = new CreateUserPoolRequest()
                .withPoolName(userPoolName)
                .withAutoVerifiedAttributes(VerifiedAttributeType.Email)
                .withMfaConfiguration(UserPoolMfaType.OFF)
                .withUsernameAttributes(UsernameAttributeType.Email)
                .withSchema(
                        new SchemaAttributeType().withName("given_name").withRequired(true).withMutable(true),
                        new SchemaAttributeType().withName("family_name").withRequired(true).withMutable(true),
                        new SchemaAttributeType().withName("preferred_username").withRequired(true).withMutable(true),
                        new SchemaAttributeType().withName("email").withRequired(true).withMutable(true)
                )
                .withPolicies(new UserPoolPolicyType()
                        // passwords must be at least 8 characters
                        .withPasswordPolicy(
                                new PasswordPolicyType()
                                        .withMinimumLength(8)
                                        .withRequireLowercase(true)
                                        .withRequireUppercase(true)
                        )
                );
        //TODO should we create a PostConfirmationLambda, or just auto add all BOOTRAPPED USERS to the users group
        //.withLambdaConfig(new LambdaConfigType().withPostConfirmation())

        CreateUserPoolResult res = this.userPoolClient.createUserPool(req);
        log.debug("created user pool with id {}", res.getUserPool().getId());

        // create another request to create our custom attribute:  avatar_file_name
        AddCustomAttributesRequest custReq = new AddCustomAttributesRequest()
                .withUserPoolId(res.getUserPool().getId())
                .withCustomAttributes(new SchemaAttributeType().withName("avatar_file_name").withAttributeDataType(AttributeDataType.String));
        AddCustomAttributesResult custRes = this.userPoolClient.addCustomAttributes(custReq);
        log.debug("created custom attribute: avatar_file_name");

        return res;
    }

    /**
     * create a user pool application client for the given userPoolId, with the given appClientName
     */
    CreateUserPoolClientResult createUserPoolApplicationClient(String userPoolId, String appClientName) {
        CreateUserPoolClientRequest req = new CreateUserPoolClientRequest()
                .withUserPoolId(userPoolId)
                .withClientName(appClientName)
                .withExplicitAuthFlows(ExplicitAuthFlowsType.ADMIN_NO_SRP_AUTH);
        CreateUserPoolClientResult res = this.userPoolClient.createUserPoolClient(req);
        log.debug("created user pool application client with id {}", res.getUserPoolClient().getClientId());
        return res;
    }

    /**
     * creates a blogen user with the specified attributes. The cognito username will be the
     * user's email address.
     * the user will be in the FORCE_CHANGE_PASSWORD state
     */
    private AdminCreateUserResult createUser(
            String userPoolId,
            String email,
            String firstName,
            String lastName,
            String preferredName,
            String avatarFileName) {
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
                .withUserPoolId(userPoolId)
                .withMessageAction(MessageActionType.SUPPRESS);

        AdminCreateUserResult res = this.userPoolClient.adminCreateUser(request);
        log.debug("created user {} with username {}", email, res.getUser().getUsername());

        // store user in user hashmap, for later use by DynamoDB bootstrapper
        Blogen user = Blogen.builder()
                .userId(res.getUser().getUsername())
                .userName(preferredName)
                .avatarFileName(avatarFileName)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        this.userMap.put(email, user);

        return res;
    }

    private CreateGroupResult createGroup(String userPoolId, BLOGEN_GROUP group, int precedence) {
        CreateGroupResult res = this.userPoolClient.createGroup(new CreateGroupRequest()
                .withGroupName(group.toString())
                .withUserPoolId(userPoolId)
                .withPrecedence(precedence));
        log.debug("created group {} with precedence {}", group.toString(), precedence);
        return res;
    }

    /**
     * add the specified user to the specified group name, in the specified userPool
     */
    private AdminAddUserToGroupResult addUserToGroup(
            String userPoolId,
            String userName,
            BLOGEN_GROUP group) {
        AdminAddUserToGroupResult res = this.userPoolClient.adminAddUserToGroup(
                new AdminAddUserToGroupRequest()
                        .withGroupName(group.toString())
                        .withUserPoolId(userPoolId)
                        .withUsername(userName));
        log.debug("added user {} to group {}", userName, group);
        return res;
    }

    /**
     * returns the specified user details from the internal map
     */
    protected Blogen getUser(String email) {
        return this.userMap.get(email);
    }

    @PreDestroy
    private void deleteUserPool() {
        try {
            log.info("deleting user pool {}", this.userPoolId);
            this.userPoolClient.deleteUserPool(new DeleteUserPoolRequest().withUserPoolId(this.userPoolId));
            log.info("deleted user pool");
        } catch (Exception e) {
            log.error("exception deleting user pool {} exception is {}", this.userPoolId, e.getMessage());
        }

    }

    public void bootstrap() {
        // create user pool
        CreateUserPoolResult userPoolRes = createUserPool(USER_POOL_NAME);
        this.userPoolId = userPoolRes.getUserPool().getId();
        this.userPoolArn = userPoolRes.getUserPool().getArn();

        // create admin group
        createGroup(this.userPoolId, BLOGEN_GROUP.Admin, 5);

        // create user group
        createGroup(this.userPoolId, BLOGEN_GROUP.User, 10);

        // create user pool application client
        CreateUserPoolClientResult appClientRes = createUserPoolApplicationClient(this.userPoolId, APP_CLIENT_NAME);
        this.appClientId = appClientRes.getUserPoolClient().getClientId();

        // create an admin user
        AdminCreateUserResult adminRes = createUser(this.userPoolId, "admin@example.com", "FirstAdmin", "LastAdmin", "admin", "avatar7.jpg");
        // assign admin to admin group
        addUserToGroup(userPoolRes.getUserPool().getId(), adminRes.getUser().getUsername(), BLOGEN_GROUP.Admin);
        addUserToGroup(userPoolRes.getUserPool().getId(), adminRes.getUser().getUsername(), BLOGEN_GROUP.User);

        // create regular (non-admin) users, add them to user group and store their usernames in a HashMap for use by DynamoDB

        // create user "mcgill"
        AdminCreateUserResult userRes = createUser(this.userPoolId, "mcgill@example.com", "Maggie", "McGill", "mcgill", "avatar1.jpg");
        addUserToGroup(userPoolRes.getUserPool().getId(), userRes.getUser().getUsername(), BLOGEN_GROUP.User);

        // create user "scotsman"
        userRes = createUser(this.userPoolId, "scotsman@example.com", "William", "Wallace", "scotsman", "avatar2.jpg");
        addUserToGroup(userPoolRes.getUserPool().getId(), userRes.getUser().getUsername(), BLOGEN_GROUP.User);

        //create user "johndoe"
        userRes = createUser(this.userPoolId, "johndoe@example.com", "John", "Doe", "johndoe", "avatar3.jpg");
        addUserToGroup(userPoolRes.getUserPool().getId(), userRes.getUser().getUsername(), BLOGEN_GROUP.User);

        //create user "lizreed"
        userRes = createUser(this.userPoolId, "lizreed@example.com", "Elizabeth", "Reed", "lizreed", "avatar4.jpg");
        addUserToGroup(userPoolRes.getUserPool().getId(), userRes.getUser().getUsername(), BLOGEN_GROUP.User);

    }

}
