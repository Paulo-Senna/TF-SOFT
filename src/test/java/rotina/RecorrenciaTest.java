package rotina;

import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Test;

public class RecorrenciaTest {

    @Test
    public void diariaSomaUmDia() {
        assertEquals(LocalDate.of(2026, 11, 11), Recorrencia.DIARIA.proximaData(LocalDate.of(2026, 11, 10)));
    }

    @Test
    public void diariaViraOAno() {
        assertEquals(LocalDate.of(2027, 1, 1), Recorrencia.DIARIA.proximaData(LocalDate.of(2026, 12, 31)));
    }

    @Test
    public void semanalSomaSeteDias() {
        assertEquals(LocalDate.of(2026, 11, 17), Recorrencia.SEMANAL.proximaData(LocalDate.of(2026, 11, 10)));
    }

    @Test
    public void semanalViraOMes() {
        assertEquals(LocalDate.of(2026, 12, 3), Recorrencia.SEMANAL.proximaData(LocalDate.of(2026, 11, 26)));
    }

    @Test(expected = IllegalStateException.class)
    public void nenhumaNaoTemProximaData() {
        Recorrencia.NENHUMA.proximaData(LocalDate.of(2026, 11, 10));
    }
}
