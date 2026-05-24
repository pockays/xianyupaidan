@echo off
chcp 65001 >nul
title Canal Server

set "CANAL_HOME=E:\canal"

if not exist "%CANAL_HOME%" (
    echo [ERROR] Canal not found at %CANAL_HOME%
    echo Download: https://github.com/alibaba/canal/releases
    echo Extract to E:\canal\
    pause
    exit /b
)

echo Starting Canal Server...
echo Make sure MySQL is running with binlog enabled.
echo Make sure Kafka is running at localhost:9092.
echo.

cd /d "%CANAL_HOME%"
call bin\startup.bat

echo Canal starting on port 11111...
echo Check logs: %CANAL_HOME%\logs\canal\canal.log
pause
