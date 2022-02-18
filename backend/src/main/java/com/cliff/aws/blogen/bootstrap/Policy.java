package com.cliff.aws.blogen.bootstrap;

/**
 * Policy string used by the Cognito Identity Pool for Unauthenticated amd Authenticated users.
 * These policies will grant users (clients) that ability to make "ListUsers" api calls against
 * our cognito user pool
 */
public class Policy {


    public static String buildAuthenticatedRole(String identityPoolId) {
        return String.format("{\"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\n" +
                "        \"Federated\": \"cognito-identity.amazonaws.com\"\n" +
                "      },\n" +
                "      \"Action\": \"sts:AssumeRoleWithWebIdentity\",\n" +
                "      \"Condition\": {\n" +
                "        \"StringEquals\": {\n" +
                "          \"cognito-identity.amazonaws.com:aud\": \"%s\"\n" +
                "        },\n" +
                "        \"ForAnyValue:StringLike\": {\n" +
                "          \"cognito-identity.amazonaws.com:amr\": \"authenticated\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]}", identityPoolId);
    }

    public static String buildUnAuthenticatedRole(String identityPoolId) {
        return String.format("{\"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\n" +
                "        \"Federated\": \"cognito-identity.amazonaws.com\"\n" +
                "      },\n" +
                "      \"Action\": \"sts:AssumeRoleWithWebIdentity\",\n" +
                "      \"Condition\": {\n" +
                "        \"StringEquals\": {\n" +
                "          \"cognito-identity.amazonaws.com:aud\": \"%s\"\n" +
                "        },\n" +
                "        \"ForAnyValue:StringLike\": {\n" +
                "          \"cognito-identity.amazonaws.com:amr\": \"unauthenticated\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]}", identityPoolId);
    }

    /**
     * returns an IAM Policy string that gives unauthenticated clients the ability
     * to make "ListUser" requests against the cognito user pool identified
     * by the userPoolArn
     *
     * @param userPoolArn - ARN of the user pool that clients will be able to make ListUsers
     *                    requests against
     */
    public static String buildUnauthenticatedPolicy(String userPoolArn) {
        return String.format("{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"mobileanalytics:PutEvents\",\n" +
                "                \"cognito-sync:*\"\n" +
                "            ],\n" +
                "            \"Resource\": \"*\",\n" +
                "            \"Effect\": \"Allow\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"cognito-idp:ListUsers\"\n" +
                "            ],\n" +
                "            \"Resource\": \"%s\",\n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", userPoolArn);
    }

    /**
     * returns an IAM policy string that gives authenticated clients permission
     * to use mobileAnalytics PutEvents, all cognito-sync, and all cognito federated identity.
     * Additionally gives clients the ability to make ListUsers api requests against the user Pool
     * identified by the given arn
     * @param userPoolArn - ARN of the user pool that clients will be able to make ListUsers requests against
     */
    public static String buildAuthenticatedPolicy(String userPoolArn) {
        return String.format("{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"mobileanalytics:PutEvents\",\n" +
                "                \"cognito-sync:*\",\n" +
                "                \"cognito-identity:*\"\n" +
                "            ],\n" +
                "            \"Resource\": \"*\",\n" +
                "            \"Effect\": \"Allow\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"cognito-idp:ListUsers\"\n" +
                "            ],\n" +
                "            \"Resource\": \"%s\",\n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", userPoolArn);
    }
}
