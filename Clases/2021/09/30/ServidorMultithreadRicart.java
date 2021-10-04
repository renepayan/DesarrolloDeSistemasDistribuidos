import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;

public class ServidorMultithreadRicart {
    static Cola<Integer> cola = new Cola<Integer>();
    static int idsRestantes;
    static boolean poseo_el_recurso;
    static int id_recurso_solicitado;
    static long tiempo_recurso_solicitado;
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static long reloj_logico;
    static Object objetoSincronizacion = new Object();
    static Object objetoSincronizacionRestantes = new Object();

    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception{
        while (longitud > 0){
          int n = f.read(b,posicion,longitud);
          posicion += n;
          longitud -= n;
        }
    }  
    static void solicitar_recurso(int idRecurso) throws Exception{
        for(int i = 0; i < num_nodos; i++){
            envia_mensaje(tiempo_recurso_solicitado, hosts[i], puertos[i], idRecurso);
        }
    }
    static void liberar_recurso(int idRecurso)throws Exception{
        while(!cola.estaVacia()){
            int nodoAEnviar = cola.desEncolar();
            envia_mensaje(reloj_logico, hosts[nodoAEnviar], puertos[nodoAEnviar], -2); //Envio el Ok
        }
    }
    static void envia_mensaje(long tiempo_logico, String host, int puerto, int id_recurso) throws Exception{
        Socket conexion;
        while(true){
            try{
                conexion = new Socket(host, puerto);
                break;
            }catch(Exception e){
                
            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        if(id_recurso == -1)
            salida.writeInt(1);
        else if(id_recurso == -2){
            //Enviar OK
            salida.writeInt(3);
            salida.write("Ok".getBytes("utf-8"));
            
        }else if(id_recurso>0){
            salida.writeInt(2);
            salida.writeInt(nodo);
            salida.writeInt(id_recurso);
        }
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
                int id_operacion;
                id_operacion = entrada.readInt();
                long tiempo_peticion;
                switch(id_operacion){
                    case 1: //Es una sincronizacion de reloj
                        tiempo_peticion = entrada.readLong();
                        if(tiempo_peticion > 0){
                            synchronized(objetoSincronizacion){
                                reloj_logico = (tiempo_peticion > reloj_logico)?tiempo_peticion+1:reloj_logico;
                            }
                        }
                        break;
                    case 2: //Es una peticion de recurso
                        int id_recurso;
                        int id_nodo_solicitante;
                        id_nodo_solicitante = entrada.readInt();
                        id_recurso = entrada.readInt();
                        tiempo_peticion = entrada.readLong();
                        if(poseo_el_recurso){
                            cola.agregar(id_nodo_solicitante); //Lo agrego a la cola
                        }else{
                            if(id_recurso_solicitado == id_recurso){
                                //Yo he solicitado este recurso
                                if(id_nodo_solicitante == nodo){ //Soy el nodo que lo solicito
                                    envia_mensaje(reloj_logico, hosts[id_nodo_solicitante], puertos[id_nodo_solicitante], -2);
                                }else{
                                    if(tiempo_peticion < tiempo_recurso_solicitado){
                                        envia_mensaje(reloj_logico, hosts[id_nodo_solicitante], puertos[id_nodo_solicitante], -2); //Envio OK
                                    }else if(tiempo_peticion == tiempo_recurso_solicitado){
                                        cola.agregar(id_nodo_solicitante);
                                    }else if(tiempo_peticion > tiempo_recurso_solicitado){
                                        if(nodo < id_nodo_solicitante){
                                            cola.agregar(id_nodo_solicitante); //Lo agrego a la cola
                                        }else{
                                            envia_mensaje(reloj_logico, hosts[id_nodo_solicitante], puertos[id_nodo_solicitante], -2); //Envio ok
                                        }
                                    }
                                }
                            }else{
                                envia_mensaje(reloj_logico, hosts[id_nodo_solicitante], puertos[id_nodo_solicitante], -2);
                            }
                        }
                        break;
                    case 3: //Es una respuesta de peticion de recurso, un Ok
                        String texto;
                        byte[] buffer = new byte[2];
                        read(entrada, buffer, 0, 2);
                        texto = new String(buffer,"utf-8");
                        tiempo_peticion = entrada.readLong();
                        if(texto.equalsIgnoreCase("Ok")){
                            synchronized(objetoSincronizacionRestantes){
                                idsRestantes -= 1;
                            }
                            if(idsRestantes == 0)
                                poseo_el_recurso = true;
                        }
                        break;
                }    
                entrada.close();
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
    static class Cola<T>{
        class Nodo<T>{
            private T objeto;
            private Nodo<T> siguiente;
            public Nodo(T objeto){
                this.objeto = objeto;
                this.siguiente = null;
            }
            public void setSiguiente(Nodo<T> siguiente){
                this.siguiente = siguiente;
            }
            public Nodo<T> getSiguiente(){
                return this.siguiente;
            }
            public T getObjeto(){
                return this.objeto;
            }
        }
        private Nodo<T> actual;
        private Nodo<T> ultimo;
        public Cola(){
            this.actual = null;
            this.ultimo = null;
        }
        public boolean estaVacia(){
            return (actual == null)?true:false;
        }
        public void agregar(T objeto){
            Nodo<T> nuevo = new Nodo<>(objeto);
            if(ultimo != null){
                ultimo.setSiguiente(nuevo);
            }
            ultimo = nuevo;
            if(actual == null)
                actual = ultimo;
        }
        public T hastaArriba(){
            return (actual == null)?null:actual.getObjeto();
        }
        public T desEncolar(){
            if(actual != null){
                Nodo<T> anterior = actual;
                actual = anterior.getSiguiente();
                return anterior.getObjeto();
            }else{
                return null;
            }
        }
    }
    public static void main(String[] args) throws Exception{
        nodo = Integer.parseInt(args[0]);
        /*if(nodo == 0)
            poseo_el_recurso = true;
        else
            poseo_el_recurso = false;*/
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
                envia_mensaje(0, hosts[i], puertos[i], -1);
        }
        Reloj reloj = new Reloj();
        reloj.start();
        servidor.join();
        Thread.sleep(1000);
        id_recurso_solicitado = 1;
        idsRestantes = num_nodos;
        synchronized(objetoSincronizacion){
            tiempo_recurso_solicitado = reloj_logico;
        }
        solicitar_recurso(id_recurso_solicitado);
        while(!poseo_el_recurso){
            Thread.sleep(1);
        }
        Thread.sleep(3000); //Acaparo el recurso
        liberar_recurso(id_recurso_solicitado);
    }
}
