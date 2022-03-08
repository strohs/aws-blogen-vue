package com.cliff.aws.blogen.api.v1.services;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.cliff.aws.blogen.config.CognitoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {


    private final AWSCognitoIdentityProvider client;

    private final CognitoConfig cognitoConfig;

    @Autowired
    public RegistrationServiceImpl(AWSCognitoIdentityProvider client, CognitoConfig cognitoConfig) {
        this.client = client;
        this.cognitoConfig = cognitoConfig;
    }

    @Override
    public void addUserToGroup(String userId, String roleName) {

        // add user to the specified cognito group
        AdminAddUserToGroupRequest addReq = new AdminAddUserToGroupRequest()
                .withGroupName(roleName)
                .withUsername(userId)
                .withUserPoolId(cognitoConfig.getUserPoolId());
        client.adminAddUserToGroup(addReq);

        log.debug("added user {} to role {}", userId, roleName);
    }
}
