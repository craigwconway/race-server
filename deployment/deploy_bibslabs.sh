#!/bin/bash
scp ../target/bibs-server-0.1.0.BUILD-SNAPSHOT.war circleci@bibslabs.co:/var/lib/tomcat6/webapps/bibs-server.war
