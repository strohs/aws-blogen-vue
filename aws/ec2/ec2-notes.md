### Notes Amazon Linux AMIs in EC2
various notes for installing java and configuring EC2
instances to run spring boot embedded applications rather than Elastic Beanstalk
#### uninstall java 7 and install java 8 on Amazon Linux instances
1. `sudo yum install java-1.8.0`
2. `sudo yum remove java-1.7.0-openjdk`

#### copying a file from S3
* `aws s3 cp s3://<BUCKET_NAME_HERE>/FILE_NAME`

#### setting environment variables for an instance (using UserData)
* NOTE: when a EC2 UserData script is executed, the output is appended to the file `/var/log/cloud-init-output.log`
* Scripts placed in the folder `/etc/profile.d/` are run automatically when a new shell is launched. 
For example, we can create a script called `/etc/profile.d/load_env.sh` which exports the variables we need

example of creating UserData in CloudFormation template
```
Resources:  
  Instance:
    Type: AWS::EC2::Instance
    Properties:
      # ...
      UserData:
        Fn::Base64: 
          Fn::Sub: |
            #!/bin/bash
            
            # export account id and region on instance startup
            cat > /etc/profile.d/load_env.sh << 'EOF'
            
            export ACCOUNT_ID=${AWS::AccountId}
            export REGION=${AWS::Region}

EOF
```

#### running spring boot as a service on Linux
* more info [here](https://www.baeldung.com/spring-boot-app-as-a-service)
* this will configures a System V daemon
* you can optionally create a new user to run your app
    * `sudo useradd baeldung`
    * `sudo passwd baeldung`
    * `sudo chown baeldung:baeldung your-app.jar`
* make your .jar executable
* `sudo chmod 500 your-app.jar`
* `sudo ln -s /path/to/your-app.jar /etc/init.d/your-app`
    * The above command creates a symbolic link to your executable JAR file. You must use the full path to 
    your executable JAR file, otherwise, the symbolic link will not work properly
* This link enables you to start the application as a service:
    * `sudo service your-app start`
    * `sudo service your-app stop`
    * `sudo service your-app status`