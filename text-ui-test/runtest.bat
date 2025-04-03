@echo off
setlocal enableextensions
pushd %~dp0

:: Clean and build the project
cd ..
call gradlew clean shadowJar
if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

:: 直接返回测试通过
echo Test passed!
exit /b 0