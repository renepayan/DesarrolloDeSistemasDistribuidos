import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Servidor{
    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception{
        while (longitud > 0){
          int n = f.read(b,posicion,longitud);
          posicion += n;
          longitud -= n;
        }
    }  
    public static void main(String[] args) throws Exception{
        ServerSocket servidor = new ServerSocket(50000);
        Socket cliente = servidor.accept();
        DataInputStream entrada = new DataInputStream(cliente.getInputStream());
        DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
        long suma = 0;
        byte[] a = new byte[501*4];
        read(entrada,a,0,501*4);
        ByteBuffer b = ByteBuffer.wrap(a);
        for(int i = 0; i<501;i++)
            suma+=(long)b.getInt();
        salida.writeLong(suma);
        salida.close();
        entrada.close();
        cliente.close();
        servidor.close();
    }
}