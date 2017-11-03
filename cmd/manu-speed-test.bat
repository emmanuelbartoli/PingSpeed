@echo off
set PATH=C:\Users\emman\Dev\java\jdk1.8.0_111\bin;%PATH%
set PROJECT_HOME=C:\Users\emman\Dev\java\projects\PingSpeed\
set LIB_HOME=%PROJECT_HOME%\target\lib
set BIN_HOME=%PROJECT_HOME%\target
set CONF_HOME=C:\Users\emman\Dev\java\conf
set MY_CLASSPATH=%BIN_HOME%\manu-ping-speed-1.0.jar;%MY_CLASSPATH%;
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\commons-logging-1.2.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\log4j-1.2.17.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\commons-lang3-3.6.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\mysql-connector-java-5.1.42.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\http-endec-1.04.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%LIB_HOME%\jspeedtest-1.31.2.jar

start /min javaw -cp %MY_CLASSPATH% fr.manu.speed.test.JSpeedTest %CONF_HOME%\speedtest.properties

