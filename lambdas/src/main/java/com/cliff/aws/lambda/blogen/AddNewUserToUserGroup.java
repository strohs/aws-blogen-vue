package com.cliff.aws.lambda.blogen;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.cliff.aws.lambda.blogen.event.CognitoEvent;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.cliff.aws.lambda.utils.StreamUtils.inputStreamToString;
import static com.cliff.aws.lambda.utils.StreamUtils.stringToOutputStream;

/**
 * Adds a newly confirmed user, to the 'User' group of the Blogen User Pool
 */
public class AddNewUserToUserGroup implements RequestStreamHandler {


    // Initialize the Log4j logger.
    static final Logger logger = LogManager.getLogger(AddNewUserToUserGroup.class);

    private static Gson gson = new Gson();

    private static final String USER_GROUP = "User";

    private AWSCognitoIdentityProvider client = AWSCognitoIdentityProviderClientBuilder.defaultClient();

    public void handleRequest (InputStream input, OutputStream output, Context context) throws IOException {

        // POJO that holds the Cognito PostConfirmation event data
        CognitoEvent pcEvent = new CognitoEvent();

        try {
            // convert input stream to string
            String inString = inputStreamToString(input, StandardCharsets.UTF_8.name());

            // convert JSON string into POJOs
            pcEvent = gson.fromJson(inString, CognitoEvent.class);
            String username = pcEvent.getUserName();
            String userPoolId = pcEvent.getUserPoolId();
            logger.info("adding username:{}  to User group in user pool:{}", username, userPoolId);

            // add user to "User" group
            AdminAddUserToGroupRequest addReq = new AdminAddUserToGroupRequest()
                    .withGroupName(USER_GROUP)
                    .withUsername(username)
                    .withUserPoolId(userPoolId);
            client.adminAddUserToGroup(addReq);

        } catch ( Exception e ) {
            logger.error(e.getMessage());
        } finally {
            stringToOutputStream(gson.toJson(pcEvent), output, StandardCharsets.UTF_8.name());
        }

    }


}
