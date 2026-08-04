@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script for Windows
@REM ----------------------------------------------------------------------------
@ECHO OFF

SET BASE_DIR=%~dp0
SET WRAPPER_JAR="%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
SET WRAPPER_PROPERTIES="%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties"
SET MAIN_CLASS=org.apache.maven.wrapper.MavenWrapperMain

IF NOT "%JAVA_HOME%"=="" (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXE=java.exe
)

"%JAVA_EXE%" -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" %MAIN_CLASS% %*
