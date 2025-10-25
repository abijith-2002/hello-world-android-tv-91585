@ECHO OFF
SET DIR=%~dp0
SET GRADLEW=%DIR%android_tv_frontend\gradlew.bat
IF EXIST "%GRADLEW%" (
  CALL "%GRADLEW%" %*
) ELSE (
  ECHO Gradle wrapper not found in android_tv_frontend. Attempting to run gradle if installed...
  gradle %*
)
