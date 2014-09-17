# hosts on which GSM are located, e.g. ("10.6.133.203" "10.6.133.210")
GSM_HOSTS=

# map with key value pairs, where key is processing unit name, value is name of file with new classes, e.g. declare -A PU=( [space]=space.jar )
declare -A PU=( [space]=space.jar [web]=web.war )

# name of user on remote machine, e.g. "user"
SSS_USER="user"

# path to gigaspace directory, e.g. "/home/user/gigaspaces-xap-premium-9.7.1-ga"
GIGASPACES_LOCATION=

# gigaspace locator to cluster, e.g. "127.0.0.1"
GIGASPACES_LOCATORS=

# lookup group, e.g. "gigaspaces-10.0.0-XAPPremium-ga"
LOOKUP_GROUP=

# timeout for identify PU (in seconds), e.g. "60"
IDENT_PU_TIMEOUT="60"

# timeout for identify space mode (in seconds), e.g. '60'
IDENT_SPACE_MODE_TIMEOUT="60"

# set "true" if space secured, set "false" if space not secured
IS_SECURED="false"

# set "true" if all instances should placed in "original" vm after redeploy
DOUBLE_RESTART="false"