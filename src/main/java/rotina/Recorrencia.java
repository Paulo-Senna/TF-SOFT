package rotina;

import java.time.LocalDate;

/** Frequência com que uma tarefa da rotina se repete. */
public enum Recorrencia {
    NENHUMA, DIARIA, SEMANAL;

    /** Calcula o prazo da próxima ocorrência a partir do prazo atual. */
    public LocalDate proximaData(LocalDate data) {
        switch (this) {
            case DIARIA:
                return data.plusDays(1);
            case SEMANAL:
                return data.plusWeeks(1);
            default:
                throw new IllegalStateException("Tarefa sem recorrência não tem próxima data.");
        }
    }
}
