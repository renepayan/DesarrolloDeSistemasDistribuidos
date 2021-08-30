import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL {
    public static void main(String[] args) throws Exception{
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory.createServerSocket(50000);
        Socket conexion = socket_servidor.accept();
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        System.out.print(entrada.readDouble());
        salida.close();
        entrada.close();
        conexion.close();
        socket_servidor.close();
    }
}
