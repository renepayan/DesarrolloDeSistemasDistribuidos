import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
    public static void main(String[] args) throws Exception{
        Socket conexion = new Socket("localhost", 50000);
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        ByteBuffer b = ByteBuffer.allocate(500*4);
        for(int i = 1; i <= 500; i++){
            b.putInt(i);
        }
        byte[] a = b.array();
        salida.write(a);
        long resultado = entrada.readLong();
        System.out.println("La suma es: "+resultado);
        salida.close();
        entrada.close();
        conexion.close();
    }    
}
