FROM tomcat:8.5.23-jre8

COPY target/alarm /usr/local/tomcat/webapps/alarm
COPY conf/syscxp.properties.example /usr/local/tomcat/webapps/alarm/WEB-INF/classes/syscxp.properties

CMD ["catalina.sh", "run"]
