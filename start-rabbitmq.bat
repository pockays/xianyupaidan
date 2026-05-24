@echo off
chcp 65001 >nul
title RabbitMQ

set "RABBITMQ_HOME=E:\rabbitmq\rabbitmq_server-4.3.0"

echo ========================================
echo  RabbitMQ
echo ========================================

if not exist "%RABBITMQ_HOME%\sbin\rabbitmq-server.bat" (
    echo [ERROR] RabbitMQ not found at %RABBITMQ_HOME%
    pause
    exit /b
)

echo Checking if already running...
"%RABBITMQ_HOME%\sbin\rabbitmqctl.bat" status 2>nul | findstr "uptime" >nul
if not errorlevel 1 (
    echo [OK] RabbitMQ is already running
    pause
    exit /b
)

echo Starting RabbitMQ...
start "" "%RABBITMQ_HOME%\sbin\rabbitmq-server.bat"

echo Waiting for RabbitMQ to be ready...
:loop
timeout /t 5 /nobreak >nul
"%RABBITMQ_HOME%\sbin\rabbitmqctl.bat" status 2>nul | findstr "uptime" >nul
if errorlevel 1 goto loop

echo [OK] RabbitMQ is running
echo   Management UI: http://localhost:15672
echo   AMQP port:     5672
pause
