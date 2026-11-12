package rotina;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GerenciadorTarefasTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    private GerenciadorTarefas g;

    @Before
    public void criar() {
        g = new GerenciadorTarefas();
    }

    private Tarefa nova(String titulo, LocalDate prazo, Prioridade p) {
        return g.adicionar(titulo, "", prazo, p, Recorrencia.NENHUMA);
    }

    // ------------------------------------------------------------ CRUD
    @Test
    public void adicionarGeraIdsSequenciais() {
        Tarefa a = nova("A", HOJE, Prioridade.BAIXA);
        Tarefa b = nova("B", HOJE, Prioridade.BAIXA);
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
        assertEquals(2, g.quantidade());
    }

    @Test
    public void adicionarTarefaInvalidaNaoSalvaNada() {
        try {
            nova("", HOJE, Prioridade.BAIXA);
            fail("Deveria rejeitar título vazio");
        } catch (IllegalArgumentException esperado) {
            assertEquals(0, g.quantidade());
        }
    }

    @Test
    public void buscarRetornaATarefaCerta() {
        nova("A", HOJE, Prioridade.BAIXA);
        Tarefa b = nova("B", HOJE, Prioridade.BAIXA);
        assertSame(b, g.buscar(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void buscarIdInexistenteLancaErro() {
        g.buscar(42);
    }

    @Test
    public void editarAlteraATarefa() {
        nova("A", HOJE, Prioridade.BAIXA);
        g.editar(1, "A editada", "nova descrição", HOJE.plusDays(3), Prioridade.ALTA);
        Tarefa t = g.buscar(1);
        assertEquals("A editada", t.getTitulo());
        assertEquals(Prioridade.ALTA, t.getPrioridade());
    }

    @Test
    public void removerTiraATarefaDaLista() {
        nova("A", HOJE, Prioridade.BAIXA);
        nova("B", HOJE, Prioridade.BAIXA);
        g.remover(1);
        assertEquals(1, g.quantidade());
        assertEquals("B", g.listar().get(0).getTitulo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removerIdInexistenteLancaErro() {
        g.remover(42);
    }

    // ------------------------------------------------------------ listagem
    @Test
    public void listarOrdenaPorPrazoEDepoisPorPrioridade() {
        nova("depois", HOJE.plusDays(5), Prioridade.ALTA);
        nova("hoje-baixa", HOJE, Prioridade.BAIXA);
        nova("hoje-alta", HOJE, Prioridade.ALTA);
        assertEquals(Arrays.asList("hoje-alta", "hoje-baixa", "depois"), titulos(g.listar()));
    }

    @Test
    public void listarDevolveCopiaQueNaoAlteraOGerenciador() {
        nova("A", HOJE, Prioridade.BAIXA);
        g.listar().clear();
        assertEquals(1, g.quantidade());
    }

    @Test
    public void filtrarUsaOFiltroInformado() {
        nova("A", HOJE, Prioridade.ALTA);
        nova("B", HOJE, Prioridade.BAIXA);
        nova("C", HOJE.plusDays(1), Prioridade.ALTA);
        assertEquals(Arrays.asList("A", "C"), titulos(g.filtrar(new FiltroPorPrioridade(Prioridade.ALTA))));
    }

    @Test
    public void listarAtrasadasMostraSoVencidasNaoConcluidas() {
        nova("vencida", HOJE.minusDays(3), Prioridade.MEDIA);
        nova("futura", HOJE.plusDays(3), Prioridade.MEDIA);
        Tarefa feita = nova("feita", HOJE.minusDays(5), Prioridade.MEDIA);
        g.concluir(feita.getId());
        assertEquals(Arrays.asList("vencida"), titulos(g.listarAtrasadas(HOJE)));
    }

    // ------------------------------------------------------------ status
    @Test
    public void fluxoCompletoDeStatusPeloGerenciador() {
        nova("A", HOJE, Prioridade.BAIXA);
        g.iniciar(1);
        assertEquals("Em andamento", g.buscar(1).getStatus());
        assertNull(g.concluir(1)); // não recorrente: não cria nova tarefa
        assertEquals("Concluída", g.buscar(1).getStatus());
        g.reabrir(1);
        assertEquals("Pendente", g.buscar(1).getStatus());
        assertEquals(1, g.quantidade());
    }

    @Test
    public void concluirTarefaRecorrenteCriaAProxima() {
        g.adicionar("Academia", "", HOJE, Prioridade.MEDIA, Recorrencia.DIARIA);
        Tarefa proxima = g.concluir(1);
        assertEquals(2, proxima.getId());
        assertEquals(HOJE.plusDays(1), proxima.getPrazo());
        assertEquals(2, g.quantidade());
        assertEquals(Arrays.asList("Academia"), titulos(g.filtrar(new FiltroPorStatus("Pendente"))));
    }

    @Test
    public void concluirDuasVezesLancaErroENaoDuplicaRecorrente() {
        g.adicionar("Academia", "", HOJE, Prioridade.MEDIA, Recorrencia.DIARIA);
        g.concluir(1);
        try {
            g.concluir(1);
            fail("Deveria lançar IllegalStateException");
        } catch (IllegalStateException esperado) {
            assertEquals(2, g.quantidade());
        }
    }

    // ------------------------------------------------------------ construtor com lista
    @Test
    public void construtorComTarefasExistentesContinuaOsIds() {
        Tarefa t5 = new Tarefa(5, "Antiga", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
        Tarefa t2 = new Tarefa(2, "Outra", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA);
        GerenciadorTarefas carregado = new GerenciadorTarefas(Arrays.asList(t5, t2));
        assertEquals(2, carregado.quantidade());
        assertEquals(6, carregado.adicionar("Nova", "", HOJE, Prioridade.BAIXA, Recorrencia.NENHUMA).getId());
    }

    private static List<String> titulos(List<Tarefa> lista) {
        return lista.stream().map(Tarefa::getTitulo).collect(java.util.stream.Collectors.toList());
    }
}
