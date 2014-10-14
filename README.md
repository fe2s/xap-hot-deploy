XAP hot deploy
===

About
---
This tool allows to refresh business logic without any system downtime and data loss (hot deploy).
See [gigaspaces wiki] for details.

Tool will restart all processing units defined by user.

New files will be copied to the deploy folder. After that application will define all processing units and restart them.

Stateful PU restart.
---
1. Tool defines all processing unit instances and identifies their space mode.
2. All backups restarted
3. All primaries restarted. If 'double_restart' option enabled, primaries restarted twice to return to the original state.

Build
---
For build use:

    mvn clean install 
    
It will create `XAP-hot-redeploy-0.0.1-SNAPSHOT.zip` file.
Note, that tests will be skipped in this case. How to build with tests see in Tests section.


Run
---

1. Copy `tool/target/HotRedeploy.zip` to any host where the script will be run from.
2. Unzip `tool/target/HotRedeploy.zip` file.
3. Configure password-less ssh connection between client machine and GSM hosts (see [SSH login without password] page for instruction).
4. Copy jar file with new classes to the folder with .sh file.
5. Configure options in `properties.sh` file.
6. Run `XAP-hot-redeploy.sh` script.

Parameters in `properties.sh` file.
---

| Option                   | Optional/required | Default value                        | Description                                                                                                                         |
|--------------------------|-------------------|--------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| GSM_HOSTS                | required          | -                                    | Hosts on which GSM are located.                                                                                                     |
| PU                       | required          | ( [space]=space.jar [web]=web.war ) | Map with key value pairs, where key is processing unit name, value is name of file with new classes.                                |
| SSS_USER                 | required          | "user"                               | Name of user on remote machine.                                                                                                     |
| GIGASPACES_LOCATION      | required          | -                                    | Path to gigaspaces directory.                                                                                                       |
| GIGASPACES_LOCATORS      | optional          | localhost                            | Gigaspaces locator.                                                                                                                 |
| LOOKUP_GROUP             | optional          | Gigaspace default lookup group       | Lookup group                                                                                                                        |
| IDENT_PU_TIMEOUT         | required          | "60"                                 | Timeout to identify processing unit (in seconds).                                                                                   |
| IDENT_SPACE_MODE_TIMEOUT | required          | "60"                                 | Timeout to identify space mode (in seconds).                                                                                        |
| IS_SECURED               | optional          | "false"                              | Set this parameter "true" if space is secured.                                                                                      |
| DOUBLE_RESTART           | optional          | "false"                              | Set "true" if all instances should be placed in "original" vm after redeploy. When set to "true" primary instances restarted twice. |

Results
---
In case if there are no problems with hot-redeploy application you can see success message and details for restarting pu instances: 
```sh
12:06:08,089  INFO main ConfigInitializer:init:25 - Pu to restart: [space, cinema, mirror]
12:06:08,092  INFO main ConfigInitializer:init:26 - Locator: null
12:06:08,095  INFO main ConfigInitializer:init:27 - Lookup group: null
12:06:08,095  INFO main ConfigInitializer:init:28 - Timeout for identify pu: 100
12:06:08,095  INFO main ConfigInitializer:init:29 - Timeout for identify space mode: 100
12:06:08,095  INFO main ConfigInitializer:init:30 - Secured: false
12:06:08,095  INFO main ConfigInitializer:init:31 - Double restart: true
12:06:13,708  INFO main StatefulPuRestarter:restartAllInstances:90 - Restarting pu space with type STATEFUL
12:06:13,710  INFO pool-6-thread-1 PuInstanceRestarter:restartPUInstance:32 - restarting instance 1 on 127.0.0.1[127.0.0.1] GSC PID:7260 mode:backup...
12:06:13,710  INFO pool-6-thread-2 PuInstanceRestarter:restartPUInstance:32 - restarting instance 2 on 127.0.0.1[127.0.0.1] GSC PID:11464 mode:backup...
12:06:26,546  INFO pool-6-thread-1 PuInstanceRestarter:restartPUInstance:39 - done
12:06:28,906  INFO pool-6-thread-2 PuInstanceRestarter:restartPUInstance:39 - done
12:06:28,970  INFO pool-7-thread-1 PuInstanceRestarter:restartPUInstance:32 - restarting instance 2 on 127.0.0.1[127.0.0.1] GSC PID:7260 mode:primary...
12:06:40,881  INFO pool-7-thread-1 PuInstanceRestarter:restartPUInstance:39 - done
12:06:40,881  INFO pool-7-thread-1 PuInstanceRestarter:restartPUInstance:32 - restarting instance 1 on 127.0.0.1[127.0.0.1] GSC PID:11464 mode:primary...
12:06:51,631  INFO pool-7-thread-1 PuInstanceRestarter:restartPUInstance:39 - done
12:06:52,644  INFO pool-8-thread-1 PuInstanceRestarter:restartPUInstance:32 - restarting instance 1 on 127.0.0.1[127.0.0.1] GSC PID:7260 mode:primary...
12:07:05,719  INFO pool-8-thread-1 PuInstanceRestarter:restartPUInstance:39 - done
12:07:05,719  INFO pool-8-thread-1 PuInstanceRestarter:restartPUInstance:32 - restarting instance 2 on 127.0.0.1[127.0.0.1] GSC PID:11464 mode:primary...
12:07:16,390  INFO pool-8-thread-1 PuInstanceRestarter:restartPUInstance:39 - done
12:07:17,433  INFO main StatelessPuRestarter:restart:23 - Restarting pu cinema with type WEB
12:07:26,107  INFO main StatelessPuRestarter:restart:25 - done
12:07:27,116  INFO main StatelessPuRestarter:restart:23 - Restarting pu mirror with type MIRROR
12:07:33,929  INFO main StatelessPuRestarter:restart:25 - done
12:07:33,945  INFO main HotRedeployMain:main:17 - Hot redeploy completed successfully
```

If there are any problems during the hot-redeploy, you will see an error message and description of the problem:
```sh
20:11:27,861  INFO main HotRedeployMain:checkFiles:76 - Please place new files on all GSM machines and try again.
20:11:27,864  INFO main HotRedeployMain:checkFiles:77 - Hot redeploy failed
```

All details about hot-redeploy process you can see in `hot-redeploy.log` file.

Tests
---

If you want to build tool with running tests use 
```
mvn clean install -DskipTests=false
```
>PREREQUISITES for running tests:

 * run gs-agent.sh/bat
 * lookup group and locator should be set to default values
 * properties should be set in `/tool/src/test/resources/config.properties` file
 * make sure that there is no pu with name "space" deployed already

[gigaspaces wiki]:http://wiki.gigaspaces.com/wiki/display/XAP96/Deploying+onto+the+Service+Grid#DeployingontotheServiceGrid-HotDeploy
[SSH login without password]:http://www.linuxproblem.org/art_9.html
