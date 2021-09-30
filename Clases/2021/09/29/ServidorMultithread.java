import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMultithread {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static class Worker extends Thread{
        Socket conexion;
        public Worker(Socket conexion){
            this.conexion = conexion;
        }
        @Override
        public void run(){
            System.out.println("Inicio el thread Worker");
        }
    }
    static class Servidor extends Thread{
        @Override
        public void run(){
            try{
                ServerSocket servidor = new ServerSocket(puertos[nodo]);
                while(true){
                    Socket conexion = servidor.accept();
                    Worker trabajador = new Worker(conexion);
                    trabajador.start();
                }
                //servidor.close();
            }catch(Exception e){

            }
        }
    }
    public static void main(String[] args) throws Exception{
        nodo = Integer.parseInt(args[0]);
        num_nodos = args.length -1;
        hosts = new String[num_nodos];
        puertos = new int[num_nodos];
        for(int i = 0; i < num_nodos; i++){
            hosts[i] = args[i+1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i+1].split(":")[1]);
        }
        Servidor servidor = new Servidor();
        servidor.start();
    }
}
