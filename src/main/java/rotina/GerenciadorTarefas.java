package rotina;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Guarda as tarefas e concentra as operações do sistema. */
public class GerenciadorTarefas {

    /** Ordem padrão: prazo mais próximo primeiro; no mesmo dia, maior prioridade primeiro. */
    private static final Comparator<Tarefa> ORDEM =
            Comparator.comparing(Tarefa::getPrazo)
                      .thenComparing(Tarefa::getPrioridade, Comparator.reverseOrder());

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public GerenciadorTarefas() {
    }

    /** Cria o gerenciador com tarefas já existentes (ex.: carregadas do arquivo). */
    public GerenciadorTarefas(List<Tarefa> existentes) {
        for (Tarefa t : existentes) {
            tarefas.add(t);
            proximoId = Math.max(proximoId, t.getId() + 1);
        }
    }

    public Tarefa adicionar(String titulo, String descricao, LocalDate prazo,
                            Prioridade prioridade, Recorrencia recorrencia) {
        Tarefa tarefa = new Tarefa(proximoId, titulo, descricao, prazo, prioridade, recorrencia);
        tarefas.add(tarefa);
        proximoId++;
        return tarefa;
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

    public List<Tarefa> listar() {
        List<Tarefa> copia = new ArrayList<>(tarefas);
        copia.sort(ORDEM);
        return copia;
    }

    public List<Tarefa> filtrar(Filtro filtro) {
        return listar().stream().filter(filtro::aceita).collect(Collectors.toList());
    }

    public List<Tarefa> listarAtrasadas(LocalDate hoje) {
        return filtrar(new FiltroAtrasadas(hoje));
    }

    public void iniciar(int id) {
        buscar(id).iniciar();
    }

    /**
     * Conclui a tarefa. Se ela for recorrente, cria automaticamente a
     * próxima ocorrência e a retorna; caso contrário retorna null.
     */
    public Tarefa concluir(int id) {
        Tarefa tarefa = buscar(id);
        tarefa.concluir();
        Tarefa proxima = tarefa.criarProximaOcorrencia(proximoId);
        if (proxima != null) {
            tarefas.add(proxima);
            proximoId++;
        }
        return proxima;
    }

    public void reabrir(int id) {
        buscar(id).reabrir();
    }

    public int quantidade() {
        return tarefas.size();
    }
}
