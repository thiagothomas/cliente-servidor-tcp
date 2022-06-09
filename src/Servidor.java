import com.sun.nio.sctp.SctpServerChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class Servidor extends Thread {

    public final ServerSocket server;
    public final Integer port;
    public final Set<Conexao> conexoes = new HashSet<>();

    Servidor(Integer port) throws IOException {
        this.port = port;
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Conexao conexao = new Conexao(this.server.accept(), this);
                this.conexoes.add(conexao);
                conexao.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarComando(String comando) {
        try {
            conexoes.forEach(c -> c.printWriter.println(comando));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
