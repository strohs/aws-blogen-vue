## code-deploy
The files in this folder where used to play around with AWS Code-Deploy and are not used. The goal was to see if Blogen
could be deployed as a service onto AWS Linux instances rather than a standalone .jar file. It turns out that it can,
as long as you configure the spring-boot-plugin to build an executable jar in /backend/pom.xml