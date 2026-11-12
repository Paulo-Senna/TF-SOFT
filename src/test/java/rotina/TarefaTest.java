package rotina;

import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class TarefaTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    private Tarefa tarefa;

    @Before
    public void criar() {
        tarefa = new Tarefa(1, "Estudar SOFT", "capítulo 3", LocalDate.of(2026, 11, 20),
                Prioridade.ALTA, Recorrencia.NENHUMA);
    }

    // ------------------------------------------------------------ criação
    @Test
    public void criaTarefaPendenteComOsDadosInformados() {
        assertEquals(1, tarefa.getId());
        assertEquals("Estudar SOFT", tarefa.getTitulo());
        assertEquals("capítulo 3", tarefa.getDescricao());
        assertEquals(LocalDate.of(2026, 11, 20), tarefa.getPrazo());
        assertEquals(Prioridade.ALTA, tarefa.getPrioridade());
        assertEquals("Pendente", tarefa.getStatus());
    }

    @Test
    public void tituloEDescricaoSemEspacosExtrasEDescricaoNulaViraVazia() {
        Tarefa t = new Tarefa(2, "  Ler  ", null, HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
        assertEquals("Ler", t.getTitulo());
        assertEquals("", t.getDescricao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tituloVazioNaoEPermitido() {
        new Tarefa(2, "   ", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tituloNuloNaoEPermitido() {
        new Tarefa(2, null, "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
    }

    @Test
    public void tituloCom100CaracteresEAceitoE101NaoE() {
        new Tarefa(2, "x".repeat(100), "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
        try {
            new Tarefa(3, "x".repeat(101), "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
            fail("Deveria rejeitar título com 101 caracteres");
        } catch (IllegalArgumentException esperado) {
            // ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void prazoEObrigatorio() {
        new Tarefa(2, "A", "", null, Prioridade.BAIXA, Recorrencia.NENHUMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prioridadeEObrigatoria() {
        new Tarefa(2, "A", "", HOJE, null, Recorrencia.NENHUMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void recorrenciaEObrigatoria() {
        new Tarefa(2, "A", "", HOJE, Prioridade.BAIXA, null);
    }

    // ------------------------------------------------------------ edição
    @Test
    public void editarAlteraOsDados() {
        tarefa.editar("Estudar COCOMO", "slides", LocalDate.of(2026, 12, 1), Prioridade.MEDIA);
        assertEquals("Estudar COCOMO", tarefa.getTitulo());
        assertEquals("slides", tarefa.getDescricao());
        assertEquals(LocalDate.of(2026, 12, 1), tarefa.getPrazo());
        assertEquals(Prioridade.MEDIA, tarefa.getPrioridade());
    }

    @Test
    public void editarComDadoInvalidoNaoAlteraNada() {
        try {
            tarefa.editar("Título novo", "desc nova", null, Prioridade.BAIXA);
            fail("Deveria rejeitar prazo nulo");
        } catch (IllegalArgumentException esperado) {
            assertEquals("Estudar SOFT", tarefa.getTitulo());
            assertEquals("capítulo 3", tarefa.getDescricao());
            assertEquals(Prioridade.ALTA, tarefa.getPrioridade());
        }
    }

    // ------------------------------------------------------------ atraso
    @Test
    public void prazoOntemEstaAtrasada() {
        Tarefa t = new Tarefa(2, "A", "", HOJE.minusDays(1), Prioridade.BAIXA, Recorrencia.NENHUMA);
        assertTrue(t.estaAtrasada(HOJE));
    }

    @Test
    public void prazoHojeAindaNaoEstaAtrasada() {
        Tarefa t = new Tarefa(2, "A", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
        assertFalse(t.estaAtrasada(HOJE));
    }

    @Test
    public void tarefaConcluidaNuncaEstaAtrasada() {
        Tarefa t = new Tarefa(2, "A", "", HOJE.minusDays(10), Prioridade.BAIXA, Recorrencia.NENHUMA);
        t.concluir();
        assertFalse(t.estaAtrasada(HOJE));
    }

    @Test
    public void tarefaEmAndamentoVencidaEstaAtrasada() {
        Tarefa t = new Tarefa(2, "A", "", HOJE.minusDays(1), Prioridade.BAIXA, Recorrencia.NENHUMA);
        t.iniciar();
        assertTrue(t.estaAtrasada(HOJE));
    }

    // ------------------------------------------------------------ recorrência
    @Test
    public void tarefaSemRecorrenciaNaoGeraProximaOcorrencia() {
        assertNull(tarefa.criarProximaOcorrencia(99));
    }

    @Test
    public void tarefaSemanalGeraProximaOcorrenciaUmaSemanaDepois() {
        Tarefa academia = new Tarefa(5, "Academia", "treino A", HOJE, Prioridade.MEDIA, Recorrencia.SEMANAL);
        academia.concluir();
        Tarefa proxima = academia.criarProximaOcorrencia(6);
        assertEquals(6, proxima.getId());
        assertEquals("Academia", proxima.getTitulo());
        assertEquals("treino A", proxima.getDescricao());
        assertEquals(HOJE.plusWeeks(1), proxima.getPrazo());
        assertEquals(Recorrencia.SEMANAL, proxima.getRecorrencia());
        assertEquals("Pendente", proxima.getStatus());
    }

    @Test
    public void toStringMostraDadosPrincipais() {
        assertTrue(tarefa.toString().contains("Estudar SOFT"));
        assertFalse(tarefa.toString().contains("repete"));
        Tarefa t = new Tarefa(2, "A", "", HOJE, Prioridade.BAIXA, Recorrencia.DIARIA);
        assertTrue(t.toString().contains("repete: DIARIA"));
    }
}
