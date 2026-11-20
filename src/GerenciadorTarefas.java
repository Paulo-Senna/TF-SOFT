import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// lista de tarefas de uma pessoa, com as operações sobre ela.
public class GerenciadorTarefas {

    //PS- ordem padrão: prazo mais próximo primeiro; no mesmo dia, maior prioridade primeiro
    private static final Comparator<Tarefa> ORDEM =
            Comparator.comparing(Tarefa::getPrazo)
                      .thenComparing(Tarefa::getPrioridade, Comparator.reverseOrder());

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    // ------------------------------------------------------------ cadastro
    public Tarefa adicionar(String titulo, String descricao, LocalDate prazo,
                            Prioridade prioridade, Recorrencia recorrencia, String pasta) {
        Tarefa tarefa = new Tarefa(proximoId, titulo, descricao, prazo, prioridade, recorrencia, pasta);
        incluir(tarefa);
        return tarefa;
    }

    //PS- coloca uma tarefa já existente na lista/ (carrega o banco)
    void incluir(Tarefa tarefa) {
        tarefas.add(tarefa);
        proximoId = Math.max(proximoId, tarefa.getId() + 1);
    }

    public Tarefa buscar(int id) {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tarefa " + id + " não encontrada.");
    }

    public void editar(int id, String titulo, String descricao, LocalDate prazo,
                       Prioridade prioridade) {
        buscar(id).editar(titulo, descricao, prazo, prioridade);
    }

    public void remover(int id) {
        tarefas.remove(buscar(id));
    }

    // ------------------------------------------------------------ consultas
    public List<Tarefa> listar() {
        List<Tarefa> copia = new ArrayList<>(tarefas);
        copia.sort(ORDEM);
        return copia;
    }

    //PS-caps sensitive pra nao dar bo
    public List<Tarefa> filtrarPorStatus(String status) {
        return filtrar(t -> t.getStatus().equalsIgnoreCase(status.trim()));
    }



    public List<Tarefa> filtrarPorPrioridade(Prioridade prioridade) {
        return filtrar(t -> t.getPrioridade() == prioridade);
    }

    public List<Tarefa> filtrarPorPasta(String pasta) {
        return filtrar(t -> t.getPasta().equalsIgnoreCase(pasta.trim()));
    }

    public List<Tarefa> listarAtrasadas(LocalDate hoje) {
        return filtrar(t -> t.estaAtrasada(hoje));
    }

    /** Aplica o critério sobre a lista já ordenada. */
    private List<Tarefa> filtrar(Predicate<Tarefa> criterio) {
        return listar().stream().filter(criterio).collect(Collectors.toList());
    }

    // ------------------------------------------------------------ status
    /**
     * Conclui a tarefa. Se ela for recorrente, cria automaticamente a próxima ocorrência e a retorna; 
     * senao return null;
     */
    
    public Tarefa concluir(int id) {
        Tarefa tarefa = buscar(id);
        tarefa.concluir();
        Tarefa proxima = tarefa.criarProximaOcorrencia(proximoId);
        if (proxima != null) {
            incluir(proxima);
        }
        return proxima;
    }
}
