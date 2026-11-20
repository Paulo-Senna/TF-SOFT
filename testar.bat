@echo off
chcp 65001 >nul
REM Compila o projeto e roda os testes unitarios (JUnit 4).
REM Uso: testar.bat              (roda todas as classes de teste)
REM      testar.bat TarefaTest   (roda so uma classe)
if exist out rmdir /s /q out
mkdir out
javac --release 17 -encoding UTF-8 -cp "lib\*" -d out src\*.java || exit /b 1
if "%~1"=="" (
    set TESTES=TarefaTest EstadoTarefaTest RecorrenciaTest GerenciadorTarefasTest PessoaTest SuperUsuarioTest BancoDadosTest TerminalTest
) else (
    set TESTES=%~1
)
java -Dfile.encoding=UTF-8 -cp "out;lib\*" org.junit.runner.JUnitCore %TESTES%
