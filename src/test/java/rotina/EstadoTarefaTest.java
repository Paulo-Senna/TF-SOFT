package rotina;

import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/** Testa todas as mudanças de status permitidas e proibidas (padrão State). */
public class EstadoTarefaTest {
    private Tarefa tarefa;

    @Before
    public void criar() {
        tarefa = new Tarefa(1, "Relatório", "", LocalDate.of(2026, 11, 20),
                Prioridade.MEDIA, Recorrencia.NENHUMA);
    }

    // ------------------------------------------------ transições válidas
    @Test
    public void pendenteParaEmAndamento() {
        tarefa.iniciar();
        assertEquals("Em andamento", tarefa.getStatus());
    }

    @Test
    public void pendenteDiretoParaConcluida() {
        tarefa.concluir();
        assertEquals("Concluída", tarefa.getStatus());
        assertTrue(tarefa.estaConcluida());
    }

    @Test
    public void emAndamentoParaConcluida() {
        tarefa.iniciar();
        tarefa.concluir();
        assertTrue(tarefa.estaConcluida());
    }

    @Test
    public void concluidaReabertaVoltaParaPendente() {
        tarefa.concluir();
        tarefa.reabrir();
        assertEquals("Pendente", tarefa.getStatus());
        assertFalse(tarefa.estaConcluida());
    }

    // ------------------------------------------------ transições inválidas
    @Test
    public void naoPodeReabrirTarefaPendente() {
        assertTransicaoInvalida(tarefa::reabrir, "Pendente");
    }

    @Test
    public void naoPodeIniciarTarefaJaEmAndamento() {
        tarefa.iniciar();
        assertTransicaoInvalida(tarefa::iniciar, "Em andamento");
    }

    @Test
    public void naoPodeReabrirTarefaEmAndamento() {
        tarefa.iniciar();
        assertTransicaoInvalida(tarefa::reabrir, "Em andamento");
    }

    @Test
    public void naoPodeConcluirTarefaJaConcluida() {
        tarefa.concluir();
        assertTransicaoInvalida(tarefa::concluir, "Concluída");
    }

    @Test
    public void naoPodeIniciarTarefaConcluida() {
        tarefa.concluir();
        assertTransicaoInvalida(tarefa::iniciar, "Concluída");
    }

    /** Verifica que a ação lança erro E que o status não mudou. */
    private void assertTransicaoInvalida(Runnable acao, String statusEsperado) {
        try {
            acao.run();
            fail("Deveria lançar IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains(statusEsperado));
            assertEquals(statusEsperado, tarefa.getStatus());
        }
    }

    // ------------------------------------------------ porNome
    @Test
    public void porNomeRecriaCadaEstado() {
        assertTrue(EstadoTarefa.porNome("Pendente") instanceof Pendente);
        assertTrue(EstadoTarefa.porNome("Em andamento") instanceof EmAndamento);
        assertTrue(EstadoTarefa.porNome("Concluída") instanceof Concluida);
    }

    @Test(expected = IllegalArgumentException.class)
    public void porNomeComStatusDesconhecidoLancaErro() {
        EstadoTarefa.porNome("Cancelada");
    }
}
