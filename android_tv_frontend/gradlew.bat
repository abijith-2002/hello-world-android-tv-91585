@ECHO OFF
SET DIR=%~dp0
SET WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
IF EXIST "%WRAPPER_JAR%" (
  java -cp "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
) ELSE (
  ECHO gradle-wrapper.jar not found. Please ensure Gradle wrapper is set up.
  gradle %*
)
