package rotina;

/**
 * Padrão Strategy: cada implementação é um critério diferente para
 * selecionar tarefas. O GerenciadorTarefas usa qualquer um deles sem
 * precisar saber qual é.
 */
public interface Filtro {
    boolean aceita(Tarefa tarefa);
}
