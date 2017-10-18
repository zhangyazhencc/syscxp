FROM tomcat:8.5.23-jre8

COPY target/account /usr/local/tomcat/webapps/account
COPY conf/syscxp.properties.example /usr/local/tomcat/webapps/account/WEB-INF/classes/syscxp.properties

CMD ["catalina.sh", "run"]
