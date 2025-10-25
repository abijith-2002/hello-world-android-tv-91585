@ECHO OFF
SET DIR=%~dp0
SET ROOT=%DIR%..
CALL "%ROOT%\gradlew.bat" %*
