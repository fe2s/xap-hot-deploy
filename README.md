XAP hot deploy
===

About
---
This tool allows to refresh business logic without any system downtime and data loss (hot deploy).
See [gigaspaces wiki] for details.

Tool will copy new files in deploy folder. After that application will define all processing unit instances and identify their space mode.
When all instances are defined, all backups will restart and then all primaries will restart. 

Build
---
For build use:

    mvn clean install 
It will create `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` file.

Run
---

1. Copy `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` to any host where the script will be run from. 
2. Unzip `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` file.
3. Configure password-less ssh connection between client machine and GSM hosts (see [SSH login without password] page for instruction).
4. Copy jar file with new classes to the folder with .sh file.
5. Configure environment properties in `properties.sh` file.
6. Run `XAP-hot-redeploy.sh` script.

Results
---
In case if there are no problems with hot-redeploy application you can see success message and details for restarting pu instances: 
```sh
20:05:37,762  INFO main HotRedeployMain:restartPUInstance:55 - restarting instance 2 on localhost GSC PID:31794 mode:backup...
20:05:41,534  INFO main HotRedeployMain:restartPUInstance:62 - done
20:05:41,534  INFO main HotRedeployMain:restartPUInstance:55 - restarting instance 1 on localhost GSC PID:31791 mode:backup...
20:05:45,545  INFO main HotRedeployMain:restartPUInstance:62 - done
20:05:45,546  INFO main HotRedeployMain:restartPUInstance:55 - restarting instance 1 on localhost GSC PID:31794 mode:primary...
20:05:55,314  INFO main HotRedeployMain:restartPUInstance:62 - done
20:05:55,314  INFO main HotRedeployMain:restartPUInstance:55 - restarting instance 2 on localhost GSC PID:31791 mode:primary...
20:06:05,015  INFO main HotRedeployMain:restartPUInstance:62 - done
20:06:05,033  INFO main HotRedeployMain:main:42 - Hot redeploy completed successfully
```

If there are any problems during the hot-redeploy, you will see an error message and description of the problem:
```sh
20:11:27,861  INFO main HotRedeployMain:checkFiles:76 - Please place new files on all GSM machines and try again.
20:11:27,864  INFO main HotRedeployMain:checkFiles:77 - Hot redeploy failed
```

All details about hot-redeploy process you can see in `hot-redeploy.log` file.

[gigaspaces wiki]:http://wiki.gigaspaces.com/wiki/display/XAP96/Deploying+onto+the+Service+Grid#DeployingontotheServiceGrid-HotDeploy
[SSH login without password]:http://www.linuxproblem.org/art_9.html
