#!/bin/sh
# Compila o projeto e roda todos os testes (sem precisar de Maven).
set -e
rm -rf out && mkdir -p out
CP="lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"
javac --release 17 -encoding UTF-8 -d out src/main/java/rotina/*.java
javac --release 17 -encoding UTF-8 -cp "out:$CP" -d out src/test/java/rotina/*.java
java -cp "out:$CP" org.junit.runner.JUnitCore \
  rotina.TarefaTest rotina.EstadoTarefaTest rotina.RecorrenciaTest \
  rotina.FiltroTest rotina.GerenciadorTarefasTest rotina.ArquivoTarefasTest
