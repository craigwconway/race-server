#!/bin/bash
scp ../target/bibs-server-0.1.0-SNAPSHOT.war http://bibslabs.co:/var/lib/tomcat6/webapps/bibs-server.war
ssh bibslabs.co -C "sudo rm -rf /var/lib/tomcat6/webapps/bibs-server; sudo service tomcat6 restart"
