package rotina;

/** Status inicial de toda tarefa. Pode ser iniciada ou concluída diretamente. */
public class Pendente extends EstadoTarefa {
    public static final String NOME = "Pendente";

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public void iniciar(Tarefa tarefa) {
        tarefa.setEstado(new EmAndamento());
    }

    @Override
    public void concluir(Tarefa tarefa) {
        tarefa.setEstado(new Concluida());
    }
}
