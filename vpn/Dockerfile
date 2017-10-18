FROM tomcat:8.5.23-jre8

COPY target/vpn /usr/local/tomcat/webapps/vpn
COPY conf/syscxp.properties.example /usr/local/tomcat/webapps/vpn/WEB-INF/classes/syscxp.properties

CMD ["catalina.sh", "run"]
