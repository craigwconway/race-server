#!/bin/bash
scp target/bibs-server-0.1.0.BUILD-SNAPSHOT.war circleci@104.154.32.45:~/bibs-server.war
scp deployment/test_deploy_steps.sh circleci@107.178.219.146:~
ssh circleci@104.154.32.45 -C sudo ./test_deploy_steps.sh
curl -X POST --data-urlencode 'payload={"channel": "#engineering", "username": "ShrugBot", "text": "A build is being pushed to <http://104.154.32.45:8080/bibs-server/|sake-bomb>. It will be ready for testing shortly.", "icon_emoji": ":ghost:"}' https://hooks.slack.com/services/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
