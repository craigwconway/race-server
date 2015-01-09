#!/bin/bash
scp target/bibs-server-0.1.0.BUILD-SNAPSHOT.war circleci@107.178.219.146:~/bibs-server.war
scp target/test_deploy_steps.sh circleci@107.178.219.146:~
ssh circleci@107.178.219.146 -C sudo ./test_deploy_steps.sh
curl -s --user 'api:key-09244ca92420306315f8bf76f587f7fa'  https://api.mailgun.net/v2/bibsmobile.com/messages -F from='ShrugBot <galen@bibsmobile.com>'  -F to=patrick@bibsmobile.com -F to=galen@bibsmobile.com -F to=brandon@bibsmobile.com -F to=bryan@bibsmobile.com -F subject='Bibslabs Server Deploy Notification' -F text='An RFID build has been deployed to develop test server from CircleCI'
