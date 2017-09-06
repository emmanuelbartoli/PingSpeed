#!/bin/sh
# 
# Start the ping daemon
#
# Emmanuel Bartoli
# Creation : 20/08/2017
#

# Log the environment configuration
. $HOME/cfg/manu.cfg
LOG_FILE=$REPLOGS/ping_daemon.log
ERR_FILE=$REPLOGS/ping_daemon.err

################
## Constants  ##
################

# This script
this_script=`basename $0`

##################################################
# Max waiting time in seconds for a daemon start #
##################################################
MAX_WAIT_TIME=60

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

# Check if a process is started
check_prc_start()
{
    # Process to test
    PRC_TO_CHECK=$1

    # Start date
    START_DATE=`date +%s`

    # Return code
    return_code=1
    
    info "Test the stop of the process "$PRC_TO_CHECK
    while true
    do
        # get the current date
        current_date=`date +%s`	

        # Elapsed time
        elapsed_time=$(($current_date-$START_DATE))
        #info "Elapsed time="$elapsed_time
            
        # Exit from the loop if the max waiting time is reached
        if [ $elapsed_time -gt $MAX_WAIT_TIME ]
        then
            info $PRC_TO_CHECK" : still dead after a waiting time of $elapsed_time seconds !"
            return_code=1
            break
        fi

        # Get the number of processes	
        PRC_TO_CHECK_NB=`ps -edf| grep ${PRC_TO_CHECK} | grep -v grep | wc -l`
        #PRC_TO_CHECK_NB=`pgrep ${PRC_TO_CHECK} | wc -l`
        
        # Test the number of processes
        if [ $PRC_TO_CHECK_NB -gt 0 ]
        then
            info $PRC_TO_CHECK" : "$PRC_TO_CHECK_NB" alive process(es) after a waiting time of $elapsed_time seconds !"
            return_code=0
            break
        else
            info $PRC_TO_CHECK" is still dead after a waiting time of $elapsed_time seconds !"
        fi
        
        # Sleep for 5s			
        sleep 5
    done
    return $return_code
}

# Start
sh_start

echo
echo "Start the ping daemon..."
echo

# Test if the daemon is not running
echo "Status before start"
. $HOME/cmd/status_ping_daemon.sh 
if [ $DAEMON_PROCESS -gt 0 ] ; then
  echo
  echo "At least one ping daemon is running, it's not allowed to start another one !"
  sh_exit 0 
fi

# Store the existing logs
if [ -f $LOG_FILE ]; then
  BACKUP_FILE=$REPLOGS/`date +"%Y%m%d%H%M%S"`.`basename $LOG_FILE`
  mv $LOG_FILE $BACKUP_FILE
  gzip $BACKUP_FILE
fi

# Purge old logs
find $HOME/logs  -mtime +7 -type f -name "*.gz" -delete

# Set up the daemon status in the database with the TO_START value
mysql --user=$DB_USER --password=$DB_PASSWORD  --execute="update DAEMON_PARAMETERS set DAP_STATUS ='TO_START';COMMIT" $DB_NAME 

# Start the daemon
MY_CLASSPATH=$HOME/java/bin/manu-ping-daemon-1.0.jar:$HOME/java/lib/commons-io-2.5.jar:$HOME/java/lib/log4j-1.2.17.jar:$HOME/java/lib/commons-logging-1.2.jar:$HOME/java/lib/commons-lang3-3.6.jar:$HOME/java/lib/mysql-connector-java-5.1.42-bin.jar
nohup java -cp $MY_CLASSPATH fr.manu.ping.daemon.PingDaemon $HOME/java/conf/ping_daemon.properties > $LOG_FILE 2> $ERR_FILE &

echo
check_prc_start "manu-ping-daemon"
return_code=$?

# Check the status
echo
echo "Status after start"
. $HOME/cmd/status_ping_daemon.sh

sh_exit $return_code

