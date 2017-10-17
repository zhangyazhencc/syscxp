FROM tomcat:8.5.23-jre8

COPY target/billing /usr/local/tomcat/webapps/billing
COPY conf/syscxp.properties.example /usr/local/tomcat/webapps/billing/WEB-INF/classes/syscxp.properties

CMD ["catalina.sh", "run"]
