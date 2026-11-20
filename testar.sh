#!/bin/sh
# Compila o projeto e roda os testes unitarios (JUnit 4).
# Uso: ./testar.sh              (roda todas as classes de teste)
#      ./testar.sh TarefaTest   (roda so uma classe)
set -e
rm -rf out && mkdir -p out
javac --release 17 -encoding UTF-8 -cp "lib/*" -d out src/*.java
if [ -z "$1" ]; then
  TESTES="TarefaTest EstadoTarefaTest RecorrenciaTest GerenciadorTarefasTest PessoaTest SuperUsuarioTest BancoDadosTest TerminalTest"
else
  TESTES="$1"
fi
java -cp "out:lib/*" org.junit.runner.JUnitCore $TESTES
