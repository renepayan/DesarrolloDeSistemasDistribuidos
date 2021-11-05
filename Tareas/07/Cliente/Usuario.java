/*
  Carlos Pineda Guerrero, Octubre 2021
*/

//import com.google.gson.*;

public class Usuario{
  public int id_usuario;
  public String email;
  public String nombre;
  public String apellido_paterno;
  public String apellido_materno;
  public String fecha_nacimiento;
  public String telefono;
  public String genero;
  public byte[] foto;

  // @FormParam necesita un metodo que convierta una String al objeto de tipo Usuario
  /*public static Usuario valueOf(String s) throws Exception
  {
    Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
    return (Usuario)j.fromJson(s,Usuario.class);
  }*/
}
