package rotina;

import java.time.LocalDate;

public class FiltroAtrasadas implements Filtro {
    private final LocalDate hoje;

    public FiltroAtrasadas(LocalDate hoje) {
        this.hoje = hoje;
    }

    @Override
    public boolean aceita(Tarefa tarefa) {
        return tarefa.estaAtrasada(hoje);
    }
}
