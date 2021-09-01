import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.FileOutputStream;
import javax.net.ssl.SSLServerSocketFactory;
public class ServidorArchivo {
    static class Worker extends Thread{
        Socket conexion;
        void escribe_archivo(String archivo,byte[] buffer) throws Exception{
            FileOutputStream f = new FileOutputStream(archivo);
            try{ 
                f.write(buffer);
            }finally{
                f.close();
            }
        }
        void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception{
            while (longitud > 0){
              int n = f.read(b,posicion,longitud);
              posicion += n;
              longitud -= n;
            }
        }
        @Override
        public void run(){
            try{
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                //Primero leo el nombre del archivo
                String nombre = entrada.readUTF();
                System.out.println("Leyendo el archivo: "+nombre);
                //Ahora leo el tamaño del archivo
                int longitudEnBytes = entrada.readInt();
                System.out.println("De longitud: "+longitudEnBytes+" en bytes");
                //Ahora leo el archivo
                byte[] buffer = new byte[longitudEnBytes];
                this.read(entrada, buffer, 0, longitudEnBytes);
                this.escribe_archivo(nombre, buffer);
                //Cierro todas las conexiones
                salida.close();
                entrada.close();
                this.conexion.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
        public Worker(Socket conexion){
            this.conexion = conexion;
        }
    }
    
    public static void main(String[] args) throws Exception{
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory.createServerSocket(50000);
        //ServerSocket socket_servidor = new ServerSocket(50000);
        for(;;){
            Socket conexion = socket_servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }
        //socket_servidor.close();
    }
}
