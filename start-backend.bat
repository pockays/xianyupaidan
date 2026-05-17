@echo off
chcp 65001 >nul
title XianyuPaidan Backend

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
set "MAVEN_HOME=C:\Users\liuris\tools\apache-maven-3.9.11"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo JAVA_HOME = %JAVA_HOME%
echo MAVEN_HOME = %MAVEN_HOME%
echo.

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java not found
    pause
    exit /b
)
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [ERROR] Maven not found
    pause
    exit /b
)

echo [OK] Java and Maven found
echo Starting Spring Boot on port 8080...
echo First run may take 1-3 minutes to download dependencies.
echo.

cd /d "%~dp0backend"
call mvn spring-boot:run
pause
