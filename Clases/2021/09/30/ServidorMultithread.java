import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;

public class ServidorMultithread {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static long reloj_logico;
    static Object objetoSincronizacion = new Object();
    static void envia_mensaje(long tiempo_logico, String host, int puerto) throws Exception{
        Socket conexion;
        while(true){
            try{
                conexion = new Socket(host, puerto);
                break;
            }catch(Exception e){
                
            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeLong(tiempo_logico);
        salida.close();
        conexion.close();
    }
    static class Reloj extends Thread{
        @Override
        public void run(){
            try{
                while(true){
                    synchronized(objetoSincronizacion){
                        System.out.println("Reloj logico: "+reloj_logico);
                        reloj_logico+=(nodo == 0)?4:(nodo == 1)?5:(nodo == 2)?6:0;
                    }
                    Thread.sleep(1000);
                }
            }catch(Exception e){

            }
        }
    }
    static class Worker extends Thread{
        Socket conexion;
        public Worker(Socket conexion){
            this.conexion = conexion;
        }
        @Override
        public void run(){
            System.out.println("Inicio el thread Worker");
            try{
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                while(true){
                    long tiempo_recibido;
                    tiempo_recibido = entrada.readLong();
                    if(tiempo_recibido > 0){
                        synchronized(objetoSincronizacion){
                            reloj_logico = (tiempo_recibido > reloj_logico)?tiempo_recibido+1:reloj_logico;
                        }
                    }
                }
                
                //entrada.close();
            }catch(Exception e){

            }
            
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
        for(int i = 0; i < num_nodos; i++){
            if(i != nodo)
                envia_mensaje(0, hosts[i], puertos[i]);
        }
        Reloj reloj = new Reloj();
        reloj.start();
        servidor.join();
    }
}
