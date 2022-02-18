package com.cliff.aws.blogen.bootstrap;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.*;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.*;
import com.cliff.aws.blogen.config.CognitoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;

/**
 * Bootstraps the Cognito Identity Pool resources needed by blogen.
 * It will create the following:
 * - a cognito identity pool
 *      - the pool will use our cognito user pool as the identity provider
 * - two IAM roles and two policies
 *      - an Authenticated role that grants authenticated users access to the following:
 *          - mobileanalytics:PutEvents, cognito-sync:*, cognito-identity:*
 *          - cognito-idp:ListUsers  this allows users to call the ListUsers api on the user pool
 *      - an UnAuthenticated role that grants the following:
 *          - mobileanalytics:PutEvents, cognito-sync:*
 *          - cognito-idp:ListUsers  this allows users to call the ListUsers api on the user pool
 */
@Component
@Slf4j
@Profile({"dev"})
public class IdentityPoolBootstrapper {

    public static final String IDENTITY_POOL_NAME = "BlogenIdentityPool-" + BootstrapUtils.currentTime();

    public static final String UNAUTHENTICATED_ROLE_NAME = "BlogenUnAuthRole-" + BootstrapUtils.currentTime();
    public static final String UNAUTHENTICATED_ROLE_POLICY_NAME = "BlogenUnAuthRolePolicy-" + BootstrapUtils.currentTime();
    public static final String AUTHENTICATED_ROLE_NAME = "BlogenAuthRole-" + BootstrapUtils.currentTime();
    public static final String AUTHENTICATED_ROLE_POLICY_NAME = "BlogenAuthRolePolicy-" + BootstrapUtils.currentTime();

    protected String identityPoolId = null;
    protected String authRoleArn = null;
    protected String authPolicyArn = null;
    protected String unAuthRoleArn = null;
    protected String unAuthPolicyArn = null;


    private final AWSCredentialsProvider credentialsProvider;

    // this client is used to access the cognito Identity Pool
    private final AmazonCognitoIdentity identityClient;

    // this client is used to access IAM in order to create ROLES and Policies
    private final AmazonIdentityManagement iamClient;

    private final CognitoConfig cognitoConfig;

    @Autowired
    public IdentityPoolBootstrapper(AWSCredentialsProvider credentialsProvider, CognitoConfig cognitoConfig) {
        this.cognitoConfig = cognitoConfig;
        this.credentialsProvider = credentialsProvider;
        this.identityClient = buildIdentityPoolClient();
        log.debug("cognito identity pool client created");
        this.iamClient = buildIamClient();
        log.debug("IAM client created");
    }

    // builds a client to access a cognito Identity pool
    private AmazonCognitoIdentity buildIdentityPoolClient() {
        return AmazonCognitoIdentityClientBuilder
                .standard()
                .withRegion(cognitoConfig.getRegion())
                .withCredentials(credentialsProvider)
                .build();
    }

    // builds a client that can access IAM
    private AmazonIdentityManagement buildIamClient() {
        return AmazonIdentityManagementClientBuilder
                .standard()
                .withRegion(cognitoConfig.getRegion())
                .withCredentials(credentialsProvider)
                .build();
    }

    // creates a IAM role for Cognito authenticated users, with an inline policy that gives
    // them permission to make ListUsers requests and to access the identity pool, and cognito sync
    private CreateRoleResult createAuthRoleAndPolicy(String userPoolArn, String identityPoolId) {
        // create an authenticated role
        CreateRoleRequest roleReq = new CreateRoleRequest()
                .withRoleName(AUTHENTICATED_ROLE_NAME)
                .withDescription("blogen role for authenticated users of the identity pool")
                .withAssumeRolePolicyDocument(Policy.buildAuthenticatedRole(identityPoolId));

        CreateRoleResult roleRes = this.iamClient.createRole(roleReq);
        log.debug("created blogen authenticated role with arn {}", roleRes.getRole().getArn());

        // create a policy for the role
        CreatePolicyResult polRes = this.iamClient.createPolicy(
                new CreatePolicyRequest()
                        .withPolicyName(AUTHENTICATED_ROLE_POLICY_NAME)
                        .withPolicyDocument(Policy.buildAuthenticatedPolicy(userPoolArn)));
        this.authPolicyArn = polRes.getPolicy().getArn();
        log.debug("created blogen authenticated policy with id {}", polRes.getPolicy().getPolicyId());

        AttachRolePolicyResult attRes = this.iamClient.attachRolePolicy(new AttachRolePolicyRequest()
                .withPolicyArn(polRes.getPolicy().getArn())
                .withRoleName(roleRes.getRole().getRoleName()));
        log.debug("attached authenticated role {} to policy {}", roleRes.getRole().getRoleName(), polRes.getPolicy().getPolicyId());

        return roleRes;
    }

    private CreateRoleResult createUnAuthRoleAndPolicy(String userPoolArn, String identityPoolId) {
        // create an authenticated role
        CreateRoleRequest roleReq = new CreateRoleRequest()
                .withRoleName(UNAUTHENTICATED_ROLE_NAME)
                .withDescription("blogen role for unauthenticated users of the identity pool")
                .withAssumeRolePolicyDocument(Policy.buildUnAuthenticatedRole(identityPoolId));

        CreateRoleResult roleRes = this.iamClient.createRole(roleReq);
        log.debug("created blogen unauthenticated role with arn {}", roleRes.getRole().getArn());

        // create a policy for the role
        CreatePolicyResult polRes = this.iamClient.createPolicy(
                new CreatePolicyRequest()
                        .withPolicyName(UNAUTHENTICATED_ROLE_POLICY_NAME)
                        .withPolicyDocument(Policy.buildUnauthenticatedPolicy(userPoolArn)));
        this.unAuthPolicyArn = polRes.getPolicy().getArn();
        log.debug("created blogen unauthenticated policy with id {}", polRes.getPolicy().getPolicyId());

        AttachRolePolicyResult attRes = this.iamClient.attachRolePolicy(new AttachRolePolicyRequest()
                .withPolicyArn(polRes.getPolicy().getArn())
                .withRoleName(roleRes.getRole().getRoleName()));
        log.debug("attached unauthenticated role {} to policy {}", roleRes.getRole().getRoleName(), polRes.getPolicy().getPolicyId());

        return roleRes;
    }

    private CreateIdentityPoolResult createIdentityPool(
            String userPoolId,
            String appClientId,
            String userPoolRegion
    ) {
        // we must explicitly build the provider name string for our cognito user pool
        String userPoolProviderName = String.format("cognito-idp.%s.amazonaws.com/%s", userPoolRegion, userPoolId);
        log.debug("identity pool provider name string is {}", userPoolProviderName);

        CreateIdentityPoolRequest req = new CreateIdentityPoolRequest()
                .withIdentityPoolName(IDENTITY_POOL_NAME)
                //.withAllowUnauthenticatedIdentities(true)
                .withCognitoIdentityProviders(new CognitoIdentityProvider()
                        .withProviderName(userPoolProviderName)
                        .withClientId(appClientId)
                );

        CreateIdentityPoolResult res = this.identityClient.createIdentityPool(req);
        log.debug("identity pool created with id {}", res.getIdentityPoolId());

        return res;
    }

    public String getIdentityPoolId() {
        return identityPoolId;
    }

    public String getAuthRoleArn() {
        return authRoleArn;
    }

    public String getUnAuthRoleArn() {
        return unAuthRoleArn;
    }

    public void bootstrap(String userPoolRegion, String userPoolId, String userPoolArn, String appClientId) {
        log.info("starting identity pool bootstrap...");
        // create the identity pool
        CreateIdentityPoolResult res = createIdentityPool(userPoolId, appClientId, userPoolRegion);
        this.identityPoolId = res.getIdentityPoolId();

        // create a role for unauthenticated users
        CreateRoleResult unAuthRoleRes = createUnAuthRoleAndPolicy(userPoolArn, this.identityPoolId);
        this.unAuthRoleArn = unAuthRoleRes.getRole().getArn();

        // create a role for authenticated users
        CreateRoleResult authRoleRes = createAuthRoleAndPolicy(userPoolArn, this.identityPoolId);
        this.authRoleArn = authRoleRes.getRole().getArn();

        // attach roles to the pool
        // build the map that maps authenticated and unauthenticated roles to their respective ARNs in IAM
        HashMap<String,String> roleMap = new HashMap<>();
        roleMap.put("authenticated", authRoleArn);
        roleMap.put("unauthenticated", unAuthRoleArn);
        this.identityClient.setIdentityPoolRoles(
                new SetIdentityPoolRolesRequest()
                        .withIdentityPoolId(res.getIdentityPoolId())
                        .withRoles(roleMap));
        log.debug("authenticated and unauthenticated roles added to identity pool {}", res.getIdentityPoolId());

        cognitoConfig.setIdentityPoolId(this.identityPoolId);
        log.info("finished identity pool bootstrap");
    }

    /**
     * deletes the resources that were bootstrapped for this identity pool:
     * - the identity pool
     * - the authenticated role
     * - the unauthenticated role
     */
    @PreDestroy
    private void deleteResources() {
        log.info("deleting identity pool resources...");

        // delete identity pool
        try {
            this.identityClient.deleteIdentityPool(new DeleteIdentityPoolRequest().withIdentityPoolId(this.identityPoolId));
            log.debug("identity pool deleted");
        } catch (Exception e) {
            log.debug("could not delete identity pool {} exception {}", this.identityPoolId, e.getMessage());
        }

        // detach authenticated policy from its role
        try {
            this.iamClient.detachRolePolicy(new DetachRolePolicyRequest()
                    .withPolicyArn(this.authPolicyArn)
                    .withRoleName(AUTHENTICATED_ROLE_NAME));
            log.debug("detached policy {} from role {}", AUTHENTICATED_ROLE_POLICY_NAME, AUTHENTICATED_ROLE_NAME);
        } catch (Exception e) {
            log.debug("could not detach policy {} from role {}  exception:{}", AUTHENTICATED_ROLE_POLICY_NAME, AUTHENTICATED_ROLE_NAME, e.getMessage());
        }

        // detach unauthenticated policy from its role
        try {
            this.iamClient.detachRolePolicy(new DetachRolePolicyRequest()
                    .withPolicyArn(this.unAuthPolicyArn)
                    .withRoleName(UNAUTHENTICATED_ROLE_NAME));
            log.debug("detached policy {} from role {}", UNAUTHENTICATED_ROLE_POLICY_NAME, UNAUTHENTICATED_ROLE_NAME);
        } catch (Exception e) {
            log.debug("could not detach policy {} from role {}  exception:{}", UNAUTHENTICATED_ROLE_POLICY_NAME, UNAUTHENTICATED_ROLE_NAME, e.getMessage());
        }

        // delete authenticated policy
        try {
            this.iamClient.deletePolicy(new DeletePolicyRequest().withPolicyArn(this.authPolicyArn));
            log.debug("deleted authenticated user policy {}", AUTHENTICATED_ROLE_POLICY_NAME);
        } catch (Exception e) {
            log.debug("could not delete authenticated user policy {} exception {}", AUTHENTICATED_ROLE_POLICY_NAME, e.getMessage());
        }

        // delete unauthenticated policy
        try {
            this.iamClient.deletePolicy(new DeletePolicyRequest().withPolicyArn(this.unAuthPolicyArn));
            log.debug("deleted unauthenticated user policy {}", UNAUTHENTICATED_ROLE_POLICY_NAME);
        } catch (Exception e) {
            log.debug("could not delete unauthenticated user policy {} exception {}", AUTHENTICATED_ROLE_POLICY_NAME, e.getMessage());
        }

        // delete authenticated role
        try {
            this.iamClient.deleteRole(new DeleteRoleRequest().withRoleName(AUTHENTICATED_ROLE_NAME));
            log.debug("deleted authenticated role {}", AUTHENTICATED_ROLE_NAME);
        } catch (Exception e) {
            log.debug("could not delete authenticated role {} exception {}", AUTHENTICATED_ROLE_NAME, e.getMessage());
        }

        // delete unauthenticated role
        try {
            this.iamClient.deleteRole(new DeleteRoleRequest().withRoleName(UNAUTHENTICATED_ROLE_NAME));
            log.debug("deleted unauthenticated role {}", UNAUTHENTICATED_ROLE_NAME);
        } catch (Exception e) {
            log.debug("could not delete unauthenticated role {} exception {}", UNAUTHENTICATED_ROLE_NAME, e.getMessage());
        }

        log.info("identity pool resource deletion finished");
    }
}
