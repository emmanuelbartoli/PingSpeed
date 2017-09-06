#!/bin/sh
# 
# Stop the ping daemon
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

#################################################
# Max waiting time in seconds for a daemon stop #
#################################################
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

# Check if a process is stopped
check_prc_stop()
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
            info $PRC_TO_CHECK" : still alive after a waiting time of $elapsed_time seconds !"
            return_code=1
            break
        fi

        # Get the number of processes	
        PRC_TO_CHECK_NB=`ps -edf| grep ${PRC_TO_CHECK} | grep -v grep | wc -l`
        #PRC_TO_CHECK_NB=`pgrep ${PRC_TO_CHECK} | wc -l`
        
        # Test the number of processes
        if [ $PRC_TO_CHECK_NB -gt 0 ]
        then
            info $PRC_TO_CHECK" : "$PRC_TO_CHECK_NB" process(es) still alive after a waiting time of $elapsed_time seconds !"
        else
            info $PRC_TO_CHECK" is dead after a waiting time of $elapsed_time seconds !"
            return_code=0
            break
        fi
        
        # Sleep for 5s			
        sleep 5
    done
    return $return_code
}

# Start
sh_start

echo
echo "Stop the ping daemon..."
echo

# Test if the daemon is not stopped
echo "Status before stop"
. $HOME/cmd/status_ping_daemon.sh
if [ $DAEMON_PROCESS -eq 0 ] ; then
  echo
  echo "No ping daemon is running, there is nothing to stop !"
  sh_exit 0 
fi

# Set up the daemon status in the database with the TO_STOP value
mysql --user=$DB_USER --password=$DB_PASSWORD  --execute="update DAEMON_PARAMETERS set DAP_STATUS ='TO_STOP';COMMIT" $DB_NAME 

echo
check_prc_stop "manu-ping-daemon"
return_code=$?

echo
echo "Status after stop"
. $HOME/cmd/status_ping_daemon.sh

# Exit
sh_exit $return_code

