package rotina;

/** Tarefa finalizada. Só pode ser reaberta (volta para pendente). */
public class Concluida extends EstadoTarefa {
    public static final String NOME = "Concluída";

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public void reabrir(Tarefa tarefa) {
        tarefa.setEstado(new Pendente());
    }
}
