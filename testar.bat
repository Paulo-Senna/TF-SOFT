@echo off
REM Compila o projeto e roda todos os testes (Windows, sem precisar de Maven).
if exist out rmdir /s /q out
mkdir out
set CP=lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar
javac --release 17 -encoding UTF-8 -d out src\main\java\rotina\*.java || exit /b 1
javac --release 17 -encoding UTF-8 -cp "out;%CP%" -d out src\test\java\rotina\*.java || exit /b 1
java -cp "out;%CP%" org.junit.runner.JUnitCore rotina.TarefaTest rotina.EstadoTarefaTest rotina.RecorrenciaTest rotina.FiltroTest rotina.GerenciadorTarefasTest rotina.ArquivoTarefasTest
