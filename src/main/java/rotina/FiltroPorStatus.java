package rotina;

public class FiltroPorStatus implements Filtro {
    private final String status;

    public FiltroPorStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean aceita(Tarefa tarefa) {
        return tarefa.getStatus().equalsIgnoreCase(status);
    }
}
