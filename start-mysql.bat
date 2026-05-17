@echo off
chcp 65001 >nul
title MySQL 8.4

set "MYSQL_HOME=E:\mysql\mysql-8.4.9-winx64"

echo Starting MySQL...
start "" "%MYSQL_HOME%\bin\mysqld" --defaults-file="%MYSQL_HOME%\my.ini"
echo MySQL started in background.
timeout /t 3 /nobreak >nul

echo Testing connection...
"%MYSQL_HOME%\bin\mysql" -u root -proot -e "SELECT 'MySQL OK' AS status" 2>nul
if errorlevel 1 (
    echo [WARN] MySQL connection failed, may still be initializing
) else (
    echo [OK] MySQL is running
)
pause
