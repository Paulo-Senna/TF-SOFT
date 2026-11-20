import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Test;

/**
 * Testes da classe Terminal (só a conversão de data, que não depende do teclado).
 *converterData() é a única validação do formato
 * dd/mm/aaaa digitado pelo usuário (RNF05). Se aceitar outro formato,
 * o prazo da tarefa pode ser gravado errado.
 */
public class TerminalTest {

    @Test
    public void converterDataNoFormatoBrasileiro() {
        assertEquals(LocalDate.of(2026, 11, 20), Terminal.converterData("20/11/2026"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void converterDataEmOutroFormatoLancaErro() {
        Terminal.converterData("2026-11-20");
    }
}
