package rotina;

/** Tarefa que o usuário começou a fazer. Só pode ser concluída. */
public class EmAndamento extends EstadoTarefa {
    public static final String NOME = "Em andamento";

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public void concluir(Tarefa tarefa) {
        tarefa.setEstado(new Concluida());
    }
}
