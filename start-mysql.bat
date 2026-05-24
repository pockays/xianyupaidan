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

:: Check if data directory is initialized
if not exist "%MYSQL_HOME%\data\mysql\" (
    echo [WARN] MySQL data not initialized. Initializing now...
    echo.
    echo Step 1/3: Initializing system tablespace...
    "%MYSQL_HOME%\bin\mysqld" --initialize-insecure --basedir="%MYSQL_HOME%" --datadir="%MYSQL_HOME%\data" --console
    if errorlevel 1 (
        echo [ERROR] MySQL initialization failed!
        pause
        exit /b
    )

    echo Step 2/3: Starting temporary instance...
    start "MySQL-tmp-init" /MIN "%MYSQL_HOME%\bin\mysqld" --basedir="%MYSQL_HOME%" --datadir="%MYSQL_HOME%\data" --port=3306 --console

    :: Wait for MySQL to be ready
    :wait_init
    timeout /t 2 /nobreak >nul
    "%MYSQL_HOME%\bin\mysqladmin" -u root --skip-password ping 2>nul | findstr "alive" >nul
    if errorlevel 1 goto wait_init

    echo Step 3/3: Setting root password to "root"...
    "%MYSQL_HOME%\bin\mysql" -u root --skip-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root'; FLUSH PRIVILEGES;" 2>nul

    :: Stop temp instance
    "%MYSQL_HOME%\bin\mysqladmin" -u root -proot shutdown 2>nul
    timeout /t 2 /nobreak >nul
    echo [OK] MySQL initialized successfully.
    echo.
)

echo Starting MySQL with binlog...
start "MySQL-server" /MIN "%MYSQL_HOME%\bin\mysqld" --basedir="%MYSQL_HOME%" --datadir="%MYSQL_HOME%\data" --port=3306 --server-id=1 --log-bin=mysql-bin --binlog-format=ROW --binlog-row-image=FULL --binlog-expire-logs-seconds=604800 --console

echo Waiting for MySQL to be ready...
:loop
timeout /t 3 /nobreak >nul
"%MYSQL_HOME%\bin\mysqladmin" -u root -proot ping 2>nul | findstr "alive" >nul
if errorlevel 1 goto loop

echo [OK] MySQL is running on port 3306
pause
