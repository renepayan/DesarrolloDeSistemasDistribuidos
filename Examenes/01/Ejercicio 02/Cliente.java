import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
    public static void main(String[] args) throws Exception{
        Socket conexion = new Socket("localhost", 50000);
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        ByteBuffer b = ByteBuffer.allocate(501*4);
        for(int i = 0; i <= 1000; i++){
            if(i % 2 == 0)
                b.putInt(i);
        }
        byte[] a = b.array();
        salida.write(a);
        long resultado = entrada.readLong();
        entrada.read()
        System.out.println("La suma es: "+resultado);
        salida.close();
        entrada.close();
        conexion.close();
    }    
}
