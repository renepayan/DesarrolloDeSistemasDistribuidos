import java.util.*;   
import java.net.*;
import java.io.*;
public class Peticion {
    private static final String IP_VM = "sisdis.sytes.net";
    private static final int PUERTO_VM = 8080;
    private static class Respuesta{
        int codigo;
        String contenido;
        public Respuesta(int codigo, String contenido){
            this.codigo = codigo;
            this.contenido = contenido;
        }
    }
    private static Respuesta enviaPeticion(Map<String, String>mapaParametros, String metodo, String servicio) throws Exception{
        URL url = new URL("http://"+IP_VM+":"+PUERTO_VM+"/Servicio/rest/ws/"+servicio);
        String parametros = "";
        if(mapaParametros != null){
            for (Map.Entry<String, String> entry : mapaParametros.entrySet()) {
                parametros+= URLEncoder.encode(entry.getKey(),"UTF-8")+"="+URLEncoder.encode(entry.getValue(),"UTF-8")+"&";
            }
            if(parametros.charAt(parametros.length()-1) == '&')
                parametros = parametros.substring(0,parametros.length()-1);
        }
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod(metodo);
        conexion.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        System.out.println(parametros);
        int codigoRespuesta = conexion.getResponseCode();
        String respuestaTMP, contenido="";
        BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        while ((respuestaTMP = br.readLine()) != null) contenido+=respuestaTMP;
        br.close();
        conexion.disconnect();
        Respuesta r = new Respuesta(codigoRespuesta, contenido);
        return r;
    }
    public static void main(String[] args) throws Exception{
        Map<String,String> mapa = new HashMap<String,String>();
        mapa.put("a","51");
        mapa.put("b","65");
        mapa.put("c","51");
        mapa.put("d","90"); 
        Respuesta respuesta = enviaPeticion(mapa,"POST","prueba");
        System.out.println("Codigo de respuesta: "+respuesta.codigo);
        System.out.println("Respuesta: "+respuesta.contenido);
    }
}
    