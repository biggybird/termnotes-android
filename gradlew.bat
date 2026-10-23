@echo off
setlocal
set GRADLE_VERSION=8.13
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set GRADLE_HOME_DIR=%GRADLE_USER_HOME%\termnotes-gradle
set GRADLE_BIN=%GRADLE_HOME_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat
if not exist "%GRADLE_BIN%" (
  if not exist "%GRADLE_HOME_DIR%" mkdir "%GRADLE_HOME_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $zip=Join-Path '%GRADLE_HOME_DIR%' 'gradle-%GRADLE_VERSION%-bin.zip'; if (!(Test-Path $zip)) { Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile $zip }; Expand-Archive -Path $zip -DestinationPath '%GRADLE_HOME_DIR%' -Force"
  if errorlevel 1 exit /b 1
)
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
