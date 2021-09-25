import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;

public class ClienteMulticast {
    static byte[] recibe_mensaje(MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, longitud_mensaje);
        socket.receive(paquete);
        return paquete.getData();
    }
    public static void main(String[] args){
        MulticastSocket socket = new MulticastSocket(50000);
        InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"),50000);
        NetworkInterface netInter = NetworkInterface.getByName("en0");

        socket.joinGroup(grupo, netInter);
        byte[] a  = recibe_mensaje(socket, 4);
        System.out.print(new String(a,"UTF-8"));
        byte[] buffer  = recibe_mensaje(socket, 5*8);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        for(int i = 0; i < 5;i++){
            System.out.println(b.getDouble());
        }
        socket.leaveGroup(grupo, netInter);
        socket.close();
    }
}
