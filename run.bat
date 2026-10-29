@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
dir /b /s src\main\java\com\cine\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
set BUILD_RESULT=%ERRORLEVEL%
del sources.txt
if not "%BUILD_RESULT%"=="0" goto error
java -cp out com.cine.CineServer %1
goto end
:error
echo.
echo Error al compilar el proyecto.
pause
:end
endlocal
