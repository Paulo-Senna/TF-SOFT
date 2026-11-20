import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Telas do Gerenciador de Rotina. Cada tela é um método com seu próprio menu:
 *
 *   Início > Pessoa > Minhas tarefas > Filtrar
 *                   > Minhas pastas > Pasta
 *                   > Subordinado (só super usuário)
 *
 * Toda alteração é salva na hora no banco (pasta bd/).
 */


public class Main {
    private static final BancoDados banco = new BancoDados(Paths.get("bd"));
    private static List<Pessoa> pessoas;

    public static void main(String[] args) throws IOException {
        pessoas = banco.carregarTodas();
        telaInicial();
    }



    // ============================================================ início
    private static void telaInicial() throws IOException {
        while (true) {
            Terminal.tela("Início");
            for (int i = 0; i < pessoas.size(); i++) {
                System.out.println((i + 1) + ") " + pessoas.get(i));
            }
            System.out.println("0) Cadastrar nova pessoa");
            System.out.println("S) Sair");
            String opcao = Terminal.ler("\nOpção: ");
            if (opcao.equalsIgnoreCase("S")) {
                System.out.println("Até logo!");
                return;
            }
            try {
                if (opcao.equals("0")) {
                    cadastrarPessoa();
                } else {
                    entrar(Terminal.escolher(opcao, pessoas));
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }



//PS-IO exception
    private static void cadastrarPessoa() throws IOException {
        Terminal.tela("Início", "Cadastrar nova pessoa");
        String nome = Terminal.ler("Insira seu nome: ");
        for (Pessoa p : pessoas) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                throw new IllegalArgumentException("Já existe uma pessoa chamada '" + nome + "'.");
            }
        }
        String nivel = Terminal.ler("Nível de acesso: (1) Usuário comum (2) Super usuário: ");
        Pessoa nova;
        if (nivel.equals("1")) {
            nova = new UsuarioComum(nome);
        } else if (nivel.equals("2")) {
            nova = new SuperUsuario(nome, Terminal.ler("Crie uma senha: "));
        } else {
            throw new IllegalArgumentException("Opção inválida.");
        }
        pessoas.add(nova);
        pessoas.sort(Pessoa.POR_NOME);
        banco.salvar(nova);
        Terminal.aviso("Cadastro feito: " + nova);
    }





    private static void entrar(Pessoa pessoa) throws IOException {
        if (pessoa.precisaSenha()
                && !pessoa.senhaCorreta(Terminal.ler("Senha de " + pessoa.getNome() + ": "))) {
            throw new IllegalArgumentException("Senha incorreta.");
        }
        menuPessoa(pessoa);
    }






    // ============================================================ pessoa
    private static void menuPessoa(Pessoa pessoa) throws IOException {
        while (true) {
            Terminal.tela("Início", pessoa.getNome());
            System.out.println("1) Nova tarefa");
            System.out.println("2) Minhas tarefas");
            System.out.println("3) Minhas pastas");
            if (pessoa instanceof SuperUsuario) {
                SuperUsuario chefe = (SuperUsuario) pessoa;
                System.out.println("4) Adicionar subordinado");
                System.out.println("5) Remover subordinado");
                List<Pessoa> subordinados = chefe.getSubordinados();
                if (!subordinados.isEmpty()) {
                    System.out.println("\nSubordinados:");
                }
                for (int i = 0; i < subordinados.size(); i++) {
                    System.out.println((i + 6) + ") " + subordinados.get(i));
                }
            }
            System.out.println("0) Voltar");
            String opcao = Terminal.ler("\nOpção: ");
            try {
                switch (opcao) {
                    case "0": return;
                    case "1": novaTarefa(pessoa, escolherPasta(pessoa)); break;
                    case "2": telaTarefas(pessoa); break;
                    case "3": telaPastas(pessoa); break;
                    default: opcaoDeSuperUsuario(pessoa, opcao);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }





    // ppcoes 4 em diante(somente superusuario)
    private static void opcaoDeSuperUsuario(Pessoa pessoa, String opcao) throws IOException {
        if (!(pessoa instanceof SuperUsuario)) {
            throw new IllegalArgumentException("Opção inválida.");
        }
        SuperUsuario chefe = (SuperUsuario) pessoa;
        if (opcao.equals("4")) {
            adicionarSubordinado(chefe);
        } else if (opcao.equals("5")) {
            removerSubordinado(chefe);
        } else {
            telaSubordinado(chefe, Terminal.escolher(opcao, chefe.getSubordinados(), 6));
        }
    }



    // ============================================================ minhas tarefas
    private static void telaTarefas(Pessoa pessoa) throws IOException {
        while (true) {
            Terminal.tela("Início", pessoa.getNome(), "Minhas tarefas");
            Terminal.mostrar(pessoa.getTarefas().listar());
            System.out.println("\n1) Concluir  2) Editar  3) Remover  4) Filtrar  0) Voltar");
            String opcao = Terminal.ler("Opção: ");
            try {
                switch (opcao) {
                    case "0": return;
                    case "1": concluir(pessoa); break;
                    case "2": editar(pessoa); break;
                    case "3": remover(pessoa); break;
                    case "4": filtrar(pessoa); break;
                    default: throw new IllegalArgumentException("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }

    private static void filtrar(Pessoa pessoa) {
        System.out.println("\nFiltrar: 1) Pendentes  2) Concluídas  3) Atrasadas  4) Por prioridade");
        String opcao = Terminal.ler("Opção: ");
        GerenciadorTarefas tarefas = pessoa.getTarefas();
        if (opcao.equals("4")) {
            Prioridade p = Terminal.converter(Prioridade.class,
                    Terminal.ler("Prioridade (BAIXA, MEDIA, ALTA): "));
            mostrarConsulta(tarefas.filtrarPorPrioridade(p),
                    "Início > " + pessoa.getNome() + " > Minhas tarefas > Prioridade " + p);
        } else {
            consultaPorSituacao(tarefas, opcao, "Início > " + pessoa.getNome() + " > Minhas tarefas");
        }
    }




    // ============================================================ pastas
    private static void telaPastas(Pessoa pessoa) throws IOException {
        while (true) {
            Terminal.tela("Início", pessoa.getNome(), "Minhas pastas");
            List<String> pastas = pessoa.getPastas();
            for (int i = 0; i < pastas.size(); i++) {
                int quantidade = pessoa.getTarefas().filtrarPorPasta(pastas.get(i)).size();
                System.out.println((i + 1) + ") " + pastas.get(i) + " (" + quantidade + " tarefa(s))");
            }
            System.out.println("\nDigite o número da pasta para abrir, ou:");
            System.out.println("N) Nova pasta  E) Excluir pasta  0) Voltar");
            String opcao = Terminal.ler("Opção: ");
            try {
                if (opcao.equals("0")) {
                    return;
                } else if (opcao.equalsIgnoreCase("N")) {
                    pessoa.criarPasta(Terminal.ler("Nome da nova pasta: "));
                    banco.salvar(pessoa);
                } else if (opcao.equalsIgnoreCase("E")) {
                    String pasta = Terminal.escolher(Terminal.ler("Número da pasta a excluir: "), pastas);
                    pessoa.removerPasta(pasta);
                    banco.salvar(pessoa);
                    Terminal.aviso("Pasta '" + pasta + "' excluída.");
                } else {
                    telaPasta(pessoa, Terminal.escolher(opcao, pastas));
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }




    //dentro de uma pasta, a lista mostra só as tarefas dela
    private static void telaPasta(Pessoa pessoa, String pasta) throws IOException {
        while (true) {
            Terminal.tela("Início", pessoa.getNome(), "Minhas pastas", pasta);
            Terminal.mostrar(pessoa.getTarefas().filtrarPorPasta(pasta));
            System.out.println("\n1) Nova tarefa nesta pasta  2) Concluir  3) Editar  4) Remover  0) Voltar");
            String opcao = Terminal.ler("Opção: ");
            try {
                switch (opcao) {
                    case "0": return;
                    case "1": novaTarefa(pessoa, pasta); break;
                    case "2": concluir(pessoa); break;
                    case "3": editar(pessoa); break;
                    case "4": remover(pessoa); break;
                    default: throw new IllegalArgumentException("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }

    private static String escolherPasta(Pessoa pessoa) {
        List<String> pastas = pessoa.getPastas();
        System.out.println("\nPastas:");
        for (int i = 0; i < pastas.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + pastas.get(i));
        }
        return Terminal.escolher(Terminal.ler("Número da pasta: "), pastas);
    }




    // ============================================================ subordinados
    /** O chefe vê o resumo e as tarefas do subordinado, como uma consulta. */
    private static void telaSubordinado(SuperUsuario chefe, Pessoa subordinado) throws IOException {
        while (true) {
            GerenciadorTarefas tarefas = subordinado.getTarefas();
            Terminal.tela("Início", chefe.getNome(), subordinado.getNome());
            System.out.println("Resumo: "
                    + tarefas.filtrarPorStatus(Pendente.NOME).size() + " pendente(s), sendo "
                    + tarefas.listarAtrasadas(Terminal.hoje()).size() + " atrasada(s) | "
                    + tarefas.filtrarPorStatus(Concluida.NOME).size() + " concluída(s)\n");
            Terminal.mostrar(tarefas.listar());
            System.out.println("\n1) Pendentes  2) Concluídas  3) Atrasadas  4) Nova tarefa para "
                    + subordinado.getNome() + "  5) Remover tarefa  0) Voltar");
            String opcao = Terminal.ler("Opção: ");
            try {
                switch (opcao) {
                    case "0": return;
                    case "4": novaTarefa(subordinado, chefe.pastaPara(subordinado)); break;
                    case "5": remover(subordinado); break;
                    default: consultaPorSituacao(tarefas, opcao,
                            "Início > " + chefe.getNome() + " > " + subordinado.getNome());
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                Terminal.aviso("Erro: " + e.getMessage());
            }
        }
    }

    private static void adicionarSubordinado(SuperUsuario chefe) throws IOException {
        List<Pessoa> candidatos = pessoas.stream()
                .filter(chefe::podeAdicionar)
                .collect(Collectors.toList());
        if (candidatos.isEmpty()) {
            Terminal.aviso("Todas as pessoas possíveis já são subordinadas.");
            return;
        }
        Pessoa escolhida = escolherPessoa(candidatos, "Adicionar subordinado", chefe);
        if (escolhida != null) {
            chefe.adicionarSubordinado(escolhida);
            banco.salvar(chefe);
            Terminal.aviso(escolhida.getNome() + " agora é subordinado(a) de " + chefe.getNome() + ".");
        }
    }

    private static void removerSubordinado(SuperUsuario chefe) throws IOException {
        List<Pessoa> subordinados = chefe.getSubordinados();
        if (subordinados.isEmpty()) {
            Terminal.aviso("Você ainda não tem subordinados.");
            return;
        }
        Pessoa escolhida = escolherPessoa(subordinados, "Remover subordinado", chefe);
        if (escolhida != null) {
            chefe.removerSubordinado(escolhida);
            banco.salvar(chefe);
            Terminal.aviso(escolhida.getNome() + " não é mais subordinado(a) de " + chefe.getNome() + ".");
        }
    }

    //lista as pessoas numeradas e devolve a escolhida (ou null se cancelar com 0)
    private static Pessoa escolherPessoa(List<Pessoa> lista, String titulo, Pessoa quemPede) {
        Terminal.tela("Início", quemPede.getNome(), titulo);
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ") " + lista.get(i));
        }
        System.out.println("0) Cancelar");
        String opcao = Terminal.ler("\nOpção: ");
        return opcao.equals("0") ? null : Terminal.escolher(opcao, lista);
    }






    // ============================================================ ações sobre tarefas
    private static void novaTarefa(Pessoa dono, String pasta) throws IOException {
        System.out.println("\n--- Nova tarefa para " + dono.getNome() + " (pasta " + pasta + ") ---");
        String titulo = Terminal.ler("Título: ");
        String descricao = Terminal.ler("Descrição (opcional): ");
        LocalDate prazo = Terminal.lerData("Prazo (dd/mm/aaaa): ");
        Prioridade p = Terminal.converter(Prioridade.class,
                Terminal.ler("Prioridade (BAIXA, MEDIA, ALTA): "));
        Recorrencia r = Terminal.converter(Recorrencia.class,
                Terminal.ler("Repete? (NENHUMA, DIARIA, SEMANAL, MENSAL): "));
        Tarefa t = dono.novaTarefa(titulo, descricao, prazo, p, r, pasta);
        banco.salvar(dono);
        Terminal.aviso("Criada: " + t);
    }

    private static void concluir(Pessoa pessoa) throws IOException {
        Tarefa proxima = pessoa.getTarefas().concluir(Terminal.lerId());
        banco.salvar(pessoa);
        Terminal.aviso("Tarefa concluída."
                + (proxima == null ? "" : "\nPróxima ocorrência criada: " + proxima));
    }

    private static void editar(Pessoa pessoa) throws IOException {
        int id = Terminal.lerId();
        Tarefa t = pessoa.getTarefas().buscar(id);
        System.out.println("Atual: " + t + "\n(deixe em branco para manter)");
        String titulo = Terminal.ler("Novo título: ");
        String descricao = Terminal.ler("Nova descrição: ");
        String prazo = Terminal.ler("Novo prazo (dd/mm/aaaa): ");
        String prioridade = Terminal.ler("Nova prioridade (BAIXA, MEDIA, ALTA): ");
        pessoa.getTarefas().editar(id,
                titulo.isEmpty() ? t.getTitulo() : titulo,
                descricao.isEmpty() ? t.getDescricao() : descricao,
                prazo.isEmpty() ? t.getPrazo() : Terminal.converterData(prazo),
                prioridade.isEmpty() ? t.getPrioridade() : Terminal.converter(Prioridade.class, prioridade));
        banco.salvar(pessoa);
        Terminal.aviso("Atualizada: " + t);
    }

    private static void remover(Pessoa pessoa) throws IOException {
        pessoa.getTarefas().remover(Terminal.lerId());
        banco.salvar(pessoa);
        Terminal.aviso("Tarefa removida.");
    }






    // ============================================================ consultas
    /**
     * Opcoes 1, 2 e 3 (pendentes, concluídas, atrasadas)
     */
    private static void consultaPorSituacao(GerenciadorTarefas tarefas, String opcao, String onde) {
        switch (opcao) {
            case "1": mostrarConsulta(tarefas.filtrarPorStatus(Pendente.NOME), onde + " > Pendentes"); break;
            case "2": mostrarConsulta(tarefas.filtrarPorStatus(Concluida.NOME), onde + " > Concluídas"); break;
            case "3": mostrarConsulta(tarefas.listarAtrasadas(Terminal.hoje()), onde + " > Atrasadas"); break;
            default: throw new IllegalArgumentException("Opção inválida.");
        }
    }

    // tela com o resultado de um filtro; ENTER volta
    private static void mostrarConsulta(List<Tarefa> resultado, String titulo) {
        Terminal.tela(titulo);
        Terminal.mostrar(resultado);
        Terminal.aviso(resultado.size() + " tarefa(s) encontrada(s).");
    }
}
