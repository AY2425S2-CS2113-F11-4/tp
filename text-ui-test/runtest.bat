@echo off
setlocal enableextensions
pushd %~dp0

:: ...（前面部分保持不变）...

:jarfound
java -jar "%jarloc%" < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL_RAW.TXT
if errorlevel 1 (
    echo Error running the application
    exit /b 1
)

cd ..\..\text-ui-test

:: 标准化处理（移除颜色代码、尾部空格、统一换行符）
powershell -Command @"
(Get-Content ACTUAL_RAW.TXT -Raw) -replace '\x1B\[[0-9;]*[mGK]', '' `
    -replace '[ \t]+$', '' -replace '\r\n', '`n' | Set-Content ACTUAL.TXT
(Get-Content EXPECTED.TXT -Raw) -replace '[ \t]+$', '' -replace '\r\n', '`n' | Set-Content EXPECTED_CLEAN.TXT
"@

:: 比较时忽略空白差异
fc ACTUAL.TXT EXPECTED_CLEAN.TXT >NUL
if errorlevel 1 (
    echo Test failed!
    echo.
    echo ===== EXPECTED =====
    type EXPECTED_CLEAN.TXT
    echo.
    echo ===== ACTUAL =====
    type ACTUAL.TXT
    echo.
    exit /b 1
) else (
    echo Test passed!
    exit /b 0
)