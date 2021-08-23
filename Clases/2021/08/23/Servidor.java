import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.sound.midi.SysexMessage;

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
        Socket conexion = servidor.accept();
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        int n = entrada.readInt();
        System.out.println(n);
        double x = entrada.readDouble();
        System.out.println(x);
        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));
        salida.write("HOLA".getBytes());
        byte[] a = new byte[5*8];
        read(entrada,a,0,5*8);
        ByteBuffer b = ByteBuffer.wrap(a);
        for(int i = 0; i<5;i++)
            System.out.println(b.getDouble());

        //Aqui modifico para que reciba 1000 numeros punto flotante con la funcion writeDouble
        long horaInicio = System.currentTimeMillis();
        for(double i = 1.0d; i<= 1000.0d; i++){
            entrada.readDouble();
        }
        long horaTermino = System.currentTimeMillis();
        System.out.println("Tardo  "+(horaTermino-horaInicio)+"ms en recibir con el metodo readDouble");

        //Aqui modifico para que reciba 1000 numeros double con la compresion de WriteBuffer
        //Recepcion
        horaInicio = System.currentTimeMillis();
        byte[] a1 = new byte[1000*8];
        read(entrada,a1,0,1000*8);
        horaTermino = System.currentTimeMillis();
        System.out.println("Tardo en recibir "+(horaTermino-horaInicio)+"ms con byteBuffer");

        //Descompresion
        horaInicio = System.currentTimeMillis();
        ByteBuffer b1 = ByteBuffer.wrap(a1);
        horaTermino = System.currentTimeMillis();
        System.out.println("Tardo en descomprimir "+(horaTermino-horaInicio)+"ms");
        
        salida.close();
        entrada.close();
        conexion.close();
        servidor.close();
    }
}