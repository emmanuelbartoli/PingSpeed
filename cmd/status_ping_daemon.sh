#!/bin/sh

blueBLK()
{
  TEXT=$1
  echo "\033[5m\033[44m${TEXT}\033[0m"
}

pinkBLK()
{
  TEXT=$1
  echo "\033[5m\033[45m${TEXT}\033[0m"
}

redBLK()
{
  TEXT=$1
  echo "\033[5m\033[41m${TEXT}\033[0m"
}

blue()
{
  TEXT=$1
  echo "\033[34m\033[40m${TEXT}\033[0m"
}

pink()
{
  TEXT=$1
  echo "\035[5m\033[40m${TEXT}\033[0m"
}

red()
{
  TEXT=$1
  echo "\033[31m\033[40m${TEXT}\033[0m"
}


# Get data
export DAEMON_PROCESS=`ps -edf | grep manu-ping-daemon | grep -v "grep" | wc -l`
export DAEMON_STATUS=`mysql --user=manu --password=manu -sN --execute="select DAP_STATUS from DAEMON_PARAMETERS" manudb`
export DAEMON_SLEEP=`mysql --user=manu --password=manu -sN --execute="select DAP_SLEEP from DAEMON_PARAMETERS" manudb`
export DAEMON_INTERVAL=`mysql --user=manu --password=manu -sN --execute="select DAP_PING_INTERVAL from DAEMON_PARAMETERS" manudb`

# Colors
if [ "${DAEMON_STATUS}" = "RUNNING" ]; then
  DAEMON_STATUS=`blueBLK ${DAEMON_STATUS}`  
fi
if [ "${DAEMON_STATUS}" = "STOPPED" ]; then
   DAEMON_STATUS=`pinkBLK ${DAEMON_STATUS}`
fi
if [ "${DAEMON_STATUS}" = "FAILURE" ]; then
  DAEMON_STATUS=`redBLK ${DAEMON_STATUS}`
fi

if [ "${DAEMON_STATUS}" = "TO_START" ]; then
  DAEMON_STATUS=`blue ${DAEMON_STATUS}`
fi
if [ "${DAEMON_STATUS}" = "TO_STOP" ]; then
   DAEMON_STATUS=`pink ${DAEMON_STATUS}`
fi
 
# Ouput
echo "Ping daemon process  : ${DAEMON_PROCESS}"
echo "Ping daemon status   : ${DAEMON_STATUS}"
echo "Ping daemon interval : ${DAEMON_INTERVAL} minutes"
echo "Ping daemon sleep    : ${DAEMON_SLEEP} seconds"
