@echo off
chcp 65001 >nul
title Kafka (KRaft)

set "KAFKA_HOME=E:\kafka\kafka_2.13-3.7.0"

if not exist "%KAFKA_HOME%" (
    echo [ERROR] Kafka not found at %KAFKA_HOME%
    echo Download: https://kafka.apache.org/downloads
    echo Extract to E:\kafka\
    pause
    exit /b
)

cd /d "%KAFKA_HOME%"

:: Format KRaft storage (first-time only)
if not exist "data\kraft-logs" mkdir data\kraft-logs
call bin\windows\kafka-storage.bat format --config config\kraft\server.properties --cluster-id xianyupaidan-kafka-cluster --no-prompt

:: Start Kafka
echo Starting Kafka...
start "Kafka" bin\windows\kafka-server-start.bat config\kraft\server.properties

echo Kafka starting on port 9092...
echo Once started, create topic:
echo   bin\windows\kafka-topics.bat --create --topic xianyupaidan-audit --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
pause
