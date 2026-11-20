/**
 *cada status da tarefa é um objeto que sabe quais mudanças de status são permitidas a partir dele.
 *
 * Por padrão a transição é inválida. a subclasse que pode concluir libera.
 */

public abstract class EstadoTarefa {

    public abstract String getNome();

    public void concluir(Tarefa tarefa) {
        throw new IllegalStateException(
                "Não é possível concluir uma tarefa com status '" + getNome() + "'.");
    }

    public static EstadoTarefa porNome(String nome) {
        switch (nome) {
            case Pendente.NOME:
                return new Pendente();
            case Concluida.NOME:
                return new Concluida();
            default:
                throw new IllegalArgumentException("Status desconhecido: " + nome);
        }
    }
}
