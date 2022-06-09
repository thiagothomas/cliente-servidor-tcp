import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class Cliente extends Thread {

    private BufferedReader reader;

    Cliente(Socket socket) throws IOException {
        reader = Utils.criarBufferedReader(socket.getInputStream());
    }

    @Override
    public void run() {
        boolean aux = true;
        while (aux) {
            try {
                JsonObject mensagem = Json.createReader(reader).readObject();
                processarMensagem(mensagem);
            } catch (Exception e) {
                aux = false;
                super.interrupt();
            }
        }
    }

    private void processarMensagem(JsonObject mensagemJson) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (mensagemJson.containsKey(Utils.COMANDO)) {
            System.out.println("======================= EXECUTANDO COMANDO '" + mensagemJson.getString(Utils.COMANDO) + "' =======================");
            try {
                Process resultado = Runtime.getRuntime().exec(mensagemJson.getString(Utils.COMANDO));
                BufferedReader reader = Utils.criarBufferedReader(resultado.getInputStream());

                String linha;
                while ((linha = reader.readLine()) != null) {
                    stringBuilder.append(linha).append("\n");
                    System.out.println(linha);
                }

                resultado.waitFor();
                System.out.println("exit: " + resultado.exitValue());
                resultado.destroy();
                System.out.println("- FIM EXECUCAO -");
                enviarMensagemDeVolta(stringBuilder.toString(), mensagemJson.getJsonNumber(Utils.SERVIDOR).toString());
            } catch (IOException | InterruptedException e) {
                System.out.println("Erro na execução do comando");
                enviarMensagemDeVolta("Erro na execucao do comando", mensagemJson.getJsonNumber(Utils.SERVIDOR).toString());
            }

        } else if (mensagemJson.containsKey(Utils.RESPOSTA) && Peer.servidor.port == Integer.parseInt(mensagemJson.getString(Utils.SERVIDOR))) {
            System.out.println("========================= RETORNO DE [" + mensagemJson.getString(Utils.NOME_PEER) + "] =========================");
            System.out.println(mensagemJson.getString(Utils.RESPOSTA));
            System.out.println("- FIM RETORNO -");
        }
        System.out.print("> ");

    }

    private void enviarMensagemDeVolta(String resposta, String port) throws IOException {
        Peer.enviarResposta(resposta, port);
    }

}
