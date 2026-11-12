package rotina;

/**
 * Padrão State: cada status da tarefa é um objeto que sabe quais
 * mudanças de status são permitidas a partir dele.
 *
 * Por padrão toda transição é inválida; cada subclasse libera as suas.
 */
public abstract class EstadoTarefa {

    public abstract String getNome();

    public void iniciar(Tarefa tarefa) {
        throw transicaoInvalida("iniciar");
    }

    public void concluir(Tarefa tarefa) {
        throw transicaoInvalida("concluir");
    }

    public void reabrir(Tarefa tarefa) {
        throw transicaoInvalida("reabrir");
    }

    private IllegalStateException transicaoInvalida(String acao) {
        return new IllegalStateException(
                "Não é possível " + acao + " uma tarefa com status '" + getNome() + "'.");
    }

    /** Recria o estado a partir do nome (usado ao carregar do arquivo). */
    public static EstadoTarefa porNome(String nome) {
        switch (nome) {
            case Pendente.NOME:
                return new Pendente();
            case EmAndamento.NOME:
                return new EmAndamento();
            case Concluida.NOME:
                return new Concluida();
            default:
                throw new IllegalArgumentException("Status desconhecido: " + nome);
        }
    }
}
