package rotina;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Uma atividade da rotina do usuário. */
public class Tarefa {
    public static final int TAMANHO_MAX_TITULO = 100;

    private final int id;
    private String titulo;
    private String descricao;
    private LocalDate prazo;
    private Prioridade prioridade;
    private final Recorrencia recorrencia;
    private EstadoTarefa estado;

    public Tarefa(int id, String titulo, String descricao, LocalDate prazo,
                  Prioridade prioridade, Recorrencia recorrencia) {
        validar(titulo, prazo, prioridade);
        if (recorrencia == null) {
            throw new IllegalArgumentException("A recorrência é obrigatória.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
        this.prazo = prazo;
        this.prioridade = prioridade;
        this.recorrencia = recorrencia;
        this.estado = new Pendente();
    }

    // ------------------------------------------------------------ status
    public void iniciar() {
        estado.iniciar(this);
    }

    public void concluir() {
        estado.concluir(this);
    }

    public void reabrir() {
        estado.reabrir(this);
    }

    public String getStatus() {
        return estado.getNome();
    }

    public boolean estaConcluida() {
        return estado instanceof Concluida;
    }

    /** Chamado pelos objetos de estado (padrão State) e ao carregar do arquivo. */
    void setEstado(EstadoTarefa novoEstado) {
        this.estado = novoEstado;
    }

    // ------------------------------------------------------------ regras
    /** Atrasada = prazo já passou e a tarefa ainda não foi concluída. */
    public boolean estaAtrasada(LocalDate hoje) {
        return !estaConcluida() && prazo.isBefore(hoje);
    }

    /**
     * Para tarefas recorrentes, cria a próxima ocorrência (mesmos dados,
     * novo prazo, status pendente). Retorna null se a tarefa não se repete.
     */
    public Tarefa criarProximaOcorrencia(int novoId) {
        if (recorrencia == Recorrencia.NENHUMA) {
            return null;
        }
        return new Tarefa(novoId, titulo, descricao, recorrencia.proximaData(prazo),
                prioridade, recorrencia);
    }

    /** Altera os dados da tarefa. Valida tudo antes, para não alterar pela metade. */
    public void editar(String titulo, String descricao, LocalDate prazo, Prioridade prioridade) {
        validar(titulo, prazo, prioridade);
        this.titulo = titulo.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
        this.prazo = prazo;
        this.prioridade = prioridade;
    }

    private static void validar(String titulo, LocalDate prazo, Prioridade prioridade) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }
        if (titulo.trim().length() > TAMANHO_MAX_TITULO) {
            throw new IllegalArgumentException(
                    "O título deve ter no máximo " + TAMANHO_MAX_TITULO + " caracteres.");
        }
        if (prazo == null) {
            throw new IllegalArgumentException("O prazo é obrigatório.");
        }
        if (prioridade == null) {
            throw new IllegalArgumentException("A prioridade é obrigatória.");
        }
    }

    // ------------------------------------------------------------ getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDate getPrazo() { return prazo; }
    public Prioridade getPrioridade() { return prioridade; }
    public Recorrencia getRecorrencia() { return recorrencia; }

    @Override
    public String toString() {
        return String.format("#%d %s | prazo %s | %s | %s%s", id, titulo,
                prazo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                prioridade, getStatus(),
                recorrencia == Recorrencia.NENHUMA ? "" : " | repete: " + recorrencia);
    }
}
