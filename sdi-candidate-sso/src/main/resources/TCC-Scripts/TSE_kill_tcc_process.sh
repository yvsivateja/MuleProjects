#! /bin/ksh

if [[ -z "$1" ]]
then
   echo "Missing TCC_ID Argument.  Nothing killed!"
   exit 0
fi

TCC_ID=$1
echo Trying to locate TCC_ID Process: ${TCC_ID}
PROC_ID=`ps -ef | grep epirun | grep Dtcc.id=${TCC_ID} | grep -v grep | awk '{print $2}'`
PARENT_ID=`ps -ef | grep epirun | grep Dtcc.id=${TCC_ID} | grep -v grep | awk '{print $3}'`

if [[ $PROC_ID != "" ]] ; then
        echo Killing TCC Child Process ID: $PROC_ID
        kill -15 $PROC_ID

        echo Killing TCC Parent Process ID: $PARENT_ID
        kill -15 $PARENT_ID
else
        echo Unable to find a TCC Process to kill.
fi

# Verify they are gone.
PROC_ID=`ps -ef | grep epirun | grep Dtcc.id=${TCC_ID} | grep -v grep | awk '{print $2}'`
if [[ $PROC_ID = "" ]] ; then
   echo Verified TCC Process does not exist.
else
        echo TCC Process was not killed.
fi
