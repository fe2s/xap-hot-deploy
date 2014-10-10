#!/bin/bash
#check sudo
if [[ $EUID -ne 0 ]]; then
    echo "This script should not be run using sudo or as the root user"
    exit 1
fi



#------MOVE TO ANOTHER PLACE--------

#ask for properties

# hosts on which GSM are located, e.g. ("10.6.133.203" "10.6.133.210")
read -p "Please enter shosts on which GSM are located, e.g. ("10.6.133.203" "10.6.133.210"): " GSM_HOSTS
echo $GSM_HOSTS

# map with key value pairs, where key is processing unit name, value is name of file with new classes, e.g. declare -A PU=( [space]=space.jar )
PUS="("
while :
do
  read -p "Please enter PU name: " PU_NAME
  read -p "Please enter path to PU(jar, war) you want to redeploy: " PU_FILE
  PUS=$PUS"[$PU_NAME]=$PU_FILE"
  read -p "Do you want to redeploy more units (y/n) : " RESP
  if [ "$RESP" = "n" ]; then
    PUS=$PUS")"
    break
  elif [ "$RESP" = "y" ]; then
    PUS=$PUS","
  else
    echo "OK"
  fi
done

# path to gigaspace directory, e.g. "/home/user/gigaspaces-xap-premium-9.7.1-ga"
read -p "Please enter path to gigaspace directory, e.g. /home/user/gigaspaces-xap-premium-9.7.1-ga : " GIGASPACES_LOCATION
echo $GIGASPACES_LOCATION

# gigaspace locator to cluster, e.g. "127.0.0.1"
read -p "Please enter locators (format: 10.0.2.15:4174,10.0.2.16:4174) : " LOCATORS
echo $LOCATORS

# lookup group, e.g. "gigaspaces-10.0.0-XAPPremium-ga"
read -p "Please enter lookup groups : " LOOKUP_GROUPS
echo $LOOKUP_GROUPS

#installation dir
read -p "Please enter path to installation directory: " installDir
echo $installDir

#------MOVE TO ANOTHER PLACE--------
mvn clean install

#1. Copy `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` to any host where the script will be run from. 
installDir=$installDir/hot-redeploy
mkdir $installDir
cp tool/target/HotRedeploy.zip $installDir
cd $installDir
#2. Unzip `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` file.
unzip HotRedeploy.zip
rm HotRedeploy.zip
cd HotRedeploy

pwd

echo "GSM_HOSTS=$GSM_HOSTS" > 'properties.sh'
echo "declare -A PU=$PUS" >> 'properties.sh'
echo 'SSS_USER="user"' >> 'properties.sh'
echo "GIGASPACES_LOCATION=$GIGASPACES_LOCATION" >> 'properties.sh'
echo "GIGASPACES_LOCATORS=$LOCATORS" >> 'properties.sh'
echo "LOOKUP_GROUP=$LOOKUP_GROUPS" >> 'properties.sh'
echo 'IDENT_PU_TIMEOUT="60"' >> 'properties.sh'
echo 'IDENT_SPACE_MODE_TIMEOUT="60"' >> 'properties.sh'
echo 'IS_SECURED="false"' >> 'properties.sh'
echo 'DOUBLE_RESTART="false"' >> 'properties.sh'


chmod +x XAP-hot-redeploy.sh
./XAP-hot-redeploy.sh

#4. Copy jar file with new classes to the folder with .sh file.

#5. Configure options in `properties.sh` file.

#6. Run `XAP-hot-redeploy.sh` script.

#1. Copy `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` to any host where the script will be run from. 
#2. Unzip `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` file.
#3. Configure password-less ssh connection between client machine and GSM hosts (see [SSH login without password] page for instruction).
#4. Copy jar file with new classes to the folder with .sh file.
#5. Configure options in `properties.sh` file.
#6. Run `XAP-hot-redeploy.sh` script.
