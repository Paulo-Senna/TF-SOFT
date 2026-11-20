import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Test;

/**
 * Testes do enum Recorrencia.
 * proximaData() define o prazo de todas as tarefasque se repetem. Um erro aqui se propaga para todas as ocorrências futuras
 */


public class RecorrenciaTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 20);

    @Test
    public void diariaSomaUmDia() {
        assertEquals(LocalDate.of(2026, 11, 21), Recorrencia.DIARIA.proximaData(HOJE));
    }

    @Test
    public void semanalSomaSeteDias() {
        assertEquals(LocalDate.of(2026, 11, 27), Recorrencia.SEMANAL.proximaData(HOJE));
    }

    @Test
    public void mensalSomaUmMes() {
        assertEquals(LocalDate.of(2026, 12, 20), Recorrencia.MENSAL.proximaData(HOJE));
    }

    @Test
    public void mensalNoDia31VaiParaOUltimoDiaDoMesSeguinte() {
        assertEquals(LocalDate.of(2027, 2, 28),
                Recorrencia.MENSAL.proximaData(LocalDate.of(2027, 1, 31)));
    }

    @Test(expected = IllegalStateException.class)
    public void nenhumaNaoTemProximaData() {
        Recorrencia.NENHUMA.proximaData(HOJE);
    }
}
