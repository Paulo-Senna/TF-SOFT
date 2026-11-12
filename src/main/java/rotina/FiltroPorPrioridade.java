package rotina;

public class FiltroPorPrioridade implements Filtro {
    private final Prioridade prioridade;

    public FiltroPorPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    @Override
    public boolean aceita(Tarefa tarefa) {
        return tarefa.getPrioridade() == prioridade;
    }
}
