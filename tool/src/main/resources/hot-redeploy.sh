#!/bin/sh

source ./properties.sh

if [ "x$GSM_HOSTS" = "x" ]; then
    echo "GSM_HOSTS must be set"
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

for host in "${GSM_HOSTS[@]}"
do
  for K in "${!PU[@]}";
  do
    echo ${PU[$K]}
    scp ${PU[$K]} $SSS_USER@$host:$GIGASPACES_LOCATION/deploy
    ssh $SSS_USER@$host rm -rf $K
    ssh $SSS_USER@$host unzip $GIGASPACES_LOCATION/deploy/${PU[$K]} -d $GIGASPACES_LOCATION/deploy/$K
  done
done
args="-put $IDENT_PU_TIMEOUT -smt $IDENT_SPACE_MODE_TIMEOUT -dr $DOUBLE_RESTART -rt $RESTART_TIMEOUT" -gsloc $GIGASPACES_LOCATION

for K in "${!PU[@]}"; do args="$args -pun $K"; done

if [ "x$GIGASPACES_LOCATORS" != "x" ]; then
    args="$args -gsl $GIGASPACES_LOCATORS"
fi

if [ "x$LOOKUP_GROUP" != "x" ]; then
    args="$args -gsg $LOOKUP_GROUP"
fi

if [ "x$IS_SECURED" != "x" ]; then
   args="$args -s $IS_SECURED"
fi

for K in "${!MYMAP[@]}"; do echo $K; done
echo $args

java -classpath XAP-hot-redeploy.jar org.openspaces.admin.application.hotredeploy.HotRedeployMain $args


