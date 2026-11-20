import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// Funções de entrada e saída do console, usadas pelas telas do Main
public class Terminal {
    private static final Scanner in = new Scanner(System.in);
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_EXTENSO =
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("pt-BR"));

    public static LocalDate hoje() {
        return LocalDate.now();
    }





    // ------------------------------------------------------------ saída
   
   
    //LIMPA TELA


    public static void tela(String... caminho) {
        limpar();
        System.out.println("=== " + String.join(" > ", caminho) + " ===");
        System.out.println("Hoje: " + hoje().format(DATA_EXTENSO));
        System.out.println();
    }

 
    public static void mostrar(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("  (nenhuma tarefa)");
        }
        for (Tarefa t : tarefas) {
            System.out.println("  " + t + (t.estaAtrasada(hoje()) ? "   << ATRASADA" : ""));
        }
    }

    public static void aviso(String mensagem) {
        System.out.println("\n" + mensagem);
        ler("Pressione Enter para continuar...");
    }

    private static void limpar() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J"); //PS- código que limpa terminais
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(); // se não der para limpar, só pula uma linha
        }
    }




    // ------------------------------------------------------------ entrada
    public static String ler(String mensagem) {
        System.out.print(mensagem);
        if (!in.hasNextLine()) { // entrada fechada (ex.: Ctrl+Z): encerra o programa
            System.exit(0);
        }
        return in.nextLine().trim();
    }

    /**
     * Converte o número digitado no item correspondente da lista.
     * primeiroNumero é o número mostrado ao lado do primeiro item.
     */



    public static <T> T escolher(String digitado, List<T> itens, int primeiroNumero) {
        try {
            int indice = Integer.parseInt(digitado) - primeiroNumero;
            if (indice >= 0 && indice < itens.size()) {
                return itens.get(indice);
            }
        } catch (NumberFormatException e) {
            // cai no erro abaixo
        }
        throw new IllegalArgumentException("Opção inválida.");
    }

    public static <T> T escolher(String digitado, List<T> itens) {
        return escolher(digitado, itens, 1);
    }

    public static int lerId() {
        try {
            return Integer.parseInt(ler("Número da tarefa (#): "));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido.");
        }
    }

    public static LocalDate lerData(String mensagem) {
        return converterData(ler(mensagem));
    }

    public static LocalDate converterData(String texto) {
        try {
            return LocalDate.parse(texto, DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use dd/mm/aaaa.");
        }
    }

    // converte o texto digitado em uma opção do enum (ex.: "alta" vira Prioridade.ALTA)
    public static <E extends Enum<E>> E converter(Class<E> tipo, String texto) {
        try {
            return Enum.valueOf(tipo, texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Opção inválida: '" + texto + "'.");
        }
    }
}
