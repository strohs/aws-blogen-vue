#!/bin/bash
#####################################################################
# ec2 user-data script for a blogen appserver
#   this script will install java8, uninstall java7,
#   and set some environment variables that are needed by spring boot
#
#####################################################################
# install java 8 and remove java 7
yum install -y java-1.8.0
yum remove -y java-1.7.0-openjdk

# update yum packages
yum update -y

# set environment variables needed by Spring Boot
cat > /etc/profile.d/load_env.sh << 'EOF'
export BLOGEN_SECURITY_JWT_AWS_USERPOOLID=us-east-1_USER_POOL_ID
export BLOGEN_SECURITY_JWT_AWS_IDENTITYPOOLID=us-east-1:IDENTITY_POOL_ID
export BLOGEN_SECURITY_JWT_AWS_REGION=us-east-1
# export SERVER_PORT=5000
EOF

# copy a file from S3
aws s3 cp s3://bucket/blogen.zip /home/ec2-user
