//tarefa finalizada, não muda mais de status
public class Concluida extends EstadoTarefa {
    public static final String NOME = "Concluída";

    @Override
    public String getNome() {
        return NOME;
    }
}
