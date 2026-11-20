import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * "Banco de dados" em arquivos de texto: cada pessoa vira um arquivo
 * bd/<nome>.txt. As linhas com # são o cabeçalho e as outras são tarefas:
 *
 *   #tipo=super
 *   #senha=1234
 *   #subordinados=Filho1;Filho2
 *   #pastas=Geral;Casa
 *   id;titulo;descricao;prazo;prioridade;recorrencia;status;pasta
 */


public class BancoDados {
    private static final String SEP = ";";

    private final Path pasta;

    public BancoDados(Path pasta) {
        this.pasta = pasta;
    }

    // ------------------------------------------------------------ salvar
    public void salvar(Pessoa pessoa) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("#tipo=" + pessoa.getNivel());
        if (pessoa instanceof SuperUsuario) {
            SuperUsuario s = (SuperUsuario) pessoa;
            linhas.add("#senha=" + codificar(s.getSenha()));
            linhas.add("#subordinados=" + juntar(s.getSubordinados().stream()
                    .map(Pessoa::getNome).collect(Collectors.toList())));
        }
        linhas.add("#pastas=" + juntar(pessoa.getPastas()));
        for (Tarefa t : pessoa.getTarefas().listar()) {
            linhas.add(String.join(SEP,
                    String.valueOf(t.getId()),
                    codificar(t.getTitulo()),
                    codificar(t.getDescricao()),
                    t.getPrazo().toString(),
                    t.getPrioridade().name(),
                    t.getRecorrencia().name(),
                    codificar(t.getStatus()),
                    codificar(t.getPasta())));
        }
        Files.createDirectories(pasta);
        Files.write(pasta.resolve(pessoa.getNome() + ".txt"), linhas, StandardCharsets.UTF_8);
    }





    // ------------------------------------------------------------ carregar
    //PS-le todas as pessoas do banco ->  Se a pasta não existe, devolve lista vazia.

    public List<Pessoa> carregarTodas() throws IOException {
        List<Pessoa> pessoas = new ArrayList<>();
        if (!Files.isDirectory(pasta)) {
            return pessoas;
        }


        //etapa1: lê cada arquivo e anota os subordinados de cada super usuário
        Map<SuperUsuario, String> subordinadosDe = new HashMap<>();
        try (DirectoryStream<Path> arquivos = Files.newDirectoryStream(pasta, "*.txt")) {
            for (Path arquivo : arquivos) {
                pessoas.add(carregar(arquivo, subordinadosDe));
            }
        }


        //etapa2: com todos lidos, liga cada chefe aos seus subordinados
        for (Map.Entry<SuperUsuario, String> e : subordinadosDe.entrySet()) {
            for (String nome : separar(e.getValue())) {
                e.getKey().adicionarSubordinado(buscar(pessoas, nome));
            }
        }
        pessoas.sort(Pessoa.POR_NOME);
        return pessoas;
    }



    private Pessoa carregar(Path arquivo, Map<SuperUsuario, String> subordinadosDe)
            throws IOException {
        String nome = arquivo.getFileName().toString().replaceFirst("\\.txt$", "");
        Map<String, String> cabecalho = new HashMap<>();
        List<String> linhasTarefas = new ArrayList<>();
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (linha.startsWith("#")) {
                String[] chaveValor = linha.substring(1).split("=", 2);
                cabecalho.put(chaveValor[0], chaveValor.length > 1 ? chaveValor[1] : "");
            } else if (!linha.isBlank()) {
                linhasTarefas.add(linha);
            }
        }

        Pessoa pessoa;
        if ("super".equals(cabecalho.get("tipo"))) {
            SuperUsuario s = new SuperUsuario(nome, decodificar(cabecalho.get("senha")));
            subordinadosDe.put(s, cabecalho.get("subordinados"));
            pessoa = s;
        } else {
            pessoa = new UsuarioComum(nome);
        }
        for (String p : separar(cabecalho.get("pastas"))) {
            pessoa.garantirPasta(p);
        }
        for (String linha : linhasTarefas) {
            Tarefa t = lerTarefa(linha);
            pessoa.garantirPasta(t.getPasta());
            pessoa.getTarefas().incluir(t);
        }
        return pessoa;
    }

    private Tarefa lerTarefa(String linha) throws IOException {
        String[] c = linha.split(SEP, -1);
        if (c.length != 8) {
            throw new IOException("Linha inválida no banco: " + linha);
        }
        Tarefa t = new Tarefa(Integer.parseInt(c[0]), decodificar(c[1]), decodificar(c[2]),
                LocalDate.parse(c[3]), Prioridade.valueOf(c[4]), Recorrencia.valueOf(c[5]),
                decodificar(c[7]));
        t.setEstado(EstadoTarefa.porNome(decodificar(c[6])));
        return t;
    }

    private static Pessoa buscar(List<Pessoa> pessoas, String nome) throws IOException {
        for (Pessoa p : pessoas) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        throw new IOException("Subordinado '" + nome + "' não existe no banco.");
    }






    // ------------------------------------------------------------ texto
    private static String juntar(List<String> textos) {
        return textos.stream().map(BancoDados::codificar).collect(Collectors.joining(SEP));
    }

    private static List<String> separar(String texto) {
        List<String> lista = new ArrayList<>();
        if (texto != null && !texto.isEmpty()) {
            for (String parte : texto.split(SEP)) {
                lista.add(decodificar(parte));
            }
        }
        return lista;
    }

    //PS-troca só o que quebraria o arquivo (%, ; e quebras de linha) por códigos.
    private static String codificar(String texto) {
        return texto.replace("%", "%25").replace(";", "%3B")
                    .replace("\n", "%0A").replace("\r", "%0D");
    }

    private static String decodificar(String texto) {
        return texto.replace("%0D", "\r").replace("%0A", "\n")
                    .replace("%3B", ";").replace("%25", "%");
    }
}
