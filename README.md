# Gerenciador de Rotina

Projeto Final de Engenharia de Software (SOFT) — UDESC
Alunos: Paulo Henrique Gomes de Senna e Leonardo Floriani

## A ideia: organizar tarefas de um grupo de pessoas

Aplicação de console em Java para organizar a rotina de **várias pessoas ao mesmo
tempo**, com **hierarquia**:

- **Em casa:** a mãe (super usuário) distribui as tarefas da casa entre os filhos
  (usuários comuns) e acompanha o que está pendente, o que já foi feito e o que
  está atrasado.
- **No trabalho:** o chefe acompanha o gerente, que por sua vez acompanha os
  funcionários. Cada pessoa tem um único chefe direto.

Cada pessoa também tem as próprias tarefas, organizadas em **pastas** (ex.: "Escola",
"Casa"). Quando a mãe cria uma tarefa para o filho, ela vai para a pasta "Mãe" do filho.

## O que o sistema faz
- Cadastrar pessoas: **usuário comum** (entra direto) ou **super usuário** (entra com senha).
- Super usuário: adicionar ou remover subordinados, ver o resumo e as tarefas de cada um
  (pendentes, concluídas, atrasadas) e criar ou remover tarefas para eles.
- Tarefas: criar, listar por data (da mais próxima para a mais distante), concluir,
  editar, remover e filtrar (pendentes, concluídas, atrasadas, por prioridade).
- Tarefas atrasadas aparecem com o aviso `<< ATRASADA`.
- Repetição diária, semanal ou mensal: ao concluir, a próxima já é criada.
- Pastas: criar, abrir (mostra só as tarefas da pasta) e excluir (só se estiver vazia).
- **Tudo é salvo na hora** na pasta `bd/`, um arquivo `.txt` por pessoa, simulando
  um banco de dados. Fechou e abriu, está tudo lá.

## Requisitos
- Java 17 ou superior (JDK)

## Comandos (abra o terminal na pasta do projeto)

| O que fazer | Windows (PowerShell / terminal do VS Code) | Windows (Prompt de Comando) | Linux / macOS |
|---|---|---|---|
| Usar o programa | `.\executar.bat` | `executar.bat` | `./executar.sh` |
| Rodar todos os testes | `.\testar.bat` | `testar.bat` | `./testar.sh` |
| Rodar uma classe de teste | `.\testar.bat TarefaTest` | `testar.bat TarefaTest` | `./testar.sh TarefaTest` |

No PowerShell é obrigatório o `.\` antes do nome do arquivo.

### Usar o programa
Rode `.\executar.bat`. O programa compila sozinho e abre a tela inicial com as pessoas
cadastradas. A pasta `bd/` já vem com dados de exemplo:

| Pessoa | Nível | Como entrar |
|---|---|---|
| Filho1 | usuário comum | digite `1` (sem senha) |
| Filho2 | usuário comum | digite `2` (sem senha) |
| Mãe | super usuário | digite `3` e a senha `1234` |

- **Como usuário comum** (ex.: Filho1): crie tarefas, veja "Minhas tarefas"
  (concluir, editar, remover, filtrar) e organize "Minhas pastas".
- **Como super usuário** (Mãe): além disso, escolha um filho para ver o resumo e as
  tarefas dele, crie tarefas para ele e adicione ou remova subordinados.
- Para cadastrar outra pessoa, digite `0` na tela inicial. Para sair, digite `S`.

Tudo o que você faz é salvo na hora em `bd/<nome>.txt`.

### Rodar os testes unitários (JUnit 4)
Os jars do JUnit já estão na pasta `lib/`, não é preciso instalar nada.
São 57 testes em 8 classes de teste.

- **Todos os testes:** `.\testar.bat`. Se tudo passar, a última linha mostra `OK (57 tests)`.
- **Uma classe só:** `.\testar.bat NomeDaClasse`. As classes são: `TarefaTest`,
  `EstadoTarefaTest`, `RecorrenciaTest`, `GerenciadorTarefasTest`, `PessoaTest`,
  `SuperUsuarioTest`, `BancoDadosTest` e `TerminalTest`.

Os testes usam pastas temporárias e **não mexem** nos dados de `bd/`.

## Estrutura
Tudo fica em `src/`: cada classe do projeto e, ao lado, a classe de teste dela.
```
src/
  Pessoa.java               classe abstrata: nome, tarefas, pastas e chefe   | PessoaTest.java
    UsuarioComum.java         subclasse: só cuida das próprias tarefas        | (testada em PessoaTest)
    SuperUsuario.java         subclasse: senha + subordinados                 | SuperUsuarioTest.java
  Tarefa.java               uma tarefa (título, prazo, prioridade, pasta...)  | TarefaTest.java
  GerenciadorTarefas.java   lista de tarefas de uma pessoa                    | GerenciadorTarefasTest.java
  EstadoTarefa.java         padrão State (classe abstrata)                    | EstadoTarefaTest.java
    Pendente.java, Concluida.java   estados concretos                         | (testados em EstadoTarefaTest)
  Recorrencia.java          enum: NENHUMA, DIARIA, SEMANAL, MENSAL            | RecorrenciaTest.java
  Prioridade.java           enum: BAIXA, MEDIA, ALTA
  BancoDados.java           salva e carrega cada pessoa em bd/<nome>.txt      | BancoDadosTest.java
  Terminal.java             entrada e saída do console                        | TerminalTest.java
  Main.java                 as telas do menu
lib/                        jars do JUnit 4
bd/                         "banco de dados" (um .txt por pessoa)
```

## Testes unitários: o que foi testado e por quê
Não há 100% de cobertura. Os testes focam nas classes com as regras do sistema;
o topo de cada arquivo `*Test.java` explica por que aquela classe é crítica.

| Classe | Testes | Por que é crítica |
|---|---|---|
| `Tarefa` | 11 | Entidade central: validação, cálculo de atraso, edição sem corromper dados e próxima ocorrência. |
| `EstadoTarefa` (State) | 4 | Decide as mudanças de status; concluir duas vezes duplicaria tarefas recorrentes. |
| `Recorrencia` | 5 | Define o prazo de todas as tarefas que se repetem (inclui o caso 31/01 → 28/02). |
| `GerenciadorTarefas` | 12 | Operações usadas por todas as telas: ordenação, filtros e conclusão. |
| `Pessoa` / `UsuarioComum` | 10 | O nome vira nome de arquivo; as regras de pastas evitam tarefa sem pasta. |
| `SuperUsuario` | 10 | Controla o acesso (senha) e a hierarquia (um só chefe, sem ciclos). |
| `BancoDados` | 3 | É o que mantém os dados entre execuções; um erro aqui perde tudo. |
| `Terminal` (só `converterData`) | 2 | Única validação do formato dd/mm/aaaa digitado pelo usuário. |

**Não testadas:** `Main` e o restante de `Terminal` só leem o teclado e escrevem
na tela, sem regra de negócio própria (chamam as classes acima, que já estão testadas).
`Prioridade` é um enum sem lógica. Essas partes foram verificadas usando o programa.

Conceitos de POO usados:
- **Herança:** `UsuarioComum` e `SuperUsuario` herdam de `Pessoa`.
- **Classe abstrata:** ninguém é "só Pessoa"; `Pessoa` e `EstadoTarefa` são abstratas.
- **Polimorfismo:** `getNivel()`, `precisaSenha()` e `senhaCorreta()` funcionam de
  um jeito em cada subclasse.
- **Encapsulamento:** os atributos são privados; o chefe de uma pessoa só muda
  pelo `SuperUsuario` (ao adicionar ou remover subordinado).
- **Padrão State:** o status da tarefa (`Pendente` → `Concluida`) é um objeto.

## Formato do banco (`bd/<nome>.txt`)
```
#tipo=super
#senha=1234
#subordinados=Filho1;Filho2
#pastas=Geral;Casa
1;Pagar contas;Luz, água e internet;2026-11-25;ALTA;MENSAL;Pendente;Casa
```
Linhas com `#` são o cabeçalho da pessoa; as demais são as tarefas
(`id;título;descrição;prazo;prioridade;repetição;status;pasta`).
