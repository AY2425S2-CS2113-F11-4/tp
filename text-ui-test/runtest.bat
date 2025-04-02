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
:: Run the test and capture output
java -jar "%jarloc%" < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT
if errorlevel 1 (
    echo Error running the application
    exit /b 1
)

:: Compare results with detailed output
cd ..\..\text-ui-test
echo Comparing results...
fc ACTUAL.TXT EXPECTED.TXT >NUL
if errorlevel 1 (
    echo Test failed!
    echo.
    echo ===== EXPECTED =====
    type EXPECTED.TXT
    echo.
    echo ===== ACTUAL =====
    type ACTUAL.TXT
    echo.
    exit /b 1
) else (
    echo Test passed!
    exit /b 0
)