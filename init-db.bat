@echo off
chcp 65001 >nul
title Init Database

set "MYSQL_HOME=E:\mysql\mysql-8.4.9-winx64"

echo ========================================
echo  Database Initialization
echo ========================================

echo Checking MySQL connection...
"%MYSQL_HOME%\bin\mysqladmin" -u root -proot ping 2>nul | findstr "alive" >nul
if errorlevel 1 (
    echo [ERROR] MySQL is not running. Start MySQL first.
    pause
    exit /b
)

echo Creating database and tables...
cd /d "%~dp0"
"%MYSQL_HOME%\bin\mysql" -u root -proot < backend\schema.sql 2>nul
if errorlevel 1 (
    echo [WARN] Some statements may have failed (tables may already exist)
) else (
    echo [OK] Database initialized successfully
)

echo.
echo Tables created:
"%MYSQL_HOME%\bin\mysql" -u root -proot -e "USE xianyupaidan; SHOW TABLES;" 2>nul
pause
