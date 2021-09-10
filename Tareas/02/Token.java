import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

class Token{
  static DataInputStream entrada;
  static DataOutputStream salida;
  static boolean inicio = true;
  static String ip;
  static int nodo;
  static long token;
  static SSLContext obtenerContextoDesdeAlmacen(String almacen, String password) throws Exception{
    SSLContext contexto = null;
    KeyStore ks = KeyStore.getInstance("PKCS12");
    InputStream ISAlmacen = new FileInputStream(almacen);
    ks.load(ISAlmacen, password.toCharArray());
    ISAlmacen.close();
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, password.toCharArray());
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);
    contexto = SSLContext.getInstance("TLS");
    contexto.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
    ISAlmacen.close();
    return contexto;
  }
  static class Worker extends Thread{
    public void run(){
       //Algoritmo 1
       try{
        SSLContext contexto = obtenerContextoDesdeAlmacen("keystore_servidor.jks", "69696969");
        SSLServerSocketFactory socket_factory = contexto.getServerSocketFactory();
        ServerSocket servidor = socket_factory.createServerSocket(20000+nodo);
        Socket conexion;
        conexion = servidor.accept();
        entrada = new DataInputStream(conexion.getInputStream());
       }catch(Exception e){
         e.printStackTrace();
       }
    }
  }

  public static void main(String[] args) throws Exception{
    if (args.length != 2){
      System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
      System.exit(1);
    }
    nodo = Integer.valueOf(args[0]);
    ip = args[1];
    //Algoritmo 2
    SSLContext contexto = obtenerContextoDesdeAlmacen("keystore_cliente.jks", "420420420420");
    SSLSocketFactory cliente = contexto.getSocketFactory();
    Worker w;
    w = new Worker();
    w.start();
    Socket conexion = null;
    while(true){
      try{
        conexion = cliente.createSocket(ip, 20000+(nodo+1)%4);
        break;
      }catch(Exception e){
        Thread.sleep(500);
      }
    }
    salida = new DataOutputStream(conexion.getOutputStream());
    w.join();
    while(true){
      if(nodo == 0){
        if(inicio){
          inicio = false;
          token = 1;
        }else{
          token = entrada.readLong();
          token+=1;
          System.out.println("Nodo: "+nodo+" Token: "+token);
        }
      }else{
        token = entrada.readLong();
        token+=1;
        System.out.println("Nodo: "+nodo+" Token: "+token);
      }
      if(nodo == 0 && token >= 1000){
        break;
      }
      salida.writeLong(token);
    }
    entrada.close();
    salida.close();
    conexion.close();
  }
}