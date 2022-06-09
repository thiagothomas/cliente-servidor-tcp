import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Conexao extends Thread {

    private final Socket socket;
    private final Servidor servidor;
    public PrintWriter printWriter;

    Conexao(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Utils.criarBufferedReader(socket.getInputStream());
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                servidor.enviarComando(reader.readLine());
            }
        } catch (Exception e) {
            servidor.conexoes.remove(this);
        }
    }

}
