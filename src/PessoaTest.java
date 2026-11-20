import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes da classe abstrata Pessoa (usando a subclasse UsuarioComum).
 * nome da pessoa vira o nome do arquivo no banco, então um nome inválido quebraria o salvamento. As regras de pastas garantem que nenhuma tarefa fique sem pasta.
 */


public class PessoaTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 20);

    private Pessoa pessoa;

    @Before
    public void criarPessoa() {
        pessoa = new UsuarioComum("Ana");
    }

    @Test
    public void usuarioComumComecaComPastaGeralESemSenha() {
        assertEquals("Ana", pessoa.getNome());
        assertEquals("comum", pessoa.getNivel());
        assertTrue(pessoa.temPasta("Geral"));
        assertFalse(pessoa.precisaSenha());
        assertNull(pessoa.getChefe());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nomeVazioLancaErro() {
        new UsuarioComum("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nomeComCaractereProibidoLancaErro() {
        new UsuarioComum("Ana/Maria");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nomeCom31CaracteresLancaErro() {
        new UsuarioComum("a".repeat(31));
    }

    @Test(expected = IllegalArgumentException.class)
    public void criarPastaRepetidaLancaErro() {
        pessoa.criarPasta("Escola");
        pessoa.criarPasta("escola");
    }

    @Test
    public void novaTarefaFicaNaPastaEscolhida() {
        pessoa.criarPasta("Escola");

        Tarefa t = pessoa.novaTarefa("Prova", "", HOJE, Prioridade.ALTA, Recorrencia.NENHUMA, "Escola");

        assertEquals("Escola", t.getPasta());
        assertEquals(1, pessoa.getTarefas().filtrarPorPasta("Escola").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void novaTarefaEmPastaInexistenteLancaErro() {
        pessoa.novaTarefa("Prova", "", HOJE, Prioridade.ALTA, Recorrencia.NENHUMA, "Escola");
    }

    @Test
    public void removerPastaVaziaFunciona() {
        pessoa.criarPasta("Escola");
        pessoa.removerPasta("Escola");
        assertFalse(pessoa.temPasta("Escola"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removerPastaComTarefasLancaErro() {
        pessoa.criarPasta("Escola");
        pessoa.novaTarefa("Prova", "", HOJE, Prioridade.ALTA, Recorrencia.NENHUMA, "Escola");
        pessoa.removerPasta("Escola");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removerPastaGeralLancaErro() {
        pessoa.removerPasta("Geral");
    }
}
