import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.FileInputStream;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import javax.swing.JFileChooser;
public class ClienteArchivo {
    static byte[] lee_archivo(String archivo) throws Exception{
        FileInputStream f = new FileInputStream(archivo);
        byte[] buffer;
        try{
            buffer = new byte[f.available()];
            f.read(buffer);
        }
        finally{
            f.close();
        }
        return buffer;
    }
    public static void main(String[] args) throws Exception{
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = null;
        for(;;){
            try{
                conexion = cliente.createSocket("localhost", 50000);
                //conexion = new Socket("localhost",50000);
                break;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        DataOutputStream salida = new DataOutputStream (conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        //Aqui abro un file chooser para buscar el archivo
            for(;;){
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("Enviando el archivo: "+chooser.getSelectedFile().getAbsolutePath());
                System.out.println("De nombre: "+chooser.getSelectedFile().getName());
                salida.writeUTF(chooser.getSelectedFile().getName());
                byte[] buffer = lee_archivo(chooser.getSelectedFile().getAbsolutePath());
                salida.writeInt(buffer.length);
                System.out.println("De tamaño: "+buffer.length+" en bytes");
                salida.write(buffer);
                break;
            }
        }
        salida.close();
        entrada.close();
        conexion.close();
    }
}
