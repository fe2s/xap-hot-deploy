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
15:29:43,534  INFO pool-6-thread-1 RestartInstance:restartPUInstance:36 - restarting instance 1 on localhost GSC PID:29899 mode:backup...
15:29:43,534  INFO pool-6-thread-2 RestartInstance:restartPUInstance:36 - restarting instance 2 on localhost GSC PID:29904 mode:backup...
15:29:48,639  INFO pool-6-thread-1 RestartInstance:restartPUInstance:43 - done
15:29:49,586  INFO pool-6-thread-2 RestartInstance:restartPUInstance:43 - done
15:29:50,047  INFO pool-7-thread-1 RestartInstance:restartPUInstance:36 - restarting instance 1 on localhost GSC PID:29904 mode:primary...
15:29:50,048  INFO pool-7-thread-2 RestartInstance:restartPUInstance:36 - restarting instance 2 on localhost GSC PID:29899 mode:primary...
15:30:00,689  INFO pool-7-thread-1 RestartInstance:restartPUInstance:43 - done
15:30:01,482  INFO pool-7-thread-2 RestartInstance:restartPUInstance:43 - done
15:30:01,569  INFO main HotRedeployMain:main:51 - Hot redeploy completed successfully
```

If there are any problems during the hot-redeploy, you will see an error message and description of the problem:
```sh
20:11:27,861  INFO main HotRedeployMain:checkFiles:76 - Please place new files on all GSM machines and try again.
20:11:27,864  INFO main HotRedeployMain:checkFiles:77 - Hot redeploy failed
```

All details about hot-redeploy process you can see in `hot-redeploy.log` file.

[gigaspaces wiki]:http://wiki.gigaspaces.com/wiki/display/XAP96/Deploying+onto+the+Service+Grid#DeployingontotheServiceGrid-HotDeploy
[SSH login without password]:http://www.linuxproblem.org/art_9.html
