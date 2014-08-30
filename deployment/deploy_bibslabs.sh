#!/bin/bash
scp target/bibs-server-0.1.0.BUILD-SNAPSHOT.war circleci@bibslabs.co:~/bibs-server.war
ssh circleci@bibslabs.co -C sudo cp bibs-server.war /var/lib/tomcat6/webapps/bibs-server.war
