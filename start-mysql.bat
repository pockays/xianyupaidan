@echo off
chcp 65001 >nul
title MySQL 8.4

set "MYSQL_HOME=E:\mysql\mysql-8.4.9-winx64"

echo ========================================
echo  MySQL 8.4
echo ========================================

if not exist "%MYSQL_HOME%\bin\mysqld.exe" (
    echo [ERROR] MySQL not found at %MYSQL_HOME%
    pause
    exit /b
)

echo Checking if already running...
"%MYSQL_HOME%\bin\mysqladmin" -u root -proot ping 2>nul | findstr "alive" >nul
if not errorlevel 1 (
    echo [OK] MySQL is already running on port 3306
    pause
    exit /b
)

echo Starting MySQL with binlog...
start "" "%MYSQL_HOME%\bin\mysqld" --basedir="%MYSQL_HOME%" --datadir="%MYSQL_HOME%\data" --port=3306 --server-id=1 --log-bin=mysql-bin --binlog-format=ROW --binlog-row-image=FULL --binlog-expire-logs-seconds=604800

echo Waiting for MySQL to be ready...
:loop
timeout /t 3 /nobreak >nul
"%MYSQL_HOME%\bin\mysqladmin" -u root -proot ping 2>nul | findstr "alive" >nul
if errorlevel 1 goto loop

echo [OK] MySQL is running on port 3306
pause
