// pessoa que cuida só das próprias tarefas. Entra sem senha
public class UsuarioComum extends Pessoa {

    public UsuarioComum(String nome) {
        super(nome);
    }

    @Override
    public String getNivel() {
        return "comum";
    }
}
