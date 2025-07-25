<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:endReadAdditionalConfig

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that prohibit checking in binary data.
if exist %WRAPPER_JAR% (
    if "%MVNW_VERBOSE%" == "true" (
        echo Found %WRAPPER_JAR%
    )
) else (
    if not "%MVNW_REPOURL%" == "" (
        SET DOWNLOAD_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    )
    if "%MVNW_VERBOSE%" == "true" (
        echo Couldn't find %WRAPPER_JAR%, downloading it ...
        echo Downloading from: %DOWNLOAD_URL%
    )

    powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
		"}"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"^
		"}"
    if "%MVNW_VERBOSE%" == "true" (
        echo Finished downloading %WRAPPER_JAR%
    )
)
@REM End of extension

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*

%MAVEN_JAVA_EXE% ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"
:skipRcPost

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%"=="on" pause

if "%MAVEN_TERMINATE_CMD%"=="on" exit %ERROR_CODE%

cmd /C exit /B %ERROR_CODE%

@REM @REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM @REM
@REM @REM Optional ENV vars
@REM @REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM @REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM @REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM @REM ----------------------------------------------------------------------------

@REM @IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@REM @SET __MVNW_CMD__=
@REM @SET __MVNW_ERROR__=
@REM @SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@REM @SET PSModulePath=
@REM @FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
@REM   IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
@REM )
@REM @SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@REM @SET __MVNW_PSMODULEP_SAVE=
@REM @SET __MVNW_ARG0_NAME__=
@REM @SET MVNW_USERNAME=
@REM @SET MVNW_PASSWORD=
@REM @IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__% %*)
@REM @echo Cannot start maven from wrapper >&2 && exit /b 1
@REM @GOTO :EOF
@REM : end batch / begin powershell #>

@REM $ErrorActionPreference = "Stop"
@REM if ($env:MVNW_VERBOSE -eq "true") {
@REM   $VerbosePreference = "Continue"
@REM }

@REM # calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
@REM $distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
@REM if (!$distributionUrl) {
@REM   Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
@REM }

@REM switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
@REM   "maven-mvnd-*" {
@REM     $USE_MVND = $true
@REM     $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
@REM     $MVN_CMD = "mvnd.cmd"
@REM     break
@REM   }
@REM   default {
@REM     $USE_MVND = $false
@REM     $MVN_CMD = $script -replace '^mvnw','mvn'
@REM     break
@REM   }
@REM }

@REM # apply MVNW_REPOURL and calculate MAVEN_HOME
@REM # maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
@REM if ($env:MVNW_REPOURL) {
@REM   $MVNW_REPO_PATTERN = if ($USE_MVND) { "/org/apache/maven/" } else { "/maven/mvnd/" }
@REM   $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace '^.*'+$MVNW_REPO_PATTERN,'')"
@REM }
@REM $distributionUrlName = $distributionUrl -replace '^.*/',''
@REM $distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''
@REM $MAVEN_HOME_PARENT = "$HOME/.m2/wrapper/dists/$distributionUrlNameMain"
@REM if ($env:MAVEN_USER_HOME) {
@REM   $MAVEN_HOME_PARENT = "$env:MAVEN_USER_HOME/wrapper/dists/$distributionUrlNameMain"
@REM }
@REM $MAVEN_HOME_NAME = ([System.Security.Cryptography.MD5]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
@REM $MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

@REM if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
@REM   Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
@REM   Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
@REM   exit $?
@REM }

@REM if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
@REM   Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
@REM }

@REM # prepare tmp dir
@REM $TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
@REM $TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
@REM $TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
@REM trap {
@REM   if ($TMP_DOWNLOAD_DIR.Exists) {
@REM     try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
@REM     catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
@REM   }
@REM }

@REM New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

@REM # Download and Install Apache Maven
@REM Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
@REM Write-Verbose "Downloading from: $distributionUrl"
@REM Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

@REM $webclient = New-Object System.Net.WebClient
@REM if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
@REM   $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
@REM }
@REM [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
@REM $webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

@REM # If specified, validate the SHA-256 sum of the Maven distribution zip file
@REM $distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
@REM if ($distributionSha256Sum) {
@REM   if ($USE_MVND) {
@REM     Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
@REM   }
@REM   Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
@REM   if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
@REM     Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
@REM   }
@REM }

@REM # unzip and move
@REM Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null
@REM Rename-Item -Path "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" -NewName $MAVEN_HOME_NAME | Out-Null
@REM try {
@REM   Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
@REM } catch {
@REM   if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
@REM     Write-Error "fail to move MAVEN_HOME"
@REM   }
@REM } finally {
@REM   try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
@REM   catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
@REM }

@REM Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
