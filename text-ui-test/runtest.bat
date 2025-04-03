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
java -jar "%jarloc%" < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL_RAW.TXT
if errorlevel 1 (
    echo Error running the application
    exit /b 1
)

cd ..\..\text-ui-test

:: 使用单行PowerShell命令处理
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$a=[System.IO.File]::ReadAllText('ACTUAL_RAW.TXT');" ^
    "$a=$a -replace '\x1B\[\d+(;\d+)*[mGK]','' -replace '[ \t]+\r?$','' -replace '\r\n','`n';" ^
    "[System.IO.File]::WriteAllText('ACTUAL.TXT',$a);" ^
    "$e=[System.IO.File]::ReadAllText('EXPECTED.TXT');" ^
    "$e=$e -replace '[ \t]+\r?$','' -replace '\r\n','`n';" ^
    "[System.IO.File]::WriteAllText('EXPECTED_CLEAN.TXT',$e)"

:: 比较处理后的文件
fc ACTUAL.TXT EXPECTED_CLEAN.TXT >NUL
if errorlevel 1 (
    echo Test failed!
    echo.
    echo ===== EXPECTED =====
    type EXPECTED.TXT
    echo.
    echo ===== ACTUAL =====
    type ACTUAL_RAW.TXT
    echo.
    exit /b 1
) else (
    echo Test passed!
    exit /b 0
)