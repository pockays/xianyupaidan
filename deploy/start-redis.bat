@echo off
chcp 65001 >nul
title Redis

set "REDIS_HOME=E:\redis"

echo ========================================
echo  Redis
echo ========================================

if not exist "%REDIS_HOME%\redis-server.exe" (
    echo [ERROR] Redis not found at %REDIS_HOME%
    pause
    exit /b
)

echo Checking if already running...
"%REDIS_HOME%\redis-cli.exe" ping >nul 2>&1
if not errorlevel 1 (
    echo [OK] Redis is already running on port 6379
    pause
    exit /b
)

echo Starting Redis...
start "" "%REDIS_HOME%\redis-server.exe"

timeout /t 2 /nobreak >nul
"%REDIS_HOME%\redis-cli.exe" ping >nul 2>&1
if not errorlevel 1 (
    echo [OK] Redis is running on port 6379
) else (
    echo [WARN] Redis may still be starting, check the Redis window
)
pause
