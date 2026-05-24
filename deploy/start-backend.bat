@echo off
chcp 65001 >nul
title XianyuPaidan Backend

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
set "MAVEN_HOME=C:\Users\liuris\tools\apache-maven-3.9.11"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo ========================================
echo  XianyuPaidan Backend (Spring Boot)
echo ========================================

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java not found at %JAVA_HOME%
    pause
    exit /b
)
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [ERROR] Maven not found at %MAVEN_HOME%
    pause
    exit /b
)

echo Java: %JAVA_HOME%
echo Maven: %MAVEN_HOME%
echo.

:: Check MySQL
echo Checking dependencies...
"C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot\bin\java" -version 2>&1 | findstr "OpenJDK" >nul || (
    echo [WARN] Java version check failed
)

echo Starting Spring Boot on port 8080...
echo First run may take 1-3 minutes to download dependencies.
echo.
cd /d "%~dp0backend"
call mvn spring-boot:run
pause
