@echo off
chcp 65001 >nul
if not exist out mkdir out
javac --release 17 -encoding UTF-8 -d out src\main\java\rotina\*.java || exit /b 1
java -Dfile.encoding=UTF-8 -cp out rotina.Main
