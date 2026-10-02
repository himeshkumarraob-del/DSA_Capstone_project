@echo off
echo ==============================================
echo  MetroPath Backend - Starting Spring Boot...
echo ==============================================
set MAVEN_HOME=%LOCALAPPDATA%\apache-maven-3.9.9
set PATH=%MAVEN_HOME%\bin;%PATH%
cd /d "%~dp0backend"
mvn spring-boot:run
pause
