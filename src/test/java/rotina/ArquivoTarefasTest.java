package rotina;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ArquivoTarefasTest {
    @Rule
    public TemporaryFolder pasta = new TemporaryFolder();

    @Test
    public void salvarECarregarPreservaTodosOsDados() throws IOException {
        Path arquivo = pasta.getRoot().toPath().resolve("tarefas.txt");
        Tarefa a = new Tarefa(1, "Pagar conta; luz", "linha 1\nlinha 2", LocalDate.of(2026, 11, 15),
                Prioridade.ALTA, Recorrencia.NENHUMA);
        Tarefa b = new Tarefa(7, "Academia", "", LocalDate.of(2026, 11, 12),
                Prioridade.MEDIA, Recorrencia.SEMANAL);
        b.iniciar();

        ArquivoTarefas.salvar(Arrays.asList(a, b), arquivo);
        List<Tarefa> lidas = ArquivoTarefas.carregar(arquivo);

        assertEquals(2, lidas.size());
        Tarefa a2 = lidas.get(0);
        assertEquals(1, a2.getId());
        assertEquals("Pagar conta; luz", a2.getTitulo());   // ';' não quebra o arquivo
        assertEquals("linha 1\nlinha 2", a2.getDescricao()); // quebra de linha preservada
        assertEquals(LocalDate.of(2026, 11, 15), a2.getPrazo());
        assertEquals(Prioridade.ALTA, a2.getPrioridade());
        assertEquals("Pendente", a2.getStatus());
        Tarefa b2 = lidas.get(1);
        assertEquals(7, b2.getId());
        assertEquals(Recorrencia.SEMANAL, b2.getRecorrencia());
        assertEquals("Em andamento", b2.getStatus());        // status preservado
    }

    @Test
    public void carregarArquivoInexistenteRetornaListaVazia() throws IOException {
        Path arquivo = pasta.getRoot().toPath().resolve("nao-existe.txt");
        assertTrue(ArquivoTarefas.carregar(arquivo).isEmpty());
    }

    @Test
    public void carregarIgnoraLinhasEmBranco() throws IOException {
        Path arquivo = pasta.newFile("t.txt").toPath();
        Files.write(arquivo, Arrays.asList("", "1;A;;2026-11-10;BAIXA;NENHUMA;Conclu%C3%ADda", "  "),
                StandardCharsets.UTF_8);
        List<Tarefa> lidas = ArquivoTarefas.carregar(arquivo);
        assertEquals(1, lidas.size());
        assertTrue(lidas.get(0).estaConcluida());
    }

    @Test(expected = IOException.class)
    public void carregarLinhaComCamposFaltandoLancaErro() throws IOException {
        Path arquivo = pasta.newFile("ruim.txt").toPath();
        Files.write(arquivo, Arrays.asList("1;A;2026-11-10"), StandardCharsets.UTF_8);
        ArquivoTarefas.carregar(arquivo);
    }
}
