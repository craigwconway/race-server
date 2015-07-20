#!/bin/bash
scp target/bibs-server-0.1.0.BUILD-SNAPSHOT.war circleci@bibslabs.co:~/bibs-server.war
ssh circleci@bibslabs.co -C sudo cp bibs-server.war /var/lib/tomcat6/webapps/bibs-server.war
curl -s --user 'api:key-09244ca92420306315f8bf76f587f7fa'  https://api.mailgun.net/v2/bibsmobile.com/messages -F from='ShrugBot <galen@bibsmobile.com>'  -F to=patrick@bibsmobile.com -F to=galen@bibsmobile.com -F to=brandon@bibsmobile.com -F to=bryan@bibsmobile.com -F subject='Bibslabs Server Deploy Notification' -F text='An RFID build has been deployed to bibslabs.co from CircleCI'
