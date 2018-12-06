#!/bin/bash
#
# NOTE: you must make an executable jar by first configuring the maven plugin. SEE /backend/pom.xml
#
echo 'Creating Blogen AppServer Service'
cd '/home/ec2-user'
chmod 500 blogen-appserver-0.0.1.jar
ln -s /home/ec2-user/blogen-appserver-0.0.1.jar /etc/init.d/blogen-appserver