aws lambda create-function ^
--region us-east-1 ^
--function-name BlogenAddUserToGroup ^
--zip-file fileb://../target/blogen-lambdas-0.0.1.jar ^
--timeout 10 ^
--memory-size 256 ^
--role ROLE_ARN_HERE ^
--handler com.cliff.aws.lambda.blogen.AddNewUserToUserGroup ^
--runtime java8