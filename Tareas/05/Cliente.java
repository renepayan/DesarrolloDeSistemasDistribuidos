import java.rmi.Naming;

public class Cliente {
    static int N;
    static final int LIMITE = 3000;
    static final int num_nodos = 3;
    static double[][] separa_matriz(double[][] A, int renglon_inicial){
        double[][] M = new double[N/3][N];
        for(int i = 0; i < N/3; i++){
            for(int j = 0; j < N; j++){
                M[i][j] = A[i+renglon_inicial][j];
            }
        }
        return M;
    }
    static void acomoda_matriz(double[][] C, double[][] c, int renglon, int columna){
        for(int i = 0; i < N/3; i++){
            for(int j = 0; j < N/3; j++){
                C[i + renglon][j + columna] = c[i][j];
            }
        }
    }
    static double calcula_checksum(double[][] matriz){
        double suma = 0.0;
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                suma+=matriz[i][j];
            }
        }
        return suma;
    }
    static class Recurso{
        private String url;
        InterfaceRMI interfaz;
        public Recurso(String host, int puerto){
            url = "rmi://"+host+":"+puerto+"/MultiplicaMatrices";
        }
        public boolean encontrarRecursos(){
            try{
                this.interfaz = (InterfaceRMI)Naming.lookup(this.url);
                return true;
            }catch(Exception e){
                return false;
            }
        }
        public void eliminarRecurso(){
            this.interfaz = null;
        }
        public double[][] multiplicar(double[][] matriz1, double[][] matriz2){
            try{
                return this.interfaz.multiplica_matrices(matriz1, matriz2, N);
            }catch(Exception e){
		e.printStackTrace();
                return null;   
            }
        }
    }
    static class Worker extends Thread{
        private double[][] resultado, matriz1, matriz2;
        private Recurso recurso;
        public Worker(Recurso recurso, double[][] matriz1, double[][] matriz2){
            this.recurso = recurso;
            this.matriz1 = matriz1;
            this.matriz2 = matriz2;
        }
        public double[][] getResultado(){
            return this.resultado;
        }
        @Override
        public void run(){
            try{
                this.resultado = this.recurso.multiplicar(matriz1, matriz2);
            }catch(Exception e){

            }
        }
    }
    static void imprime_matriz(double[][] matriz){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N;j++){
                System.out.print(matriz[i][j]+" ");
            }
            System.out.print("\n");
        }
    }
    public static void main(String[] args) throws Exception{
        Recurso[] recursos = new Recurso[num_nodos];
        if(args.length != num_nodos){
            System.out.println("Uso: java Cliente Servidor:Puerto Servidor:Puerto Servidor:Puerto");
            return;
        }
        for(int i = 0; i < num_nodos; i++){
            String host = args[i].split(":")[0];
            int puerto = Integer.parseInt(args[i].split(":")[1]);
            recursos[i] = new Recurso(host, puerto);
            recursos[i].encontrarRecursos();
        }
        for(N = 3; N <= LIMITE; N+=3){
	    System.out.println(N);
            double[][] A = new double[N][N];
            double[][] B = new double[N][N];
            double[][] C = new double[N][N];
            for(int i = 0; i<N; i++){
                for(int j = 0; j<N; j++){
                    A[i][j] = 4 * i + j;
                    B[i][j] = 4 * i - j;
                }
            }
            if(N == 9){
                System.out.println("Matriz A:");
                imprime_matriz(A);
                System.out.println("Matriz B:");
                imprime_matriz(A);
                System.out.println("Matriz C:");
            }
            for (int i = 0; i < N; i++){
                for (int j = 0; j < i; j++){
                    double x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }
            }
            double[][] A1 = separa_matriz(A, 0);
            double[][] A2 = separa_matriz(A, N/3);
            double[][] A3 = separa_matriz(A, 2*(N/3));
            double[][] B1 = separa_matriz(B, 0);
            double[][] B2 = separa_matriz(B, N/3);
            double[][] B3 = separa_matriz(B, 2*(N/3));
            Worker[] workers = {
                new Worker(recursos[0], A1,B1),
                new Worker(recursos[0], A1,B2),
                new Worker(recursos[0], A1,B3),
                new Worker(recursos[1], A2,B1),
                new Worker(recursos[1], A2,B2),
                new Worker(recursos[1], A2,B3),
                new Worker(recursos[2], A3,B1),
                new Worker(recursos[2], A3,B2),
                new Worker(recursos[2], A3,B3)
            };
            for(int i = 0; i < num_nodos*3;i++){
                workers[i].start();
            }
            for(int i = 0; i < num_nodos;i++){
                for(int j = 0; j < num_nodos; j++){
                    workers[(num_nodos*i)+j].join();
                    acomoda_matriz(C, workers[(num_nodos*i)+j].getResultado(), i*(N/3), j*(N/3));
                }
            }
            if(N == 9 || N == 3000){
                if(N == 9)
                    imprime_matriz(C);
                double suma = calcula_checksum(C);
                System.out.println("Checksum: "+suma);
            }
        }
    }
}
