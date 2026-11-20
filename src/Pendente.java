/** Status inicial de toda tarefa. Pode ser concluída. */


public class Pendente extends EstadoTarefa {
    public static final String NOME = "Pendente";

    @Override
    public String getNome() {
        return NOME;
    }

    @Override
    public void concluir(Tarefa tarefa) {
        tarefa.setEstado(new Concluida());
    }
}
