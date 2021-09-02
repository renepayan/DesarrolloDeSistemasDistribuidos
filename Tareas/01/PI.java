import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class PI{
    static Object obj= new Object();
    static float pi = 0;
    static class Worker extends Thread{
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }
        public void run(){
            try{
                float suma = 0.0f;
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                suma = entrada.readFloat();
                synchronized(obj){
                    pi = suma + pi;
                }
                salida.close();
                entrada.close();
                conexion.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception{
        if (args.length != 1){
            System.err.println("Uso:");
            System.err.println("java PI <nodo>");
            System.exit(0);
        }
        int nodo = Integer.valueOf(args[0]);
        if (nodo < 0 || nodo > 4){
            System.err.println("Nodo invalido");
            System.exit(0);
        }
        if (nodo == 0){
            ServerSocket servidor = null;
            servidor = new ServerSocket(20000);
            Worker[] v = new Worker[4];
            int i = 0;
            for( i = 0; i < 4; i++ ){
                Socket conexion;
                conexion = servidor.accept();
                v[i] = new Worker(conexion);
                v[i].start();
            }
            for( i = 0; i < 4; i++ ){
                v[i].join();
            }
            servidor.close();
            System.out.println("El valor de la variable PI es: "+pi);
        }else{
            System.out.println("El nodo "+nodo+" inicia su ejecucion");
            Socket conexion = null;
            for(;;){
                try{
                    conexion = new Socket("localhost",20000);
                    break;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            float suma = 0.0f;
            int i = 0;
            for(i = 0; i < 1000000; i++){
                suma = (4.0f / (8.0f*(float)i+2.0f*(nodo-2)+3)) + suma;
            }
            suma = nodo%2==0?-suma:suma;
            salida.writeFloat(suma);
            salida.close();
            entrada.close();
            conexion.close();
            System.out.println("El nodo "+nodo+" termina su ejecucion");
        }
    }
}