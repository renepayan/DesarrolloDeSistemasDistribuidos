import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI{
    public ClaseRMI() throws RemoteException{
        super();
    }
    @Override
    public double[][] multiplica_matrices(double[][] A, double[][] B, int N) throws RemoteException{
        double[][] C = new double[N/3][N/3];
        for(int i = 0; i < N/3; i++){
            for(int j = 0; j < N/3; j++){
                for(int k = 0; k < N; k++){
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        return C;
    }
}
