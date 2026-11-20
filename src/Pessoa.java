import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Uma pessoa cadastrada no sistema, com suas próprias tarefas e pastas.
 *
 * Classe abstrata: ninguém é "só Pessoa". Toda pessoa é um UsuarioComum
 * ou um SuperUsuario (herança), e cada um responde do seu jeito a
 * getNivel(), precisaSenha() e senhaCorreta() (polimorfismo).
 */


public abstract class Pessoa {
    public static final String PASTA_PADRAO = "Geral";
    public static final int TAMANHO_MAX_NOME = 30;

    
    private static final String PROIBIDOS = "\\/:*?\"<>|;"; // caracteres proibidos no nome, pra nao vira nome de arquivo

    // ordem alfabetica sem diferenciar maiúsculas
    public static final Comparator<Pessoa> POR_NOME =
            Comparator.comparing(Pessoa::getNome, String.CASE_INSENSITIVE_ORDER);

    private final String nome;
    private final GerenciadorTarefas tarefas = new GerenciadorTarefas();
    private final List<String> pastas = new ArrayList<>();
    private SuperUsuario chefe; // null = não é subordinado de ninguém

    protected Pessoa(String nome) {
        this.nome = validarNome(nome);
        pastas.add(PASTA_PADRAO);
    }





    // ------------------------------------------------------------ polimorfismo

    // "comum" ou "super": cada subclasse diz o seu. 
    public abstract String getNivel();



    // por padrão ninguém precisa de senha; o SuperUsuario muda isso. 
    public boolean precisaSenha() {
        return false;
    }

    public boolean senhaCorreta(String senha) {
        return true;
    }




    // ------------------------------------------------------------ tarefas
    public GerenciadorTarefas getTarefas() {
        return tarefas;
    }


    // criar uma tarefa dentro de uma das pastas da pessoa. NOVA TAREFA
    public Tarefa novaTarefa(String titulo, String descricao, LocalDate prazo,
                             Prioridade prioridade, Recorrencia recorrencia, String pasta) {
        if (!temPasta(pasta)) {
            throw new IllegalArgumentException("A pasta '" + pasta + "' não existe.");
        }
        return tarefas.adicionar(titulo, descricao, prazo, prioridade, recorrencia, pasta);
    }




    // ------------------------------------------------------------ pastas
    public List<String> getPastas() {
        return new ArrayList<>(pastas);
    }

    public boolean temPasta(String pasta) {
        return pastas.stream().anyMatch(p -> p.equalsIgnoreCase(pasta.trim()));
    }

    public void criarPasta(String pasta) {
        if (pasta == null || pasta.isBlank()) {
            throw new IllegalArgumentException("O nome da pasta é obrigatório.");
        }
        if (temPasta(pasta)) {
            throw new IllegalArgumentException("Já existe a pasta '" + pasta.trim() + "'.");
        }
        pastas.add(pasta.trim());
    }



    //PS- cria a pasta só se ela ainda não existir
    public void garantirPasta(String pasta) {
        if (!temPasta(pasta)) {
            criarPasta(pasta);
        }
    }

    //só exclui pasta vazia, para nenhuma tarefa ficar sem pasta. 
    public void removerPasta(String pasta) {
        if (pasta.equalsIgnoreCase(PASTA_PADRAO)) {
            throw new IllegalArgumentException("A pasta " + PASTA_PADRAO + " não pode ser excluída.");
        }
        if (!tarefas.filtrarPorPasta(pasta).isEmpty()) {
            throw new IllegalArgumentException("Esvazie a pasta '" + pasta + "' antes de excluí-la.");
        }
        pastas.removeIf(p -> p.equalsIgnoreCase(pasta));
    }




    // ------------------------------------------------------------ hierarquia
    public SuperUsuario getChefe() {
        return chefe;
    }

    //ps- Só o SuperUsuario muda o chefe, ao adicionar ou remover subordinados
    void setChefe(SuperUsuario chefe) {
        this.chefe = chefe;
    }

    // ------------------------------------------------------------ dados
    public String getNome() {
        return nome;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        nome = nome.trim();
        if (nome.length() > TAMANHO_MAX_NOME) {
            throw new IllegalArgumentException(
                    "O nome deve ter no máximo " + TAMANHO_MAX_NOME + " caracteres.");
        }
        for (char c : nome.toCharArray()) {
            if (PROIBIDOS.indexOf(c) >= 0) {
                throw new IllegalArgumentException("O nome não pode ter o caractere '" + c + "'.");
            }
        }
        return nome;
    }

    @Override
    public String toString() {
        return nome + " [" + getNivel() + "]";
    }
}
