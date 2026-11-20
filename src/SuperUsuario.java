import java.util.ArrayList;
import java.util.List;

/**PS-
 * 
 * Pessoa que, além das próprias tarefas, acompanha as tarefas dos seus
 * subordinados (ex.: mãe e filhos, chefe e equipe). Entra com senha.
 *
 * Pode existir hierarquia (chefe > gerente > funcionário): um super
 * usuário também pode ser subordinado, mas cada pessoa tem um só chefe.
 */



public class SuperUsuario extends Pessoa {
    private final String senha;
    private final List<Pessoa> subordinados = new ArrayList<>();

    public SuperUsuario(String nome, String senha) {
        super(nome);
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("O super usuário precisa de uma senha.");
        }
        this.senha = senha;
    }



    // ------------------------------------------------------------ polimorfismo
    @Override
    public String getNivel() {
        return "super";
    }

    @Override
    public boolean precisaSenha() {
        return true;
    }

    @Override
    public boolean senhaCorreta(String senha) {
        return this.senha.equals(senha);
    }

    String getSenha() {
        return senha;
    }




    // ------------------------------------------------------------ subordinados

    public List<Pessoa> getSubordinados() {
        return new ArrayList<>(subordinados);
    }

    // pode adicionar quem ainda não tem chefe, não é ele mesmo e não está acima dele.
    public boolean podeAdicionar(Pessoa pessoa) {
        return pessoa != this && pessoa.getChefe() == null && !estaAcimaDeMim(pessoa);
    }

    public void adicionarSubordinado(Pessoa pessoa) {
        if (!podeAdicionar(pessoa)) {
            throw new IllegalArgumentException(
                    pessoa.getNome() + " não pode ser subordinado(a) de " + getNome() + ".");
        }
        subordinados.add(pessoa);
        pessoa.setChefe(this);
    }

    public void removerSubordinado(Pessoa pessoa) {
        if (!subordinados.remove(pessoa)) {
            throw new IllegalArgumentException(
                    pessoa.getNome() + " não é subordinado(a) de " + getNome() + ".");
        }
        pessoa.setChefe(null);
    }

    /**PS-
     * Prepara a pasta onde o chefe coloca tarefas para o subordinado.
     * A pasta tem o nome do chefe (ex.: o filho recebe a pasta "Mãe").
     */
    public String pastaPara(Pessoa subordinado) {
        if (!subordinados.contains(subordinado)) {
            throw new IllegalArgumentException(
                    subordinado.getNome() + " não é subordinado(a) de " + getNome() + ".");
        }
        subordinado.garantirPasta(getNome());
        return getNome();
    }


    private boolean estaAcimaDeMim(Pessoa pessoa) {
        for (SuperUsuario c = getChefe(); c != null; c = c.getChefe()) {
            if (c == pessoa) {
                return true;
            }
        }
        return false;
    }
}
