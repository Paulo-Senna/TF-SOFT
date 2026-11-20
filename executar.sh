#!/bin/sh
# Compila e abre o menu do Gerenciador de Rotina.
set -e
mkdir -p out
javac --release 17 -encoding UTF-8 -cp "lib/*" -d out src/*.java
java -cp out Main
