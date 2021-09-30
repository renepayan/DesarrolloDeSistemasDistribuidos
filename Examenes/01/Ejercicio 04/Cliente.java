import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class Cliente {
    public static void main(String[] args) throws Exception{
        Socket conexion = new Socket("sisdis.sytes.net", 20020);
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeDouble(66.0f);
        salida.writeDouble(52.0f);
        salida.writeDouble(32.0f);
        salida.writeInt(27);
        double respuesta = entrada.readDouble();
        System.out.println("Respuesta: "+respuesta);
        salida.close();
        entrada.close();
        conexion.close();
    }
}