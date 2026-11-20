@echo off
chcp 65001 >nul
REM Compila e abre o menu do Gerenciador de Rotina.
if not exist out mkdir out
javac --release 17 -encoding UTF-8 -cp "lib\*" -d out src\*.java || exit /b 1
java -Dfile.encoding=UTF-8 -cp out Main
