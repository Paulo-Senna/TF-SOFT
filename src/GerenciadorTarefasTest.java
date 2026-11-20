import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes da classe GerenciadorTarefas
 *
 * Por que é crítica: concentra as operações usadas por todas as telas
 * (cadastrar, buscar, remover, listar, filtrar e concluir). A ordenação e
 * os filtros são exatamente o que o usuário vê, e concluir() é o ponto
 * onde as tarefas recorrentes se multiplicam.
 */


public class GerenciadorTarefasTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 20);

    private GerenciadorTarefas g;
    private Tarefa vencida;
    private Tarefa hoje;
    private Tarefa amanha;

    @Before
    public void criarTresTarefas() {
        g = new GerenciadorTarefas();
        // cadastradas fora de ordem de propósito
        amanha = g.adicionar("Amanhã", "", HOJE.plusDays(1), Prioridade.ALTA, Recorrencia.NENHUMA, "Geral");
        hoje = g.adicionar("Hoje", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA, "Escola");
        vencida = g.adicionar("Vencida", "", HOJE.minusDays(1), Prioridade.BAIXA, Recorrencia.NENHUMA, "Geral");
    }

    @Test
    public void adicionarGeraIdsEmSequencia() {
        assertEquals(1, amanha.getId());
        assertEquals(2, hoje.getId());
        assertEquals(3, vencida.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void buscarIdInexistenteLancaErro() {
        g.buscar(99);
    }

    @Test
    public void removerTiraATarefaDaLista() {
        g.remover(hoje.getId());
        assertEquals(2, g.listar().size());
        assertFalse(g.listar().contains(hoje));
    }

    @Test
    public void editarAlteraTituloPrazoEPrioridadeDaTarefaBuscada() {
        g.editar(hoje.getId(), "Hoje revisado", "", HOJE.plusDays(3), Prioridade.ALTA);

        Tarefa editada = g.buscar(hoje.getId());
        assertEquals("Hoje revisado", editada.getTitulo());
        assertEquals(HOJE.plusDays(3), editada.getPrazo());
        assertEquals(Prioridade.ALTA, editada.getPrioridade());
    }

    @Test
    public void listarOrdenaPorPrazo() {
        List<Tarefa> lista = g.listar();

        assertEquals("Vencida", lista.get(0).getTitulo());
        assertEquals("Hoje", lista.get(1).getTitulo());
        assertEquals("Amanhã", lista.get(2).getTitulo());
    }

    @Test
    public void noMesmoDiaMaiorPrioridadeVemPrimeiro() {
        g.adicionar("Urgente", "", HOJE, Prioridade.ALTA, Recorrencia.NENHUMA, "Geral");

        List<Tarefa> lista = g.listar();

        assertEquals("Urgente", lista.get(1).getTitulo());
        assertEquals("Hoje", lista.get(2).getTitulo());
    }

    @Test
    public void filtrarPorStatusIgnoraMaiusculasEEspacos() {
        g.concluir(hoje.getId());

        List<Tarefa> concluidas = g.filtrarPorStatus("  concluída ");

        assertEquals(1, concluidas.size());
        assertEquals("Hoje", concluidas.get(0).getTitulo());
        assertEquals(2, g.filtrarPorStatus("Pendente").size());
    }

    @Test
    public void filtrarPorPrioridadeDevolveSoAPrioridadeEscolhida() {
        assertEquals(1, g.filtrarPorPrioridade(Prioridade.ALTA).size());
        assertEquals(2, g.filtrarPorPrioridade(Prioridade.BAIXA).size());
    }

    @Test
    public void filtrarPorPastaDevolveSoAsTarefasDaPasta() {
        List<Tarefa> escola = g.filtrarPorPasta("escola");

        assertEquals(1, escola.size());
        assertEquals("Hoje", escola.get(0).getTitulo());
    }

    @Test
    public void listarAtrasadasIgnoraTarefasConcluidas() {
        assertEquals(1, g.listarAtrasadas(HOJE).size());

        g.concluir(vencida.getId());

        assertEquals(0, g.listarAtrasadas(HOJE).size());
    }

    @Test
    public void concluirTarefaRecorrenteCriaAProximaComNovoId() {
        Tarefa mensal = g.adicionar("Aluguel", "", HOJE, Prioridade.ALTA, Recorrencia.MENSAL, "Geral");

        Tarefa proxima = g.concluir(mensal.getId());

        assertEquals(5, proxima.getId());
        assertEquals(LocalDate.of(2026, 12, 20), proxima.getPrazo());
        assertEquals(5, g.listar().size());
    }

    @Test
    public void concluirTarefaSemRecorrenciaNaoCriaNada() {
        assertNull(g.concluir(hoje.getId()));
        assertEquals(3, g.listar().size());
    }
}
