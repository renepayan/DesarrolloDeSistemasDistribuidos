import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Chat {
    
    static void envia_mensaje_multicast(byte[] buffer,String ip,int puerto) throws IOException{
      DatagramSocket socket = new DatagramSocket();
      socket.send(new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),puerto));
      socket.close();
    }
    static byte[] recibe_mensaje_multicast(MulticastSocket socket,int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
    static class Worker extends Thread{
        public void run(){
            // En un ciclo infinito se recibirán los mensajes enviados al
            // grupo 230.0.0.0 a través del puerto 20000 y se desplegarán en la pantalla.
            try{
                MulticastSocket socket = new MulticastSocket(20000);
                InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"),20000);
                NetworkInterface netInter = NetworkInterface.getByName("en0");
                socket.joinGroup(grupo, netInter);
                while(true){
                    byte[] arreglo = recibe_mensaje_multicast(socket, 128);
                    String decodificado = new String(arreglo, "cp437");
                    String[] partes = decodificado.split(":");
                    String emisor = partes[0];
                    String mensaje = partes[1];
                    System.out.println('\n'+emisor+">"+mensaje);

                }
            }catch(Exception e){

            }
        }
    }
    public static void main(String[] args) throws Exception{
        new Worker().start();
        String nombre = args[0];
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el mensaje al
        // grupo 230.0.0.0 a través del puerto 20000.
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp437"));
        while(true){
            System.out.print("Ingrese el mensaje a enviar: ");
            String mensaje = reader.readLine();
            String paquete =  nombre+":"+mensaje;
            envia_mensaje_multicast(paquete.getBytes("cp437"), "230.0.0.0", 20000);
        }
    }
}
