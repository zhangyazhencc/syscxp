#!/bin/sh
set -u

cd ..
mvn -DskipTests clean install
cd -

### mvn war:war
### rm -rf $CATALINA_HOME/webapps/zstack
### rm -f $CATALINA_HOME/webapps/zstack.war
### cp target/zstack.war $CATALINA_HOME/webapps/zstack.war

echo "Deployed zstack.war to $CATALINA_HOME/webapps/zstack.war, run 'mvn -pl build -P debug exec:exec -Ddebug' to start"
