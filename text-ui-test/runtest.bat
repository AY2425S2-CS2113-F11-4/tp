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

:: Find the newest JAR file
cd build\libs
for /f "tokens=*" %%a in ('dir /b /o-d *.jar') do (
    set "jarloc=%%a"
    goto :jarfound
)
echo No JAR file found in build\libs!
exit /b 1

:jarfound
:: Run and capture colored output
java -jar "%jarloc%" < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL_RAW.TXT
if errorlevel 1 (
    echo Error running the application
    exit /b 1
)

:: Remove ANSI colors before comparison
cd ..\..\text-ui-test
powershell -Command "(Get-Content ACTUAL_RAW.TXT) -replace '\x1B\[[0-9;]*[mGK]', '' | Set-Content ACTUAL.TXT"

:: Compare cleaned output
fc ACTUAL.TXT EXPECTED.TXT >NUL
if errorlevel 1 (
    echo Test failed!
    echo.
    echo ===== EXPECTED =====
    type EXPECTED.TXT
    echo.
    echo ===== ACTUAL (no color) =====
    type ACTUAL.TXT
    echo.
    echo ===== RAW ACTUAL OUTPUT =====
    type ACTUAL_RAW.TXT
    exit /b 1
) else (
    echo Test passed!
    echo [NOTE] 彩色输出已保存到 ACTUAL_RAW.TXT
    exit /b 0
)