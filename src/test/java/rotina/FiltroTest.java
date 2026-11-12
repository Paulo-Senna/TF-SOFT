package rotina;

import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/** Testa os filtros (padrão Strategy). */
public class FiltroTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    private Tarefa pendenteAlta, andamentoBaixa, concluidaVencida;

    @Before
    public void criar() {
        pendenteAlta = new Tarefa(1, "A", "", HOJE.minusDays(2), Prioridade.ALTA, Recorrencia.NENHUMA);
        andamentoBaixa = new Tarefa(2, "B", "", HOJE.plusDays(5), Prioridade.BAIXA, Recorrencia.NENHUMA);
        andamentoBaixa.iniciar();
        concluidaVencida = new Tarefa(3, "C", "", HOJE.minusDays(5), Prioridade.ALTA, Recorrencia.NENHUMA);
        concluidaVencida.concluir();
    }

    @Test
    public void filtroPorStatus() {
        Filtro f = new FiltroPorStatus("Em andamento");
        assertFalse(f.aceita(pendenteAlta));
        assertTrue(f.aceita(andamentoBaixa));
        assertFalse(f.aceita(concluidaVencida));
    }

    @Test
    public void filtroPorStatusIgnoraMaiusculas() {
        assertTrue(new FiltroPorStatus("PENDENTE").aceita(pendenteAlta));
    }

    @Test
    public void filtroPorPrioridade() {
        Filtro f = new FiltroPorPrioridade(Prioridade.ALTA);
        assertTrue(f.aceita(pendenteAlta));
        assertFalse(f.aceita(andamentoBaixa));
        assertTrue(f.aceita(concluidaVencida));
    }

    @Test
    public void filtroAtrasadasIgnoraConcluidas() {
        Filtro f = new FiltroAtrasadas(HOJE);
        assertTrue(f.aceita(pendenteAlta));
        assertFalse(f.aceita(andamentoBaixa));
        assertFalse(f.aceita(concluidaVencida));
    }
}
