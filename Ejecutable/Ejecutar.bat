@echo off
chcp 65001 >nul
title Sistema de Gestion de Productos
cd /d "%~dp0"
if not exist "gestion-productos-1.0.jar" (
    echo No se encontro gestion-productos-1.0.jar en esta carpeta.
    pause
    exit /b 1
)
java -jar gestion-productos-1.0.jar
echo.
pause
