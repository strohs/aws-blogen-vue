#!/usr/bin/env bash
aws lambda create-function \
--region us-east-1 \
--function-name AddUserToUserGroup \
--zip-file fileb://../target/blogen-lambdas-0.0.1.jar \
--role ROLE_ARN_HERE \
--handler com.cliff.aws.blogen.lambda.AddNewUserToUserGroup::handleRequest \
--runtime java8 \
--timeout 10 \
--memory-size 256

