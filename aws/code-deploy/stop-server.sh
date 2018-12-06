#!/bin/bash
echo 'Stopping Spring Boot Blogen AppServer'
cd '/home/ec2-user'
service blogen-appserver stop
rm -f /etc/init.d/blogen-appserver