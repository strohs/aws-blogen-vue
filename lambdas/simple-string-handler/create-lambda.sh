#!/usr/bin/env bash
aws lambda create-function \
--region us-east-1 \
--function-name AddUserToUserGroup \
--zip-file fileb://../target/blogen-lambdas-0.0.1.jar \
--role arn:aws:iam::067678732759:role/lambda-cognito-role \
--handler com.cliff.aws.blogen.lambda.AddNewUserToUserGroup::handleRequest \
--runtime java8 \
--timeout 10 \
--memory-size 256

#{
#    "TracingConfig": {
#        "Mode": "PassThrough"
#    },
#    "CodeSha256": "WBJJzsrwzOdhuJmaCbZGP2DKpT1l4fb8IjhrNcEBnYo=",
#    "FunctionName": "AddUserToUserGroup",
#    "CodeSize": 8494958,
#    "RevisionId": "7e80bbbb-c414-43b5-984a-edb48cc45aa6",
#    "MemorySize": 128,
#    "FunctionArn": "arn:aws:lambda:us-east-1:067678732759:function:AddUserToUserGroup",
#    "Version": "$LATEST",
#    "Role": "arn:aws:iam::067678732759:role/lambda-cognito-role",
#    "Timeout": 10,
#    "LastModified": "2018-09-26T22:48:34.143+0000",
#    "Handler": "com.cliff.aws.blogen.lambda.AddNewUserToUserGroup::handleRequest",
#    "Runtime": "java8",
#    "Description": ""
#}