# hosts on which GSM are located, e.g. ("10.6.133.203" "10.6.133.210")
GSM_HOSTS=("10.6.132.93")

# map with key value pairs, where key is processing unit name, value is name of file with new classes
declare -A PU=( [space]=space.jar [cinema]=cinema.war )

# name of user on remote machine, e.g. "user"
SSS_USER="user"

# path to gigaspace directory, e.g. "/home/user/gigaspaces-xap-premium-9.7.1-ga"
GIGASPACES_LOCATION="/home/user/gigaspaces-xap-premium-10.0.0-ga/"

# gigaspace locator to cluster, e.g. "127.0.0.1"
GIGASPACES_LOCATORS="10.6.132.93"

# lookup group, e.g. "gigaspaces-10.0.0-XAPPremium-ga"
LOOKUP_GROUP="gigaspaces-10.0.0-XAPPremium-ga"

# timeout for identify PU (in seconds), e.g. "10"
IDENT_PU_TIMEOUT="100"

# timeout for identify space mode (in seconds), e.g. "10"
IDENT_SPACE_MODE_TIMEOUT="100"

# set "true" if space secured, set "false" if space not secured
IS_SECURED="false"

# set "true" if all instances should placed in "original" vm after redeploy
DOUBLE_RESTART="true"
