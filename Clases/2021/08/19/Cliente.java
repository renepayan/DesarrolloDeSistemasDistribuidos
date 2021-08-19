import java.net.Socket;

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
        
        salida.close();
        entrada.close();
        conexion.close();
    }
}