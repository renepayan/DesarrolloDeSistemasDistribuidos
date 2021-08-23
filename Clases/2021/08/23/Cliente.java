import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.*;

class Cliente{
    public static void main(String[] args) throws Exception{
        Socket conexion = new Socket("localhost", 50000);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        salida.writeInt(123);
        salida.writeDouble(1234567890.1234567890);
        salida.write("hola".getBytes());

        byte[] buffer = new byte[4];
        entrada.read(buffer,0,4);
        System.out.println(new String(buffer,"UTF-8"));
        ByteBuffer b = ByteBuffer.allocate(5*8);
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        byte[] a = b.array();
        salida.write(a);
        //Aqui modifico para que envie 1000 numeros punto flotante con la funcion writeDouble
        long horaInicio = System.currentTimeMillis();
        for(double i = 1.0d; i<= 1000.0d; i++){
            salida.writeDouble(i);
        }
        long horaTermino = System.currentTimeMillis();
        System.out.println("Tardo "+(horaTermino-horaInicio)+"ms en enviar con el metodo writeDouble");

        //Aqui modifico para que envie 1000 numeros double con la compresion de WriteBuffer
        horaInicio = System.currentTimeMillis();
        ByteBuffer b1 = ByteBuffer.allocate(1000*8);
        for(double i = 1.0d; i<= 1000.0d; i++){
            b1.putDouble(i);
        }
        byte[] a1 = b1.array();
        horaTermino = System.currentTimeMillis();
        System.out.println("Tardo en comprimir "+(horaTermino-horaInicio)+"ms");
        horaInicio = System.currentTimeMillis();
        salida.write(a1);
        horaTermino = System.currentTimeMillis();
        System.out.println("Tardo "+(horaTermino-horaInicio)+"ms en enviar con byteBuffer");
        salida.close();
        entrada.close();
        conexion.close();
    }
}