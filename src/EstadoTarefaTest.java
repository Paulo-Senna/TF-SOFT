import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes do padrão State: EstadoTarefa, Pendente e Concluida.
 *
 * Por que é crítico: o estado decide quais mudanças de status são permitidas. Concluir duas vezes geraria ocorrências repetidas.
 * 
 */


public class EstadoTarefaTest {
    private Tarefa tarefa;

    @Before
    public void criarTarefa() {
        tarefa = new Tarefa(1, "Estudar", "", LocalDate.of(2026, 11, 20),
                Prioridade.MEDIA, Recorrencia.NENHUMA, "Geral");
    }

    @Test
    public void pendenteAoConcluirViraConcluida() {
        assertEquals("Pendente", tarefa.getStatus());
        tarefa.concluir();
        assertEquals("Concluída", tarefa.getStatus());
        assertTrue(tarefa.estaConcluida());
    }

    @Test(expected = IllegalStateException.class)
    public void concluirDuasVezesLancaErro() {
        tarefa.concluir();
        tarefa.concluir();
    }

    @Test
    public void porNomeRecriaCadaEstado() {
        assertTrue(EstadoTarefa.porNome("Pendente") instanceof Pendente);
        assertTrue(EstadoTarefa.porNome("Concluída") instanceof Concluida);
    }

    @Test(expected = IllegalArgumentException.class)
    public void porNomeDesconhecidoLancaErro() {
        EstadoTarefa.porNome("Em andamento");
    }
}
