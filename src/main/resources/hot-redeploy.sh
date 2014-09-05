#!/bin/sh

source ./properties.sh

if [ "x$GSM_HOSTS" = "x" ]; then
    echo "GSM_HOSTS must be set"
    exit
fi

if [ "x$PU_FILE_NAME" = "x" ]; then
    echo "PU_FILE_NAME must be set"
    exit
fi

if [ "x$USER" = "x" ]; then
    echo "USER must be set"
    exit
fi

if [ "x$GIGASPACES_LOCATION" = "x" ]; then
    echo "GIGASPACES_LOCATION must be set"
    exit
fi

if [ "x$PU_NAME" = "x" ]; then
    echo "PU_NAME must be set"
    exit
fi

for host in "${GSM_HOSTS[@]}"
do
    scp $PU_FILE_NAME $SSS_USER@$host:$GIGASPACES_LOCATION/deploy
    ssh $SSS_USER@$host rm -rf $PU_NAME
    ssh $SSS_USER@$host unzip $GIGASPACES_LOCATION/deploy/$PU_FILE_NAME -d $GIGASPACES_LOCATION/deploy/$PU_NAME
done

args="-pun $PU_NAME -put $IDENT_PU_TIMEOUT -smt $IDENT_SPACE_MODE_TIMEOUT -dr $DOUBLE_RESTART"

if [ "x$GIGASPACES_LOCATORS" != "x" ]; then
    args="$args -gsl $GIGASPACES_LOCATORS"
fi

if [ "x$LOOKUP_GROUP" != "x" ]; then
    args="$args -gsg $LOOKUP_GROUP"
fi

if [ "x$IS_SECURED" != "x" ]; then
   args="$args -s $IS_SECURED"
fi

echo $args

java -classpath XAP-hot-redeploy.jar org.openspaces.admin.application.hotredeploy.HotRedeployMain $args 
