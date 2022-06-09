import javax.json.Json;
import javax.json.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Peer {

    public static Servidor servidor;
    public static String nomePeer;
    private static BufferedReader leitor;

    public static String end;

    public static void main(String[] args) throws IOException {
        leitor = Utils.criarBufferedReader(System.in);

        System.out.print("> Digite um nome para este peer: ");
        nomePeer = leitor.readLine();

        System.out.print("> Digite o numero da porta deste peer: ");
        String endereco = leitor.readLine();
        end = endereco;

        servidor = new Servidor(Integer.parseInt(endereco));
        servidor.start();

        atualizarPeersConectados();
    }

    private static void atualizarPeersConectados() throws IOException {
        System.out.println("> Digite, separados por espaço, os peers a que deseja ouvir (e.g. localhost:1111): ");
        System.out.println("  Para pular esta etapa, digite 'p'");

        String resposta = leitor.readLine();
        List<String> peers = Arrays.asList(resposta.split("\\s"));

        if (!resposta.equals("p")) {
            for (String peer : peers) {
                conectarSockets(peer);
            }
        }

        iniciarComunicacao();
    }

    private static void conectarSockets(String peer) throws IOException {
        String[] endereco = peer.split(":");

        try {
            new Cliente(new Socket(endereco[0], Integer.parseInt(endereco[1]))).start();
        } catch (Exception e) {
            System.out.println("* Entrada Inválida, pulando para o próximo passo.");
        }
    }

    private static void iniciarComunicacao() {
        try {
            System.out.println("> Você pode começar a comunicação agora. Digite 's' para sair ou 'a' para adicionar peers");
            while (true) {
                System.out.print("> ");
                String comando = leitor.readLine();
                if (comando.equals("s")) {
                    break;
                } else if (comando.equals("a")) {
                    atualizarPeersConectados();
                } else {
                    enviarComando(comando);
                }
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enviarComando(String comando) {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, servidor.port)
                            .add(Utils.NOME_PEER, nomePeer)
                            .add(Utils.COMANDO, comando)
                            .build()
            );
        }

        servidor.enviarComando(string.toString());
    }

    public static void enviarResposta(String resposta, String port) {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, port)
                            .add(Utils.NOME_PEER, nomePeer)
                            .add(Utils.RESPOSTA, resposta)
                            .build()
            );
        }

        servidor.enviarComando(string.toString());
    }

}
