import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;

public class Consistencia{
    private static final int NUM_NODOS = 4;
    private static final int NUM_DATOS = 16;
    private static int numeroDeNodo = 0;
    private static int datosModificados = 0;
    private static int[] puertos = new int[NUM_NODOS];
    private static String[] ips = new String[NUM_NODOS];
    private static long[] datos = new long[NUM_DATOS];
    private static boolean[] banderas = new boolean[NUM_DATOS];
    private static boolean poseoElRecurso = false;
    private static long tiempoEnQuePediElRecurso = 1;
    private static int numOks = 0;
    static long reloj_logico;
    static Object objetoSincronizacion = new Object();
    static Object objActualizar = new Object();
    static Object objetoOks = new Object();
    static Object objXD = new Object();
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
    static Cola<Integer> cola = new Cola<Integer>();
    private static long read(int posicion){
        return datos[posicion];
    } 
    private static void write(int posicion, long valor){
        synchronized(objActualizar){
            datos[posicion] = valor;
            banderas[posicion] = true;
        }        
    }
    private static void solicitarBloqueo() throws Exception{        
        long tiempoSolicitud = tiempoEnQuePediElRecurso = reloj_logico;
        System.out.println("Pido en el tiempo "+tiempoSolicitud);
        for(int i = 0; i < NUM_NODOS; i++){
            enviarMensaje(i, 2, tiempoSolicitud, 0, 0L);
            Thread.sleep(100);
        }
    }
    private static void lock(){
        numOks = 0;
        for(int i = 0; i < NUM_DATOS; i++){
            banderas[i] = false;
        }
        if(!poseoElRecurso){
            try{                
                solicitarBloqueo();
                while(!poseoElRecurso){
                    System.out.println("Tengo "+numOks+" oks");
                    Thread.sleep(1000);
                }
            }catch(Exception e){

            }
        }
    }
    private static void unlock() throws Exception{
        for(int i = 0; i < NUM_DATOS; i++){
            if(banderas[i]){
                for(int j = 0; j < NUM_NODOS; j++){
                    if(j != numeroDeNodo){
                        enviarMensaje(j, 4, 0L, i, datos[i]);
                    }
                }
            }
        }          
        while(!cola.estaVacia()){ //Desencolo
            int nodo = cola.desEncolar();            
            enviarOk(nodo);
        }
        tiempoEnQuePediElRecurso = 0L;
        poseoElRecurso = false;              
    }
    static class Reloj extends Thread{
        @Override
        public void run(){
            try{
                while(true){
                    synchronized(objetoSincronizacion){
                        //System.out.println("Reloj logico: "+reloj_logico);
                        reloj_logico+=1;
                    }
                    Thread.sleep(1000);
                }
            }catch(Exception e){

            }
        }
    }
    static void enviarMensaje(int nodo, int accion, long tiempo, int posicion, long valor) throws Exception{
        Socket conexion;
        while(true){
            try{
                conexion = new Socket(ips[nodo], puertos[nodo]);
                break;
            }catch(Exception e){

            }
        }
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeInt(accion);
        switch(accion){
            case 1:
                salida.writeLong(tiempo);
                break;
            case 2:
                salida.writeInt(numeroDeNodo);
                salida.writeLong(tiempo);
                break;
            case 3:
                break;
            case 4:
                salida.writeInt(posicion);
                salida.writeLong(valor);
                break;
        }
        salida.close();
        conexion.close();
    }
    private static void enviarOk(int numNodoAEnviar) throws Exception{
        //System.out.println("Le envio ok al nodo "+numNodoAEnviar);
        enviarMensaje(numNodoAEnviar, 3,0L,0,0);
    }
    private static void ponerEnCola(int nodoAPoner){
        cola.agregar(nodoAPoner);
    }
    static class Worker extends Thread{
        Socket conexion;
        public Worker(Socket conexion){
            this.conexion = conexion;
        }
        @Override
        public void run(){
            //System.out.println("Inicio el thread Worker");
            try{
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());                
                int id_operacion;
                id_operacion = entrada.readInt();
                long tiempo_peticion = 0;                
                switch(id_operacion){
                    case 1: //Es una sincronizacion de reloj
                        tiempo_peticion = entrada.readLong();
                        if(tiempo_peticion > 0){
                            synchronized(objetoSincronizacion){
                                reloj_logico = (tiempo_peticion > reloj_logico)?tiempo_peticion+1:reloj_logico;
                            }
                        }
                        break;
                    case 2: //Alguien solicito un lock
                        synchronized(objXD){
                            int numeroDeNodoSolicitante = entrada.readInt();                        
                            long tiempoRecibido = entrada.readLong();                        
                            System.out.println("Tengo una solicitud "+numeroDeNodoSolicitante+" "+tiempoRecibido);
                            if(numeroDeNodoSolicitante == numeroDeNodo){
                                //System.out.println("le envio ok al "+numeroDeNodoSolicitante+" porque soy yo");
                                enviarOk(numeroDeNodoSolicitante);                    
                            }else{
                                if(poseoElRecurso){
                                    ponerEnCola(numeroDeNodoSolicitante);
                                }else{
                                    if(tiempoEnQuePediElRecurso == 0L){ //Significa que no quiero el recurso                                
                                        //System.out.println("le envio ok al "+numeroDeNodoSolicitante+" porque no quiero el recurso");
                                        enviarOk(numeroDeNodoSolicitante);
                                    }else{ //Significa que si quiero el recurso
                                        if(tiempoRecibido < tiempoEnQuePediElRecurso){
                                            //System.out.println("le envio ok al "+numeroDeNodoSolicitante+" porque tiene tiempo menor");
                                            enviarOk(numeroDeNodoSolicitante);
                                        }else if(tiempoEnQuePediElRecurso == tiempoRecibido){
                                            if(numeroDeNodoSolicitante < numeroDeNodo){
                                                //System.out.println("le envio ok al "+numeroDeNodoSolicitante+" porque tiene el mismo tiempo pero es menor");
                                                enviarOk(numeroDeNodoSolicitante);                                            
                                            }else{
                                                ponerEnCola(numeroDeNodoSolicitante);
                                            }
                                        }else if(tiempoRecibido > tiempoEnQuePediElRecurso){
                                            ponerEnCola(numeroDeNodoSolicitante);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 3: //Alguien envio un ok
                        synchronized(objetoOks){
                            numOks += 1;
                            if(numOks == NUM_NODOS){
                                poseoElRecurso = true;
                            }
                        }
                        break;
                 
                    case 4: //Me mandaron una actualizacion de informacion                                                
                        int posicionAActualizar;
                        long valorAPoner;
                        posicionAActualizar = entrada.readInt();
                        valorAPoner = entrada.readLong();
                        System.out.println("me ordenan actualizar "+posicionAActualizar+" "+valorAPoner);
                        write(posicionAActualizar, valorAPoner);
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
                ServerSocket servidor = new ServerSocket(puertos[numeroDeNodo]);
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
    public static void main(String[] args){
        if(args.length != 1+NUM_NODOS){
            System.out.println("El comando se ejecuta como: java Consistencia num_nodo nodo1:p1 nodo2:p2 nodo3:p3 nodo4:p4");
        }else{
            numeroDeNodo = Integer.parseInt(args[0]);
            Reloj reloj = new Reloj();
            reloj.start();
            if(numeroDeNodo == 0)
                poseoElRecurso = true;
            for(int i = 0; i < NUM_NODOS; i++){
                ips[i] = args[i+1].split(":")[0];
                puertos[i] = Integer.parseInt(args[i+1].split(":")[1]);
            }
            Servidor servidor = new Servidor();
            servidor.start();
            for(int i = 0; i < NUM_NODOS; i++){
                try{
                    enviarMensaje(i, 1, reloj_logico, 0, 0L);
                }catch(Exception e){

                }
            }
            System.out.println("Todos los nodos iniciados");
            for(int i = 0; i < 100; i++){
                try{
                    System.out.println("Hago lock");
                    lock();
                    System.out.println("termino lock");                    
                    long r=read(0);
                    System.out.println("El valor actual es "+r);
                    r++;
                    System.out.println("El valor actual es "+r);
                    System.out.println("lo escribo");
                    write(0,r);
                    System.out.println("Hago unlock");
                    unlock();
                    System.out.println("termino unlock");                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //tiempoEnQuePediElRecurso = 0;
            //poseoElRecurso = false;  
            if(numeroDeNodo == 0){
                try{
                    lock();
                    System.out.println("Termine: "+read(0));
                    unlock();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}