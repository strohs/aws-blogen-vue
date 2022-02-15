AWS-Blogen-Vue.js
======================================================================================================
This is an Amazon Web Services (AWS) version of my [Blogen](https://github.com/strohs/springboot-blogen) demo website. 
Blogen is a very simple, micro-blogging site / message board, that lets users post short text "blurbs" along 
  with a (optional) link to an image. The site provides user sign-up/sign-in via AWS Cognito integration. Users can 
  create new threads of discussion and perform CRUD ops on their threads.  They can also update their user profile 
  information after its been created.  
 
This version of Blogen uses Vue.js with Bootstrap 4 on the frontend, while the backend has been changed to use (mostly)
AWS. Specifically, AWS Cognito is used for user management, DynamoDB is used to store user messages, AWS
Lambda is used to perform some post user registration steps. 
Spring Boot 2 is still used, but it mainly serves as the controller for the Blogen REST API. 
The Spring Boot servers are run on Elastic Beanstalk.


## Blogen Project Directory Structure
This is a Maven "multi-module" project, inspired by this blog post 
[here](https://blog.codecentric.de/en/2018/04/spring-boot-vuejs/)  

The project structure is as follows:
* **backend** module - contains the Spring Boot backend code. Spring Boot is used to run the Blogen REST API 
that performs the CRUD operations on the Blogen table in DynamoDB. Spring Boot is also used to validate Cognito 
ID Tokens that must be sent with each request to the REST API. Aditionally, Spring Boot is also used as a web server, 
it serves the initial Vue.js index page and associated javascript files
* **frontend** module - contains all the Vue.js code for the Blogen frontend
* **lambdas** module - contains AWS lambda function code used by Blogen.
* **aws** directory - this directory contains the CloudFormation templates for deploying Blogen onto AWS as well as some
script files for bootstrapping data into dynamoDB (located at `/aws/dynamodb`)
* **native-libs** - this folder contains the sql-lite libraries required by dynamodb-local. These are only used
during development/testing
* **.mvn** - contains maven wrapper .jar used by `mvnw` (Linux/MacOS) and `mvnw.cmd` (Windows). Maven wrapper is a
local version of Apache Maven for users that don't have maven installed on their machines. 


## Blogen Technology Stack Overview

### Vue.JS and Bootstrap4
The Blogen frontend was written using Vue.js and styled with Bootstrap 4 
(via the [BootstrapVue](https://bootstrap-vue.js.org/)) project. API call to Cognito are done using the
[AWS Amplify API](https://aws-amplify.github.io/) and some occasional calls to 
[AWS SDK for JavaScript](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/index.html).
REST API calls are made (using axios) to the Blogen backend server (Spring Boot) to retrieve user threads and posts.
  
### AWS Cognito
Amazon Cognito lets you add user sign-up, sign-in, and access control to your web and/or mobile apps. Blogen is using
cognito for user authentication/authorization and to store user details in a Cognito User Pool 

#### Blogen Cognito User Pool
* A Cognito *User Pool* is used to store user registration details. The pool has been configured to allow users
to sign into Blogen using their email address.
* The user pool also stores the following user attributes:
    * first-name
    * last-name
    * password
    * preferred-username 
    * *avatar image name* - a custom attribute that stores the avatar image filename that a user has chosen for themselves

#### Blogen Cognito Identity Pool
* A Cognito identity pool is used to federate authenticated and unauthenticated users with AWS and give them 
temporary access to AWS resources. An identity pool is being used because Blogen users need access to some Cognito 
API Calls, specifically the *ListUsers* API so that users can query the user pool in order to find out which preferred-usernames 
and email addresses have been taken.

### AWS Lambda
Blogen currently uses AWS Lambda to assign newly registered Blogen users to the "User"group within Cognito. 
The lambda is written using the AWS Java SDK and is triggered by a Cognito *Post Registration Trigger*.
This trigger runs when a user signs-up for Blogen and then completes their registration by entering the sign-up 
confirmation code that gets emailed to them.

### Spring Boot
Spring Boot 2 is used as the application server. It performs four roles:
- serves the web resources for the front-end (html,javascript,css,images)
- serves the (secured) Blogen REST API requests
- makes API calls to DynamoDB to retrieve data
- validates JSON Web Tokens (JWT) passed to the REST API
  -Blogen uses the *ID-Tokens* issued by Cognito for REST API Access

### Elastic Beanstalk (EB)
EB is used to run the Spring Boot application servers in EC2. Currently Blogen uses the default Java 8 environment 
provided by EB so that the embedded Spring Boot tomcat server can be used without having to configure a separate 
tomcat server in EB. The EB environment itself is configured to start two t2.micro instances running behind a 
single load balancer.

### DynamoDB
Blogen stores all data used by the website (minus user details) in a single DynamoDB table. It primarily stores
the details of a user's threads and posts, which consist of the following attributes:
- threadID, postID, post-title, post-text, image-url, userID, category-name

The Blogen table also stores other "types" of items as explained below..

#### Blogen Primary Key Structure(s)
The Blogen DynamoDB table uses a composite primary key (range + hash) to store various items. There are four "types" of items 
being stored in a single table. Each of these items use a different primary key "structure". 
The structure of the primary key for each item type is as follows:

* thread start item - stores the first "post" of a thread. Its primary key structure is as follows:
    * hash key - a UUID String (named `THREAD-UUID` see below)
    * range key - a composite string that starts with the string literal "POST_" + `TIMESTAMP` + `THREAD-UUID`
        * note: in the range key, the hash key's uuid is contained as part of the range key so that the first post
         in a thread can be found in queries
* post item - a "child" post of a thread. Its primary key structure is as follows:
    * hash key - a child posts hash key will equal its starting thread UUID (`THREAD-UUID`)
    * range key - a child posts range key is a composite string that starts with the string literal "POST_" + `TIMESTAMP` + `UUID`
        * `TIMESTAMP` is a ISO 8601 date time string of the form *2018-10-18T19:02:41.627Z*
        * `UUID` is a UUID string 
* category name item - stores the name of a category that posts can belong to, i.e. "Technology","Business"...
    * hash key - a string containing the category name
    * range key - will always equal the constant string "CATEGORY"
* avatar file name item - the name of an avatar file stored on the Blogen app server, i.e. "avatar1.jpg"
    * hash key - a String containing the actual avatar file name
    * range key - will always equal the constant string "AVATAR" 

| item type stored      | Hash Key (*partition key*) | Range Key (*sort key*)         | Attribute1 | Attribute2 | Attribute3     | Attribute4 | Attribute5 | Attribute6         | Attribute7    | Attribute8  | Attribute9 |
|-----------------------|----------------------------|--------------------------------|------------|------------|----------------|------------|------------|--------------------|---------------|-------------|------------|
| thread start item     | `THREAD-UUID`              | POST_`TIMESTAMP`_`THREAD-UUID` | *title*    | *text*     | *categoryName* | *imageUrl* | *userID*   | *updatedTimestamp* | `THREAD-UUID` | `POST-UUID` |
| post item             | `THREAD-UUID`              | POST_`TIMESTAMP`_`POST_UUID`   | *title*    | *text*     | *categoryName* | *imageUrl* | *userID*   | *updatedTimestamp* | `THREAD-UUID` | `POST-UUID` |
| category name item    | *categoryName*             | **CATEGORY**                   | 
| avatar file name item | *avatarFileName*           | **AVATAR**                     |


| Global Secondary Indices Name | GSI Hash Key   | GSI Range Key  | Notes                                                                                                                             |
|-------------------------------|----------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------|
| categoryNameIndex             | *categoryName* | *primaryRange* | this GSI is used to query posts by category name                                                                                  |
| userIdIndex                   | *userId*       | *primaryRange* | this GSI is used to find all posts made by a specific user                                                                        |
| rangeIndex                    | *primaryRange* | *primaryHash*  | this GSI is used in a number of queries: to get recently posted threads (by timestamp) and to get a list of all avatar file names |


## Blogen Set-Up and Deployment onto AWS

### Prerequisites
* Java installed on your machine, at least java 8, newer version of java were not tested 
* You must have a pre-existing AWS account and have the AWS command line interface (CLI) installed and configured. You
should also be familiar with using the AWS console.
* If you plan to do any development on the frontend, you must have node.js and npm installed, at least version 8.11 of 
node is recommended. The [/frontend/pom.xml](./frontend/pom.xml) is configured to download and temporarily install 
node 8.11.3 so that users that don't have node installed can compile and run the frontend code when using maven
* If you don't have maven installed locally on your machine, replace the `mvn` commands below with `mvnw` (for *nix systems) 
or `mvnw.cmd` (on windows systems)
* Be aware that AWS resources will be created that will incur charges. Specifically the Elastic Beanstalk template 
used in this project will create two t2.micro instances running behind a load balancer

1. Create the Blogen DynamoDB table 
    1. run the `aws/blogen-dynamodb.yaml` template in CloudFormation. This will create the *Blogen* table
    2. cd into the `/aws/dynamodb/` directory and run the `bootstrap-dynamodb` script. This script will load some 
    default category names and avatar file names into the Blogen table
    
2. Build and Upload the Blogen lambda jar file to AWS S3
    1. build the lambda .jar file by running `mvn install -pl lambdas` from the project root directory
        * this will create a .jar artifact in `/lambdas/target/blogen-lambdas-0.0.1.jar`
        * this lambda function listens for a Cognito `PostConfirmationTrigger` and adds newly 
        signed up users to a "User" group within the Blogen User Pool. The handler for this function is located at:
        [com.cliff.aws.lambda.blogen.AddNewUserToUserGroup](./lambdas/src/main/java/com/cliff/aws/lambda/blogen/AddNewUserToUserGroup.java)
    2. upload the `blogen-lambdas-0.0.1.jar` file to a bucket in S3 (with versioning enabled). This bucket will be used
    later on by our CloudFormation Template(s)

3. Create the Blogen Cognito resources
    1. create the Cognito User Pool, Identity Pool and groups by running the `/aws/blogen-cognito.yaml` template 
    in CloudFormation.
        * the template will create the following:
            * an "Admin" user (make sure to enter a valid email address as the admin's temporary password will be
            emailed to it)
            * a "User" group and "Admin Group"
            * deploy the lambda function from step 2 into AWS with a default memory size of 246MB 
            (make sure you enter the correct bucket name that contains the lambda .jar file)
            * two roles in IAM: *blogen-unauthenticated* and *blogen-authenticated* that grants users of Blogen the 
            ability to lookup users in the Blogen user pool. 
    2. **NOTE**: there are currently a couple of known bugs between CloudFormation and Cognito that will require you to 
    perform the following steps manually in the Cognito Web console:
        1. You must manually add the Admin user to the "Admin" and "User" groups.
        2. You must manually re-add the lambda function created by this template to the user pool's *Post Confirmation
        Trigger*. The function will appear in the web console, and it seems properly configured, but it is not. You
        must manually reselect it and then click the *Save Changes* button

4. Build and deploy the Blogen Application Server(s)
    1. configure `frontend/.env` with the ID's of your Cognito User Pool, Identity Pool, App Client ID
        * you can find these IDs in the Cognito Web Console or in the CloudFormation web console when looking at the cognito 
        stack outputs
    2. likewise configure `backend/src/main/resources/application.properties` with the ID's of your User Pool, Identity
    Pool and the AWS region that your pools are located in 
    3. build the `/backend/target/blogen-appserver-0.0.1.jar` file by running `mvn install -pl backend`. This will 
    build the .jar file containing the Spring Boot code that serves the Blogen REST API and frontend resources
    4. upload `blogen-appserver-0.0.1.jar` to a bucket in S3 (that has versioning enabled). You can use the same bucket
    that contains the lambda .jar file if you wish
    5. deploy the blogen application servers to Elastic Beanstalk by running the `/aws/blogen-beanstalk.yaml` 
    CloudFormation template
        * this will create two blogen appservers (on t2.micro instances), running behind a load-balancer
    6. the URL for the website can be found in the Elastic Beanstalk web console for the newly created environment. 
    It will end with **elasticbeanstalk.com** and can be found in the upper right side of the console:
        * for example: `Environment ID: e-dgrp5zm4ts, URL: blogen.us-east-1.elasticbeanstalk.com`
    7. you should now be able to access the website in your browser via the URL in step 6. 
    **Note** that it will be a regular HTTP (unsecure) connection. The elastic beanstalk environment is configured 
    for HTTPS but you will need to configure your own domain and SSL certificate to point to the beanstalk environment
