import java.util.*;   
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class Cliente {
    private static final String IP_VM = "20.115.21.46";
    private static final int PUERTO_VM = 8080;
    private static void limpiarPantalla(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    private static int desplegarMenu(Scanner sc){
        System.out.println("a. Alta usuario");
        System.out.println("b. Consulta usuario");
        System.out.println("c. Borra usuario");
        System.out.println("d. Salir");
        System.out.print("\n\nOpcion: ");
        char opcion = sc.next().charAt(0);
        return opcion - 'a'+1;
    }
    private static String leerYVAlidarCadena(String cadenaOriginal, String nombreCampo, int min, int max, String expresionRegular, Scanner sc){
        String retorno = "";
        while(true){
            System.out.print(nombreCampo+" del usuario "+(cadenaOriginal!=null?"["+cadenaOriginal+"]: ":": "));
            retorno = sc.nextLine();
            if(retorno.length() == 0 && cadenaOriginal != null){
                retorno = cadenaOriginal;
                break;
            }else{
                if(retorno.length() < min || retorno.length() > max){
                    System.out.println("Error, "+nombreCampo+" debe tener al menos "+min+" caracteres y maximo"+max+" caracteres");
                }else{
                    if(expresionRegular != null){
                        if(!Pattern.compile(expresionRegular).matcher(retorno).matches()){
                            System.out.println("Error, "+nombreCampo+" no es valido");
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
            }
        }
        return retorno;
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
        int codigoRespuesta = conexion.getResponseCode();
        String respuestaTMP, contenido="";
        BufferedReader br;
        try{
            if(codigoRespuesta == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            }
            while ((respuestaTMP = br.readLine()) != null) contenido+=respuestaTMP;
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        conexion.disconnect();
        Respuesta r = new Respuesta(codigoRespuesta, contenido);
        return r;
    }
    private static Usuario solicitarDatosDeUsuario(Usuario datosAnteriores, Scanner sc){
        Usuario retorno = new Usuario();
        sc.nextLine();
        retorno.email = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.email, "El correo electronico", 3, 100, "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",sc);
        retorno.nombre = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.nombre, "El nombre", 3, 100, null,sc);
        retorno.apellido_paterno = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.apellido_paterno, "El apellido paterno", 3, 100, null,sc);
        retorno.apellido_materno = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.apellido_materno, "El apellido materno", 3, 100, null,sc);
        System.out.println("Formato: AAAA/MM/DD");
        retorno.fecha_nacimiento = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.fecha_nacimiento, "La fecha de nacimiento", 8, 10, "^((19|20)[0-9]{2})/(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])$",sc);
        retorno.telefono = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.telefono, "El numero de telefono", 10, 10, null,sc);
        retorno.genero = leerYVAlidarCadena((datosAnteriores == null)?null:datosAnteriores.genero, "El genero", 1, 1, "^[MFmf]$",sc).toUpperCase();
        return retorno;        
    }
    private static int preguntarIdUsuario(Scanner sc){
        int id = 0;
        while(true){
            System.out.print("Id del usuario: ");
            id = sc.nextInt();
            if(id <= 0){
                System.out.println("Id invalido");
            }else{
                break;
            }
        }
        return id;
    }
    private static void altaUsuario(Scanner sc){
        System.out.println("Alta de Usuario");
        Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
        Usuario usuarioARegistrar = solicitarDatosDeUsuario(null, sc);
        Map<String,String>mapaParametros = new HashMap<String,String>();
        mapaParametros.put("usuario", j.toJson(usuarioARegistrar));
        Respuesta respuesta = null;
        try{
             respuesta = enviaPeticion(mapaParametros, "POST", "alta_usuario");
        }catch(Exception e){

        }
        if(respuesta.getCodigo() == 200){
            System.out.println("Usuario registrado!! el id es: "+respuesta.getContenido());
        }else{
            System.out.println("Error al registrar el usuario "+respuesta.getContenido());
        }
        
    }
    private static void consultaUsuario(Scanner sc){
        System.out.println("Consulta de Usuario");
        int id_usuario = preguntarIdUsuario(sc);
        Map<String,String>mapaParametros = new HashMap<String,String>();
        mapaParametros.put("id_usuario", id_usuario+"");
        Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
        Respuesta respuesta = null;
        try{
            respuesta = enviaPeticion(mapaParametros, "POST", "consulta_usuario");
        }catch(Exception e){

        }
        if(respuesta.getCodigo() == 200){
            System.out.println("Usuario encontrado!!");
            Usuario usuarioAnterior = j.fromJson(respuesta.getContenido(), Usuario.class);
            System.out.println(usuarioAnterior.toString());
            System.out.print("¿Desea modificar los datos del usuario (s/n)? ");
            char resp = sc.next().toUpperCase().charAt(0);
            if(resp == 'S'){
                Usuario nuevoUsuario = solicitarDatosDeUsuario(usuarioAnterior, sc);
                nuevoUsuario.id_usuario = usuarioAnterior.id_usuario;
                mapaParametros.clear();
                mapaParametros.put("usuario", j.toJson(nuevoUsuario));
                Respuesta respuesta2 = null;
                try{
                    respuesta2 = enviaPeticion(mapaParametros, "POST", "modifica_usuario");
                }catch(Exception e){

                }
                if(respuesta2.getCodigo() == 200){
                    System.out.println("El usuario ha sido modificado");
                }else{
                    System.out.println("Error al modificar el usuario "+respuesta2.getCodigo());
                }
            }
        }else{
            System.out.println("Error al encontrar el usuario "+respuesta.getContenido());
        }
    }
    private static void borraUsuario(Scanner sc){
        System.out.println("Borrar Usuario");
        int id_usuario = preguntarIdUsuario(sc);
        Map<String,String>mapaParametros = new HashMap<String,String>();
        mapaParametros.put("id_usuario", id_usuario+"");
        Respuesta respuesta = null;
        try{
            respuesta = enviaPeticion(mapaParametros, "POST", "borra_usuario");
        }catch(Exception e){

        }
        if(respuesta.getCodigo() == 200){
            System.out.println("El usuario ha sido borrado");
        }else{
            System.out.println("Error al eliminar el usuario "+respuesta.getContenido());
        }
    }
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        limpiarPantalla();
        do{
            opcion = desplegarMenu(sc);
            limpiarPantalla();
            switch(opcion){
                case 1:
                    altaUsuario(sc);
                    break;
                case 2:
                    consultaUsuario(sc);
                    break;
                case 3:
                    borraUsuario(sc);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }while(opcion != 4);
        sc.close();
    }    
}
