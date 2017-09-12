#!/bin/sh
# 
# build the ping html chart
#
# Emmanuel Bartoli
# Creation : 20/08/2017
#

# Log the environment configuration
. $HOME/cfg/manu.cfg


################
## Constants  ##
################

# This script
this_script=`basename $0`


# Print info with date
info()
{
  echo `date +"%d/%m/%Y %H:%M:%S"` "$1"
}

# Print info with script name + date
script_info()
{
  echo "$this_script" `date +"%d/%m/%Y %H:%M:%S"` "$1"
}

# Start function
sh_start()
{
  echo
  script_info "---------------------- START ----------------------"
}

# Exit function
sh_exit()
{
  retcode=$1
  echo
  echo "Exit with the return code $retcode"
  echo
  script_info "---------------------- END ------------------------"
  echo
  exit $retcode
}


# Start
sh_start

echo
echo "Build the ping html chart..."
echo

# Build the ping html chart
MY_CLASSPATH=$HOME/java/bin/manu-ping-speed-1.0.jar:$HOME/java/lib/ftp4j-1.7.2.jar:$HOME/java/lib/commons-io-2.5.jar:$HOME/java/lib/log4j-1.2.17.jar:$HOME/java/lib/commons-logging-1.2.jar:$HOME/java/lib/commons-lang3-3.6.jar:$HOME/java/lib/mysql-connector-java-5.1.42-bin.jar
java -Xmx256m -Xms128m -cp $MY_CLASSPATH fr.manu.ping.chart.BuildPingChart $HOME/java/conf/ping.properties

sh_exit $?

