//import negocio.Usuario;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Cliente {
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
                    }
                    break;
                }
            }
        }
        return retorno;
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
        System.out.print("Id del usuario: ");
        int id = sc.nextInt();
        return id;
    }
    private static void altaUsuario(Scanner sc){
        System.out.println("Alta de Usuario");
        Usuario anterior = null;
        Usuario usuarioARegistrar = solicitarDatosDeUsuario(anterior, sc);
        
    }
    private static void consultaUsuario(Scanner sc){
        System.out.println("Consulta de Usuario");
        int id_usuario = preguntarIdUsuario(sc);
    }
    private static void borraUsuario(Scanner sc){
        System.out.println("Borrar Usuario");
        int id_usuario = preguntarIdUsuario(sc);
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
