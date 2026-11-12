package rotina;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/** Interface de console do Gerenciador de Rotina. */
public class Main {
    private static final Path ARQUIVO = Paths.get("tarefas.txt");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        GerenciadorTarefas g = new GerenciadorTarefas(ArquivoTarefas.carregar(ARQUIVO));
        avisarAtrasadas(g);

        while (true) {
            System.out.println("\n=== GERENCIADOR DE ROTINA ===");
            System.out.println("1) Nova tarefa        2) Listar tarefas    3) Editar tarefa");
            System.out.println("4) Iniciar tarefa     5) Concluir tarefa   6) Reabrir tarefa");
            System.out.println("7) Remover tarefa     8) Filtrar           9) Ver atrasadas");
            System.out.println("0) Salvar e sair");
            String opcao = ler("Opção: ");
            try {
                switch (opcao) {
                    case "1": novaTarefa(g); break;
                    case "2": mostrar(g.listar()); break;
                    case "3": editar(g); break;
                    case "4": g.iniciar(lerId()); System.out.println("Tarefa iniciada."); break;
                    case "5": concluir(g); break;
                    case "6": g.reabrir(lerId()); System.out.println("Tarefa reaberta."); break;
                    case "7": g.remover(lerId()); System.out.println("Tarefa removida."); break;
                    case "8": filtrar(g); break;
                    case "9": mostrar(g.listarAtrasadas(LocalDate.now())); break;
                    case "0":
                        ArquivoTarefas.salvar(g.listar(), ARQUIVO);
                        System.out.println("Tarefas salvas. Até logo!");
                        return;
                    default: System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void novaTarefa(GerenciadorTarefas g) {
        String titulo = ler("Título: ");
        String descricao = ler("Descrição (opcional): ");
        LocalDate prazo = lerData("Prazo (dd/mm/aaaa): ");
        Prioridade p = Prioridade.valueOf(ler("Prioridade (BAIXA, MEDIA, ALTA): ").toUpperCase());
        Recorrencia r = Recorrencia.valueOf(ler("Repete? (NENHUMA, DIARIA, SEMANAL): ").toUpperCase());
        System.out.println("Criada: " + g.adicionar(titulo, descricao, prazo, p, r));
    }

    private static void editar(GerenciadorTarefas g) {
        int id = lerId();
        Tarefa t = g.buscar(id);
        System.out.println("Atual: " + t + " (deixe em branco para manter)");
        String titulo = ler("Novo título: ");
        String descricao = ler("Nova descrição: ");
        String prazo = ler("Novo prazo (dd/mm/aaaa): ");
        String prioridade = ler("Nova prioridade: ");
        g.editar(id,
                titulo.isEmpty() ? t.getTitulo() : titulo,
                descricao.isEmpty() ? t.getDescricao() : descricao,
                prazo.isEmpty() ? t.getPrazo() : converterData(prazo),
                prioridade.isEmpty() ? t.getPrioridade() : Prioridade.valueOf(prioridade.toUpperCase()));
        System.out.println("Atualizada: " + g.buscar(id));
    }

    private static void concluir(GerenciadorTarefas g) {
        Tarefa proxima = g.concluir(lerId());
        System.out.println("Tarefa concluída.");
        if (proxima != null) {
            System.out.println("Próxima ocorrência criada: " + proxima);
        }
    }

    private static void filtrar(GerenciadorTarefas g) {
        String tipo = ler("Filtrar por (1) status ou (2) prioridade? ");
        Filtro filtro = tipo.equals("1")
                ? new FiltroPorStatus(ler("Status (Pendente, Em andamento, Concluída): "))
                : new FiltroPorPrioridade(Prioridade.valueOf(ler("Prioridade: ").toUpperCase()));
        mostrar(g.filtrar(filtro));
    }

    private static void avisarAtrasadas(GerenciadorTarefas g) {
        List<Tarefa> atrasadas = g.listarAtrasadas(LocalDate.now());
        if (!atrasadas.isEmpty()) {
            System.out.println("ATENÇÃO: você tem " + atrasadas.size() + " tarefa(s) atrasada(s):");
            mostrar(atrasadas);
        }
    }

    private static void mostrar(List<Tarefa> lista) {
        if (lista.isEmpty()) {
            System.out.println("(nenhuma tarefa)");
        }
        lista.forEach(System.out::println);
    }

    private static String ler(String msg) {
        System.out.print(msg);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private static int lerId() {
        try {
            return Integer.parseInt(ler("Id da tarefa: "));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id inválido.");
        }
    }

    private static LocalDate lerData(String msg) {
        return converterData(ler(msg));
    }

    private static LocalDate converterData(String texto) {
        try {
            return LocalDate.parse(texto, DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use dd/mm/aaaa.");
        }
    }
}
