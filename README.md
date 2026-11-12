# Gerenciador de Rotina

Projeto Final de Engenharia de Software (SOFT) — UDESC
Alunos: Paulo Henrique Gomes de Senna e Leonardo Floriani

Aplicação de console em Java para organizar tarefas da rotina: cadastrar, editar,
remover, listar por prazo, mudar status (Pendente → Em andamento → Concluída),
filtrar por status/prioridade, ver tarefas atrasadas e repetir tarefas
diárias ou semanais. As tarefas são salvas no arquivo `tarefas.txt`.

## Requisitos
- Java 17 ou superior (JDK)

## Rodar os testes (JUnit 4)
Os jars do JUnit já estão na pasta `lib/`, não é preciso instalar nada.

```
./testar.sh        # Linux / macOS
testar.bat         # Windows
```
Ou, com Maven: `mvn test`

## Rodar o programa
```
./executar.sh      # Linux / macOS
executar.bat       # Windows
```

## Estrutura
```
src/main/java/rotina/
  Tarefa.java               entidade principal
  EstadoTarefa.java         padrão State (classe abstrata)
  Pendente.java, EmAndamento.java, Concluida.java   estados concretos
  Filtro.java               padrão Strategy (interface)
  FiltroPorStatus.java, FiltroPorPrioridade.java, FiltroAtrasadas.java
  GerenciadorTarefas.java   operações do sistema
  ArquivoTarefas.java       salvar/carregar em arquivo
  Prioridade.java, Recorrencia.java   enums
  Main.java                 menu de console
src/test/java/rotina/       56 testes unitários
docs/                       diagrama de classes (PlantUML + PNG)
```
