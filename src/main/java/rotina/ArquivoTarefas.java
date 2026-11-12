package rotina;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Salva e carrega as tarefas em um arquivo de texto, uma tarefa por linha:
 * id;titulo;descricao;prazo;prioridade;recorrencia;status
 * Título e descrição são codificados para que ';' e quebras de linha não
 * estraguem o arquivo.
 */
public class ArquivoTarefas {
    private static final String SEP = ";";

    public static void salvar(List<Tarefa> tarefas, Path arquivo) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Tarefa t : tarefas) {
            linhas.add(String.join(SEP,
                    String.valueOf(t.getId()),
                    codificar(t.getTitulo()),
                    codificar(t.getDescricao()),
                    t.getPrazo().toString(),
                    t.getPrioridade().name(),
                    t.getRecorrencia().name(),
                    codificar(t.getStatus())));
        }
        Files.write(arquivo, linhas, StandardCharsets.UTF_8);
    }

    /** Retorna lista vazia se o arquivo ainda não existe (primeira execução). */
    public static List<Tarefa> carregar(Path arquivo) throws IOException {
        List<Tarefa> tarefas = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return tarefas;
        }
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) {
                continue;
            }
            String[] c = linha.split(SEP, -1);
            if (c.length != 7) {
                throw new IOException("Linha inválida no arquivo: " + linha);
            }
            Tarefa t = new Tarefa(Integer.parseInt(c[0]), decodificar(c[1]), decodificar(c[2]),
                    LocalDate.parse(c[3]), Prioridade.valueOf(c[4]), Recorrencia.valueOf(c[5]));
            t.setEstado(EstadoTarefa.porNome(decodificar(c[6])));
            tarefas.add(t);
        }
        return tarefas;
    }

    private static String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    private static String decodificar(String texto) {
        return URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }
}
