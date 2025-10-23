@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION
SET ROOT_DIR=%~dp0
SET SUB_DIR=%ROOT_DIR%android_tv_frontend
IF NOT EXIST "%SUB_DIR%\gradlew.bat" (
  ECHO Error: Gradle wrapper not found at android_tv_frontend\gradlew.bat 1>&2
  EXIT /B 1
)

REM If -p is not provided, add -p android_tv_frontend
SET "ARGS=%*"
ECHO %ARGS% | FINDSTR /C:" -p " >NUL
IF ERRORLEVEL 1 (
  "%SUB_DIR%\gradlew.bat" -p android_tv_frontend %*
) ELSE (
  "%SUB_DIR%\gradlew.bat" %*
)
