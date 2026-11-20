import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class BancoDadosTest {
    @Rule
    public TemporaryFolder pasta = new TemporaryFolder();

    private BancoDados banco;

    @Before
    public void criarBanco() {
        banco = new BancoDados(pasta.getRoot().toPath().resolve("bd"));
    }

    @Test
    public void carregarBancoQueNaoExisteDevolveListaVazia() throws IOException {
        assertTrue(banco.carregarTodas().isEmpty());
    }

    @Test
    public void salvarECarregarPreservaTodosOsCamposDaTarefa() throws IOException {
        Pessoa aluno = new UsuarioComum("Aluno");
        aluno.criarPasta("Escola");
        aluno.novaTarefa("Ler; artigo 100%", "Introdução\nConclusão", LocalDate.of(2026, 11, 20),
                Prioridade.ALTA, Recorrencia.SEMANAL, "Escola");
        aluno.getTarefas().concluir(1); // conclui e cria a da próxima semana (#2)

        banco.salvar(aluno);
        List<Pessoa> lidas = banco.carregarTodas();

        Tarefa lida = lidas.get(0).getTarefas().buscar(1);
        assertEquals("Ler; artigo 100%", lida.getTitulo());          //PS- ';' e '%' não quebram o arquivo
        assertEquals("Introdução\nConclusão", lida.getDescricao());  // acento e quebra de linha
        assertEquals(LocalDate.of(2026, 11, 20), lida.getPrazo());
        assertEquals(Prioridade.ALTA, lida.getPrioridade());
        assertEquals(Recorrencia.SEMANAL, lida.getRecorrencia());
        assertEquals("Concluída", lida.getStatus());
        assertEquals("Escola", lida.getPasta());
        assertEquals(2, lidas.get(0).getTarefas().listar().size());
    }

    @Test
    public void salvarECarregarPreservaSenhaEHierarquia() throws IOException {
        SuperUsuario mae = new SuperUsuario("Mãe", "1234");
        UsuarioComum filho = new UsuarioComum("Filho");
        mae.adicionarSubordinado(filho);

        banco.salvar(mae);
        banco.salvar(filho);
        List<Pessoa> lidas = banco.carregarTodas(); //PS- ordem alfabética: filho, mae

        Pessoa filhoLido = lidas.get(0);
        Pessoa maeLida = lidas.get(1);
        assertTrue(maeLida instanceof SuperUsuario);
        assertTrue(maeLida.senhaCorreta("1234"));
        assertSame(maeLida, filhoLido.getChefe());
    }
}
