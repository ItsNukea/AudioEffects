@echo off
setlocal

:: ===== CONFIG =====
set "APP_DIR=%LOCALAPPDATA%\Audio Effects"
set "JAR_NAME=audio-effects.jar"
set "JAR_PATH=%APP_DIR%\%JAR_NAME%"

:: Direct raw download URL from GitHub
set "DOWNLOAD_URL=https://github.com/ItsNukea/Audio-Effects/releases/latest/download/audio-effects.jar"

:: ===== CREATE DIRECTORY =====
if not exist "%APP_DIR%" (
    mkdir "%APP_DIR%"
)

:: ===== DOWNLOAD IF MISSING =====
if not exist "%JAR_PATH%" (
    echo Jar not found, downloading...

    powershell -Command ^
        "iwr -Uri '%DOWNLOAD_URL%' -OutFile '%JAR_PATH%'"

    if not exist "%JAR_PATH%" (
        echo Download failed.
        pause
        exit /b 1
    )
)

:: ===== RUN =====
:: Make this javaw when the app is ready
java -jar "%JAR_PATH%"

endlocal