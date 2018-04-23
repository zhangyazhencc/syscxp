#!/bin/sh
set -u

cd ..
mvn -DskipTests clean install -Dmaven.test.skip=true
cd -

#### mvn war:war
### rm -rf $CATALINA_HOME/webapps/syscxp
### rm -f $CATALINA_HOME/webapps/syscxp.war
### cp target/syscxp.war $CATALINA_HOME/webapps/syscxp.war

###echo "Deployed syscxp.war to $CATALINA_HOME/webapps/syscxp.war, run 'mvn -pl build -P debug exec:exec -Ddebug' to start"
