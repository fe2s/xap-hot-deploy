@ECHO OFF

md tmp

SET /p GSM_HOSTS="Please enter hosts on which GSM are located, e.g. ("10.6.133.203" "10.6.133.210"): " %=%
ECHO %GSM_HOSTS%

SET PUS=(

:loop

SET /p PU_NAME="Please enter PU name: " %=%
ECHO %PU_NAME%

SET /p PU_FILE="Please enter path to PU(jar, war) you want to redeploy: " %=%
ECHO %PU_FILE%

xcopy %PU_FILE% tmp

Set filename=%PU_FILE%
For %%A in ("%filename%") do (
    Set Folder=%%~dpA
    Set Name=%%~nxA
)

SET PU_FILE=%Name%

SET PUS=%PUS%[%PU_NAME%]=%PU_FILE%

SET /p ANSWER="Do you want to redeploy more units (y/n): " %=%
if /i {%ANSWER%}=={y} (
SET PUS=%PUS% 
goto :loop
) 

SET PUS=%PUS%)
ECHO %PUS%

SET /p GIGASPACES_LOCATION="Please enter path to gigaspaces directory. (D:\gigaspaces-xap-premium-10.0.1-ga): " %=%
set GIGASPACES_LOCATION=%GIGASPACES_LOCATION:\=\\%
ECHO %GIGASPACES_LOCATION%

SET /p LOCATORS="Please enter locators, e.g. ("10.6.133.203:4174"): " %=%
ECHO %LOCATORS%

SET /p LOOKUP_GROUPS="Please enter lookup groups: " %=%
ECHO %LOOKUP_GROUPS%

SET /p INSTALL_DIR="Please enter install dir: " %=%
ECHO %INSTALL_DIR%

::mvn clean install

SET INSTALL_DIR=%INSTALL_DIR%\hot-redeploy

ECHO %INSTALL_DIR%

md %INSTALL_DIR%
xcopy tool\target\classes\hot-redeploy.sh %INSTALL_DIR%
xcopy tool\target\HotRedeploy-jar-with-dependencies.jar %INSTALL_DIR%
xcopy tmp\* %INSTALL_DIR%
rd /s /Q tmp
xcopy run.bat %INSTALL_DIR%
xcopy Preparation %INSTALL_DIR%\Preparation /E

cd %INSTALL_DIR%

ren HotRedeploy-jar-with-dependencies.jar  XAP-hot-redeploy.jar
ren hot-redeploy.sh XAP-hot-redeploy.sh

echo GSM_HOSTS=%GSM_HOSTS%>properties.sh
echo PU=%PUS%>>properties.sh
echo SSS_USER="user">>properties.sh
echo GIGASPACES_LOCATION=%GIGASPACES_LOCATION%>>properties.sh
echo GIGASPACES_LOCATORS=%LOCATORS%>>properties.sh
echo LOOKUP_GROUP=%LOOKUP_GROUPS%>>properties.sh
echo IDENT_PU_TIMEOUT="60">>properties.sh
echo IDENT_SPACE_MODE_TIMEOUT="60">>properties.sh
echo IS_SECURED="false">>properties.sh
echo DOUBLE_RESTART="false">>properties.sh

call run.bat

PAUSE
