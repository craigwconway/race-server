sudo service tomcat7 stop;
mysql -uroot -e "drop database bibs"
mysql -uroot -e "create database bibs"
sudo rm -rf /var/lib/tomcat7/webapps/bibs-server*
sudo cp bibs-server.war /var/lib/tomcat7/webapps/bibs-server.war
sudo service tomcat7 start;
