import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da classe SuperUsuario.
 * controla quem vê as tarefas de quem. Um erro na senha deixa qualquer um entrar como super usuário; um erro na hierarquia 
 * deixa uma pessoa com dois chefes ou cria um ciclo (alguém chefe do
 * próprio chefe).
 */



public class SuperUsuarioTest {
    private SuperUsuario mae;
    private UsuarioComum filho;

    @Before
    public void criarMaeEFilho() {
        mae = new SuperUsuario("Mãe", "1234");
        filho = new UsuarioComum("Filho");
    }

    @Test
    public void superUsuarioPrecisaDaSenhaCerta() {
        assertEquals("super", mae.getNivel());
        assertTrue(mae.precisaSenha());
        assertTrue(mae.senhaCorreta("1234"));
        assertFalse(mae.senhaCorreta("errada"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void superUsuarioSemSenhaLancaErro() {
        new SuperUsuario("Chefe", "");
    }

    @Test
    public void adicionarSubordinadoDefineOChefe() {
        mae.adicionarSubordinado(filho);

        assertEquals(1, mae.getSubordinados().size());
        assertSame(mae, filho.getChefe());
    }

    @Test(expected = IllegalArgumentException.class)
    public void pessoaQueJaTemChefeNaoPodeTerOutro() {
        SuperUsuario pai = new SuperUsuario("Pai", "abcd");
        mae.adicionarSubordinado(filho);
        pai.adicionarSubordinado(filho);
    }

    @Test(expected = IllegalArgumentException.class)
    public void naoPodeSerSubordinadoDeSiMesmo() {
        mae.adicionarSubordinado(mae);
    }

    @Test
    public void hierarquiaDeTresNiveisFunciona() {
        SuperUsuario chefe = new SuperUsuario("Chefe", "1");
        SuperUsuario gerente = new SuperUsuario("Gerente", "2");
        UsuarioComum funcionario = new UsuarioComum("Funcionario");

        chefe.adicionarSubordinado(gerente);
        gerente.adicionarSubordinado(funcionario);

        assertSame(chefe, gerente.getChefe());
        assertSame(gerente, funcionario.getChefe());
    }

    @Test
    public void naoPodeAdicionarOProprioChefeComoSubordinado() {
        SuperUsuario chefe = new SuperUsuario("Chefe", "1");
        SuperUsuario gerente = new SuperUsuario("Gerente", "2");
        chefe.adicionarSubordinado(gerente);
        chefe.removerSubordinado(gerente);
        gerente.adicionarSubordinado(chefe);   // agora o gerente manda no chefe

        assertFalse(chefe.podeAdicionar(gerente)); // ciclo: o gerente já está acima
    }

    @Test
    public void removerSubordinadoLiberaAPessoa() {
        mae.adicionarSubordinado(filho);

        mae.removerSubordinado(filho);

        assertTrue(mae.getSubordinados().isEmpty());
        assertNull(filho.getChefe());
    }

    @Test
    public void pastaParaSubordinadoTemONomeDoChefe() {
        mae.adicionarSubordinado(filho);

        String pasta = mae.pastaPara(filho);

        assertEquals("Mãe", pasta);
        assertTrue(filho.temPasta("Mãe"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pastaParaQuemNaoESubordinadoLancaErro() {
        mae.pastaPara(filho);
    }
}
