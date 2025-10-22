@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION
REM Proxy to nested Gradle wrapper in android_tv_frontend
SET ROOT=%~dp0
PUSHD "%ROOT%\android_tv_frontend"
CALL gradlew.bat %*
SET EXITCODE=%ERRORLEVEL%
POPD
EXIT /B %EXITCODE%
