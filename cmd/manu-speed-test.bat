@echo off
set PATH=C:\Users\emman\Dev\java\jdk1.8.0_111\bin;%PATH%
set MY_HOME=C:\Users\emman\Dev\java\projects\PingSpeed\cmd
set MY_CLASSPATH=%MY_HOME%\bin\manu-speed-test-1.0.jar;%MY_CLASSPATH%;
set MY_CLASSPATH=%MY_CLASSPATH%;%MY_HOME%\lib\commons-logging-1.2.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%MY_HOME%\lib\log4j-1.2.17.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%MY_HOME%\lib\commons-lang3-3.6.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%MY_HOME%\lib\mysql-connector-java-5.1.42-bin.jar
set MY_CLASSPATH=%MY_CLASSPATH%;%MY_HOME%\lib\bmartel-1.31.2.jar

start /min javaw -cp %MY_CLASSPATH% fr.manu.speed.test.JSpeedTest %MY_HOME%\conf\speedtest.properties


