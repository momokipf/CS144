#!/bin/bash 
rm Hello.class

javac -classpath $CATALINA_HOME/lib/servlet-api.jar Hello.java

cp Hello.java ./tmp/WEB-INF/classes

