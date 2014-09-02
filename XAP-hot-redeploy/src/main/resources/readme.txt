= ABOUT =
This script allows to refresh business logic without any system downtime and data loss (hot deploy).
See http://wiki.gigaspaces.com/wiki/display/XAP96/Deploying+onto+the+Service+Grid#DeployingontotheServiceGrid-HotDeploy for details

= BUILD =
1. mvn clean install (it will create "product-hot-redeploy-0.0.1-SNAPSHOT.zip")

= RUN =
1. Copy product-hot-redeploy-0.0.1-SNAPSHOT.zip to any host where the script will be run from.
2. Unzip "product-hot-redeploy-0.0.1-SNAPSHOT.zip" file
3. Configure password-less ssh connection between client machine and GSM hosts (see http://www.linuxproblem.org/art_9.html for instruction)
4. Copy jar file with new classes to the folder with .sh file
5. Configure environment properties in "properties.sh"
6. Run "product-hot-redeploy.sh" script