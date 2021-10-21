import java.rmi.*;
public class Servidor{
    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/MultiplicaMatrices";
        ClaseRMI obj = new ClaseRMI();
        Naming.bind(url, obj);
    }
}