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
12:17:20,996  INFO main HotRedeployMain:restartPUInstance:91 - restarting instance 1 on localhost GSC PID:31791 mode:backup...
12:17:25,523  INFO main HotRedeployMain:restartPUInstance:98 - done
12:17:25,523  INFO main HotRedeployMain:restartPUInstance:91 - restarting instance 2 on localhost GSC PID:31794 mode:backup...
12:17:29,784  INFO main HotRedeployMain:restartPUInstance:98 - done
12:17:29,784  INFO main HotRedeployMain:restartPUInstance:91 - restarting instance 2 on localhost GSC PID:31791 mode:primary...
12:17:39,913  INFO main HotRedeployMain:restartPUInstance:98 - done
12:17:39,914  INFO main HotRedeployMain:restartPUInstance:91 - restarting instance 1 on localhost GSC PID:31794 mode:primary...
12:17:49,077  INFO main HotRedeployMain:restartPUInstance:98 - done
12:17:49,117  INFO main HotRedeployMain:main:84 - Hot redeploy is SUCCESS
```

If there are any problems during the hot-redeploy, you will see an error message and description of the problem:
```sh
17:25:07,132  INFO main HotRedeployMain:checkFiles:109 - Please place new files on all GSM machines and try again.
17:25:07,136  INFO main HotRedeployMain:checkFiles:110 - Hot redeploy is FAILURE
```

All details about hot-redeploy process you can see in `hot-redeploy.log` file.

[gigaspaces wiki]:http://wiki.gigaspaces.com/wiki/display/XAP96/Deploying+onto+the+Service+Grid#DeployingontotheServiceGrid-HotDeploy
[SSH login without password]:http://www.linuxproblem.org/art_9.html
