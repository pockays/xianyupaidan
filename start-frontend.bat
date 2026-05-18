@echo off
chcp 65001 >nul
title XianyuPaidan Frontend

echo ========================================
echo  XianyuPaidan Frontend (Vue 3 + Vite)
echo ========================================

cd /d "%~dp0frontend"

:: Check node_modules
if not exist "node_modules\" (
    echo Installing frontend dependencies...
    call npm install
    if errorlevel 1 (
        echo [ERROR] npm install failed
        pause
        exit /b
    )
    echo [OK] Dependencies installed
) else (
    echo [OK] node_modules exists
)

echo.
echo Starting Vite dev server on port 3000...
echo.
npm run dev
pause
