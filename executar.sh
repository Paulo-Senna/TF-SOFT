#!/bin/sh
# Compila e abre o menu do Gerenciador de Rotina.
set -e
mkdir -p out
javac --release 17 -encoding UTF-8 -d out src/main/java/rotina/*.java
java -cp out rotina.Main
