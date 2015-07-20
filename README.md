RFID
====

Currently the bibs RFID build holds both the bibs server and the rfid server (though this may not be permanent).

Branch Structure:
Master:
  - This branch holds releases and release candidates

Develop:
  - This branch holds merges and commits of new development features

Build types:
  - All build types have a typename visible in /src/main/java/com/bibsmobile/util/BuildTypeUtil.java
  - These comes along with a set of properties that dictate what is shown in the UI:
    -- licensing -- whether the build uses license tokens for reads
    -- rfid -- whether this is an rfid included build
    -- registration -- whether this is the live registration build for accepting payments/results

Build Servers:
 - Carpunch: This is the semistable engineering build server. This builds off of the develop branch
 - Sakebomb: This is a private build server for so/galen. Builds are triggered from their branches

Live Servers:
 - overmind: This is the current test server for registration builds

====

Requirements (Running):
 - JRE 1.7
 - Tomcat v7.0.49 or greater. Currently versions up to 7.0.61 are verified.
 - /data directory set with read/write permission to tomcat user
 - MySQL version 5.5+

Requirements (Development):
 - JDK 1.7
 - Tomcat v7.0.49+
 - Maven 3.2
 - MySQL 5.5+
 - /data directory set with read/write permissions to tomcat user

====

Deploying a server build (tomcat):
 - With tomcat stopped, remove the old bibs-server.war and exploded bibs-server directories (rm -rf path/to/webapps/bibs-server*)
 - Make sure the war file is named bibs-server.war, move to the webapps directory of the project
 - Start tomcat. After server deploys, it should be accessible at hostname:8080/bibs-server/ (e.g. http://localhost:8080/bibs-server/)

====
Building the documentation:
Documentation for this project is held in a javadoc and in an apidoc. To build the apidoc, you will need npm (https://nodejs.org/) and to download and install apidoc (npm install apidoc -g). To generate documentation for the project, use the following command from the base directory of the project:

apidoc -i src -o apidoc

Generated documentation will appear in the apidoc folder, and can be opened by viewing index.html in a browser.
