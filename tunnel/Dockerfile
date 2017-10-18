FROM tomcat:8.5.23-jre8

COPY target/tunnel /usr/local/tomcat/webapps/tunnel
COPY conf/syscxp.properties.example /usr/local/tomcat/webapps/tunnel/WEB-INF/classes/syscxp.properties

CMD ["catalina.sh", "run"]
