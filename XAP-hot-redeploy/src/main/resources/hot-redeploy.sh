#!/bin/sh

. ./properties.sh

if ["x$GSM_HOSTS" = "x" ]; then
    echo "GSM_HOSTS must be set"
    exit
fi

if ["x$PU_FILE_NAME" = "x" ]; then
    echo "PU_FILE_NAME must be set"
    exit
fi

if ["x$USER" = "x" ]; then
    echo "USER must be set"
    exit
fi

if ["x$GIGASPACES_LOCATION" = "x" ]; then
    echo "GIGASPACES_LOCATION must be set"
    exit
fi

if ["x$PU_NAME" = "x" ]; then
    echo "PU_NAME must be set"
    exit
fi

if ["x$GIGASPACES_LOCATORS" = "x" ]; then
    echo "GIGASPACES_LOCATORS must be set"
    exit
fi

for host in "${GSM_HOSTS[@]}"
do
    scp $PU_FILE_NAME $SSS_USER@$host:$GIGASPACES_LOCATION/deploy
    ssh $SSS_USER@$host rm -rf $PU_NAME
    ssh $SSS_USER@$host unzip $GIGASPACES_LOCATION/deploy/$PU_FILE_NAME -d $GIGASPACES_LOCATION/deploy/$PU_NAME
done

java -classpath XAP-hot-redeploy.jar org.openspaces.admin.application.hotredeploy.HotRedeployMain $PU_NAME $GIGASPACES_LOCATORS $LOOKUP_GROUP $IDENT_PU_TIMEOUT $IDENT_SPACE_MODE_TIMEOUT $RESTART_TIMEOUT $IS_SECURED
