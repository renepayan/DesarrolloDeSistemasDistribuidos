import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class NTP {
    public static void main(String[] args) throws Exception{
        Socket conexion = new Socket("sisdis.sytes.net", 50002);
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        long t1 = 1632746968;
        long t2 = entrada.readLong();
        long t3 = entrada.readLong();
        long t4 = t3+3;
        long offset = ((t2 - t1) + (t3 - t4)) / 2;
        long delay = (t4 - t1) - (t3 - t2);
        long resultado = t4+offset;
        System.out.println("La nueva hora es: "+resultado);
        salida.close();
        entrada.close();
        conexion.close();
    }
}