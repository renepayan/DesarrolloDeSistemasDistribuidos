/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero, Octubre 2021
*/

package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import com.google.gson.*;
import java.util.Map;
import java.util.HashMap;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio
{
  static DataSource pool = null;
  static
  {		
    try
    {
      Context ctx = new InitialContext();
      pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder()
		.registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
		.create();

  @POST
  @Path("alta_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("usuario") Usuario usuario) throws Exception
  {
    Connection conexion = pool.getConnection();

    if (usuario.email == null || usuario.email.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el email"))).build();

    if (usuario.nombre == null || usuario.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();

    if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el apellido paterno"))).build();

    if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la fecha de nacimiento"))).build();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_usuario FROM usuarios WHERE email=?");
      try{
        stmt_1.setString(1,usuario.email);
        ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next())
             return Response.status(400).entity(j.toJson(new Error("El email ya existe"))).build();
        }
        finally{
          rs.close();
        }
      }
      finally{
        stmt_1.close();
      }

      PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO usuarios VALUES (0,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
      try{
        stmt_2.setString(1,usuario.email);
        stmt_2.setString(2,usuario.nombre);
        stmt_2.setString(3,usuario.apellido_paterno);
        stmt_2.setString(4,usuario.apellido_materno);
        stmt_2.setString(5,usuario.fecha_nacimiento);
        stmt_2.setString(6,usuario.telefono);
        stmt_2.setString(7,usuario.genero);
        stmt_2.executeUpdate();
        ResultSet keyResultSet = stmt_2.getGeneratedKeys();
        if (keyResultSet.next()) {
            usuario.id_usuario = (int) keyResultSet.getInt(1);
        }
      }finally{
        stmt_2.close();
      }

      if (usuario.foto != null)
      {
        PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_usuarios VALUES (0,?,?)");
        try
        {
          stmt_3.setBytes(1,usuario.foto);
          stmt_3.setInt(2,usuario.id_usuario);
          stmt_3.executeUpdate();
        }
        finally
        {
          stmt_3.close();
        }
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    Map<String,Integer> respuestaAlCliente = new HashMap<>();
    respuestaAlCliente.put("id_usuario",usuario.id_usuario);
    return Response.ok().entity(j.toJson(respuestaAlCliente)).build();
  }

  @POST
  @Path("consulta_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consulta(@FormParam("id_usuario") Integer id_usuario) throws Exception
  {
    Connection conexion= pool.getConnection();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.email,a.nombre,a.apellido_paterno,a.apellido_materno,a.fecha_nacimiento,a.telefono,a.genero,a.id_usuario,b.foto FROM usuarios a LEFT OUTER JOIN fotos_usuarios b ON a.id_usuario=b.id_usuario WHERE a.id_usuario=?");
      try
      {
        stmt_1.setInt(1,id_usuario);

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          if (rs.next())
          {
            Usuario r = new Usuario();
            r.email = rs.getString(1);
            r.nombre = rs.getString(2);
            r.apellido_paterno = rs.getString(3);
            r.apellido_materno = rs.getString(4);
            r.fecha_nacimiento = rs.getString(5);
            r.telefono = rs.getString(6);
            r.genero = rs.getString(7);
            r.id_usuario = rs.getInt(8);
	          r.foto = rs.getBytes(9);
            return Response.ok().entity(j.toJson(r)).build();
          }
          return Response.status(400).entity(j.toJson(new Error("El id del usuario no existe"))).build();
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
  }

  @POST
  @Path("modifica_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response modifica(@FormParam("usuario") Usuario usuario) throws Exception
  {
    Connection conexion= pool.getConnection();
    if (usuario.id_usuario == null)
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el id del usuario"))).build();

    if (usuario.email == null || usuario.email.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el email"))).build();

    if (usuario.nombre == null || usuario.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();

    if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el apellido paterno"))).build();

    if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la fecha de nacimiento"))).build();
    
    PreparedStatement stmt_0 = conexion.prepareStatement("SELECT 1 FROM usuarios WHERE id_usuario = ?");
    try{
      stmt_0.setInt(1,usuario.id_usuario.intValue());
      stmt_0.executeQuery();
      ResultSet rs = stmt_0.executeQuery();
      try{
        if (!rs.next())
          return Response.status(400).entity(j.toJson(new Error("El id del usuario no existe"))).build();
      }
      finally{
        rs.close();
      }
    }catch(Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }finally{
      stmt_0.close();
    }
    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("UPDATE usuarios SET nombre=?,apellido_paterno=?,apellido_materno=?,fecha_nacimiento=?,telefono=?,genero=?,email=? WHERE id_usuario=?");
      try
      {
        stmt_1.setString(1,usuario.nombre);
        stmt_1.setString(2,usuario.apellido_paterno);
        stmt_1.setString(3,usuario.apellido_materno);
        stmt_1.setString(4,usuario.fecha_nacimiento);
        stmt_1.setString(5,usuario.telefono);
        stmt_1.setString(6,usuario.genero);
        stmt_1.setString(7, usuario.email);
        stmt_1.setInt(8,usuario.id_usuario.intValue());
        stmt_1.executeUpdate();
      }
      finally
      {
        stmt_1.close();
      }

      if (usuario.foto != null)
      {
        PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=?");
        try
        {
          stmt_2.setInt(1,usuario.id_usuario);
          stmt_2.executeUpdate();
        }
        finally
        {
          stmt_2.close();
        }

        PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_usuarios VALUES (0,?,?)");
        try
        {
          stmt_3.setBytes(1,usuario.foto);
          stmt_3.setInt(2,usuario.id_usuario);
          stmt_3.executeUpdate();
        }
        finally
        {
          stmt_3.close();
        }
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    return Response.ok().build();
  }

  @POST
  @Path("borrar_usuarios")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borrarTodos() throws Exception
  {
    Connection conexion= pool.getConnection();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE 1 = 1");
      try{
	      stmt_1.executeUpdate();
      }
      finally{
        stmt_1.close();
      }
      PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM usuarios");
      try
      {
	      stmt_2.executeUpdate();
      }
      finally
      {
        stmt_2.close();
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    return Response.ok().build();
  }
  @POST
  @Path("borra_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borra(@FormParam("id_usuario") Integer id_usuario) throws Exception{
    Connection conexion= pool.getConnection();

    try{
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT 1 FROM usuarios WHERE id_usuario=?");
      try{
        stmt_1.setInt(1,id_usuario);

        ResultSet rs = stmt_1.executeQuery();
        try{
          if (!rs.next())
		        return Response.status(400).entity(j.toJson(new Error("El id del usuario no existe"))).build();
        }
        finally{
          rs.close();
        }
      }
      finally{
        stmt_1.close();
      }
      PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=?");
      try{
        stmt_2.setInt(1,id_usuario);
	      stmt_2.executeUpdate();
      }
      finally{
        stmt_2.close();
      }
      PreparedStatement stmt_3 = conexion.prepareStatement("DELETE FROM usuarios WHERE id_usuario=? LIMIT 1");
      try{
        stmt_3.setInt(1,id_usuario);
	      stmt_3.executeUpdate();
      }
      finally{
        stmt_3.close();
      }
    }catch (Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }finally{
      conexion.close();
    }
    return Response.ok().build();
  }
}
