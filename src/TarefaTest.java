import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes da classe Tarefa.
 *é a entidade central do sistema. Se a validação falhar,
 * entram dados inválidos no banco; se estaAtrasada() errar, o super usuário
 * recebe uma visão falsa das tarefas dos subordinados; se editar() alterar
 * pela metade, a tarefa fica corrompida.
 */
public class TarefaTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 20);

    private Tarefa tarefa;

    @Before
    public void criarTarefa() {
        tarefa = new Tarefa(1, "Estudar", "Capítulo 3", HOJE, Prioridade.ALTA,
                Recorrencia.NENHUMA, "Escola");
    }

    @Test
    public void criarGuardaOsDadosEComecaPendente() {
        assertEquals(1, tarefa.getId());
        assertEquals("Estudar", tarefa.getTitulo());
        assertEquals("Capítulo 3", tarefa.getDescricao());
        assertEquals(HOJE, tarefa.getPrazo());
        assertEquals(Prioridade.ALTA, tarefa.getPrioridade());
        assertEquals("Escola", tarefa.getPasta());
        assertEquals("Pendente", tarefa.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tituloVazioLancaErro() {
        new Tarefa(1, "   ", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA, "Geral");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tituloCom101CaracteresLancaErro() {
        new Tarefa(1, "a".repeat(101), "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA, "Geral");
    }

    @Test(expected = IllegalArgumentException.class)
    public void prazoNuloLancaErro() {
        new Tarefa(1, "Estudar", "", null, Prioridade.BAIXA, Recorrencia.NENHUMA, "Geral");
    }

    @Test
    public void tarefaPendenteComPrazoVencidoEstaAtrasada() {
        assertTrue(tarefa.estaAtrasada(HOJE.plusDays(1)));
    }

    @Test
    public void tarefaQueVenceHojeNaoEstaAtrasada() {
        assertFalse(tarefa.estaAtrasada(HOJE));
    }

    @Test
    public void tarefaConcluidaNuncaEstaAtrasada() {
        tarefa.concluir();
        assertFalse(tarefa.estaAtrasada(HOJE.plusDays(10)));
    }

    @Test
    public void editarAlteraTituloPrazoEPrioridade() {
        tarefa.editar("Revisar", "", HOJE.plusDays(2), Prioridade.BAIXA);

        assertEquals("Revisar", tarefa.getTitulo());
        assertEquals(HOJE.plusDays(2), tarefa.getPrazo());
        assertEquals(Prioridade.BAIXA, tarefa.getPrioridade());
    }

    @Test
    public void editarComDadoInvalidoNaoAlteraNada() {
        try {
            tarefa.editar("", "nova", HOJE.plusDays(5), Prioridade.BAIXA);
            fail("Deveria lançar erro");
        } catch (IllegalArgumentException e) {
            assertEquals("Estudar", tarefa.getTitulo());
            assertEquals(HOJE, tarefa.getPrazo());
        }
    }

    @Test
    public void proximaOcorrenciaMantemPastaEMudaPrazo() {
        Tarefa semanal = new Tarefa(1, "Treino", "", HOJE, Prioridade.MEDIA,
                Recorrencia.SEMANAL, "Esporte");

        Tarefa proxima = semanal.criarProximaOcorrencia(2);

        assertEquals(2, proxima.getId());
        assertEquals(HOJE.plusWeeks(1), proxima.getPrazo());
        assertEquals("Esporte", proxima.getPasta());
        assertEquals("Pendente", proxima.getStatus());
    }

    @Test
    public void tarefaSemRecorrenciaNaoTemProximaOcorrencia() {
        assertNull(tarefa.criarProximaOcorrencia(2));
    }
}
