# Blogen Post Confirmation Lambda

This directory contains a sample AWS Lambda that was used to explore the interoperation between Cognito and
AWS Lambda.  
Its only purpose is to add newly registered users to the "User" group within the Blogen Cognito User Pool. This group
information is sent with each JWT issued by cognito, and is used by spring boot to authorize use of the
rest api.

The lambda itself is triggered after a user successfully enters their confirmation code into the sign-up form during new
user registration. This is called a Post Confirmation Trigger.

NOTE that you should only install this lambda if you plan to run blogen in a "production" like environment, 
i.e. non 'dev'.
If you are using the "dev" profile, then the Vue frontend will perform this post confirmation step after a new user
enters their confirmation code. This was done in order to reduce the amount of AWS Resources created.


The lambda code is written using the AWS Lambda Java 8 SDK.
It uses Goggle GSON library to deserialize the incoming JSON from cognito, and it uses LOG4J to format logging 
data for output into CloudWatch logs.


## Building
Use maven to build the lambda jar with the following command:

    mvn clean install

This will build the lambda jar file located at: `target/blogen-lambdas-0.0.1.jar`


## Example AWS CLI Scripts
In addition to the java source code, some example scripts are included that can be used to create and run the lambda
from the command line. Here is what included in the example directory

- `examples`
  - `create-lambda.sh` uses the aws cli to create this lambda function
  - `create-lambda.cmd` the windows cmd version of `create-lambda.sh`
  - `lambda-invoke.sh` used the aws cli to invoke the lambda function
  - `postConfirmation.json` and example of the JSON format the cognito will send into the lambda function after a 
  new user enters their confirmation code

