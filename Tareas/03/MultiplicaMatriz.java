import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MultiplicaMatriz {
    static int LIMITE = 1500;
    static int N;
    static long A[][];
    static long B[][];
    static long BOrig[][];
    static long C[][];
    static Object bloqueo = new Object();
    static void recibeMatriz(long[][] matriz, DataInputStream entrada, int filaInicio, int columnaInicio, int filaFin, int ColumnaFin) throws Exception{
        int longitud = (filaFin-filaInicio)*(ColumnaFin-columnaInicio)*8;
        int posicion = 0;
        byte[] buffer = new byte[longitud];
        while (longitud > 0){
            int n = entrada.read(buffer,posicion,longitud);
            posicion += n;
            longitud -= n;
        }
        ByteBuffer x = ByteBuffer.wrap(buffer);
        for(int i = filaInicio; i < filaFin; i++){
            for(int j = columnaInicio; j < ColumnaFin; j++){
                matriz[i][j] = x.getLong();
            }
        }
    }
    static void enviarMatriz(long[][] matriz, DataOutputStream salida, int filaInicio, int filaFin, int columnaInicio, int columnaFin) throws Exception{
        int longitud = (filaFin-filaInicio)*(columnaFin-columnaInicio)*8;
        ByteBuffer b = ByteBuffer.allocate(longitud);
        for(int i = filaInicio; i < filaFin;i++){
            for(int j = columnaInicio; j < columnaFin; j++){
                b.putLong(matriz[i][j]);
            }
        }
        salida.write(b.array());
    }
    static class Worker extends Thread{
        int nodo;
        int estado; //0 espera
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
            this.estado = 0;
        }
        @Override
        public void run(){
            //Creo los stream de entrada y salida
            try{
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                this.nodo = entrada.readInt();
                while( N <= LIMITE ){
                    //Mientras no este en ready espero
                    while(estado != 1){
                        sleep(1);
                    }
                    switch(nodo){
                        case 1:
                            enviarMatriz(A, salida, 0, N/2, 0, N); //A1
                            enviarMatriz(B, salida, 0, N/2, 0, N); //B1
                            recibeMatriz(C, entrada, 0, 0, N/2, N/2); //C1
                            break;
                        case 2:
                            enviarMatriz(A, salida, 0, N/2, 0, N); //A1
                            enviarMatriz(B, salida, N/2, N, 0, N); //B2
                            recibeMatriz(C, entrada, 0, N/2, N/2, N); //C2
                            break;
                        case 3:
                            enviarMatriz(A, salida, N/2, N, 0, N); //A2
                            enviarMatriz(B, salida, 0, N/2, 0, N); //B1
                            recibeMatriz(C, entrada, N/2, 0, N, N/2); //C3
                            break;
                        case 4:
                            enviarMatriz(A, salida, N/2, N, 0, N); //A2
                            enviarMatriz(B, salida, N/2, N, 0, N); //B2
                            recibeMatriz(C, entrada, N/2, N/2, N, N); //C4
                            break;
                    }
                    //Indico que este hilo ya termino su ciclo
                    this.estado = 2;
                    if(N == LIMITE)
                        break;
                }
                //Cierro los stream y la conexion
                salida.close();
                entrada.close();
                conexion.close();
            }catch(Exception e){
            }
        }
        int getEstado(){
            return this.estado;
        }
        void setEstado(int estado){
            this.estado = estado;
        }
    }
    static long obtenChecksum(long[][] matriz){
        long checksum = 0;
        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz[i].length; j++){
                checksum+=matriz[i][j];
            }
        }
        return checksum;
    }
    static void imprimeMatriz(long[][] matriz){
        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz[i].length; j++){
                System.out.print(matriz[i][j]+" ");
            }
            System.out.print('\n');
        }
    }
    public static void main(String[] args) throws Exception{
        N = 2;
        int nodo = 0;
        String ipServidor = "";
        if(args.length != 2){
            //Valido que el numero de agumentos coincida
            System.out.println("Uso: java MultiplicaMatriz <ipServidor> <nodo>");
            return;
        }else{
            //Guardo los parametros
            ipServidor = args[0];
            try{
                nodo = Integer.parseInt(args[1]);
                if(nodo < 0 || nodo > 4){
                    //Valido que el numero de nodo este en el rango correcto
                    Integer.parseInt("error");
                }
            }catch(NumberFormatException e){
                //Imprimo la excepcion
                System.out.println("El numero de nodo es invalido");
                return;
            }
        }
        if(nodo == 0){
            //Esto es en caso de que sea el servidor
            ServerSocket servidor = new ServerSocket(20000);
            Worker[] workers = new Worker[4];
            //Estas seran las conexiones hijas
            for(int i = 0; i < 4;i++){
                workers[i] = new Worker(servidor.accept());
                workers[i].start();
            }
            while( N <= LIMITE ){
                //Creo las matrices iniciales
                A = new long[N][N];
                B = new long[N][N];
                BOrig = new long[N][N];
                C = new long[N][N];
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < N; j++){
                        A[i][j] = 2*i + j;
                        BOrig[i][j] = 2*i - j;
                        //Aqui transpongo la matriz B
                        B[j][i] = BOrig[i][j];
                    }
                }
                //Pongo a todos los hilos en ready
                for(int i = 0; i < 4;i++){
                    workers[i].setEstado(1);
                }
                //Me espero a que todos los hilos hayan terminado
                while(true){
                    int cuantos = 0;
                    for(int i = 0;i<4;i++){
                        if(workers[i].getEstado() == 2){
                            cuantos+=1;
                        }
                    }
                    if(cuantos == 4){
                        break;
                    }else{
                        Thread.sleep(1);
                    }
                }
                //Para el caso de N = 10
                if(N == 10){
                    System.out.println("N = "+N);
                    System.out.println("Matriz A: ");
                    imprimeMatriz(A);
                    System.out.println("Matriz B original: ");
                    imprimeMatriz(BOrig);
                    System.out.println("Matriz B transpuesta: ");
                    imprimeMatriz(B);
                    System.out.println("Matriz C: ");
                    imprimeMatriz(C);
                    System.out.println("Checksum de la matriz C: "+obtenChecksum(C));
                    System.out.println("-----------------------------------------");
                }
                //Para el caso de N = 1500
                if(N == LIMITE){
                    System.out.println("N = "+N);
                    System.out.println("Checksum de la matriz C: "+obtenChecksum(C));
                    System.out.println("-----------------------------------------");
                }
                //Aumento en 2 N
                N += 2;
            }
            for(int i = 0; i < 4; i++){
                workers[i].join();
            }
            servidor.close();
        }else{
            //Esto es en caso de que sea un nodo esclavo
            Socket conexion;
            while(true){
                try{
                    conexion = new Socket(ipServidor, 20000);
                    break;
                }catch(Exception e){
                }
            } 
            //Ahora creo los streams de entrada y salida
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            //Envio el numero de nodo
            salida.writeInt(nodo);
            while(N <= LIMITE){
                //Creo las matrices locales acorde al tamaño
                A = new long[N/2][N];
                B = new long[N/2][N];
                C = new long[N/2][N/2];
                //Recibo las matrices del servidor
                recibeMatriz(A, entrada, 0, 0, N/2, N);
                recibeMatriz(B, entrada, 0, 0, N/2, N);
                //Realizo la multiplicacion
                for (int i = 0; i < N/2; i++)
                    for (int j = 0; j < N/2; j++)
                        for (int k = 0; k < N; k++)
                            C[i][j] += A[i][k] * B[j][k];
                //Ahora envio la matriz al servidor
                enviarMatriz(C, salida, 0, N/2, 0, N/2);
                N+=2;
            }
            //Cierro la conexion y los streams
            salida.close();
            entrada.close();
            conexion.close();
        }
    }   
}
