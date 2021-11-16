package carritoDeCompras;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import javax.annotation.processing.Generated;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.*;
import com.google.gson.*;

@Path("ws")
public class Servicio {
    static DataSource pool = null;
    static{		
        try{
            Context ctx = new InitialContext();
            pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    @POST
    @Path("registrar_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar_articulo(@FormParam("articulo") Articulo articulo) throws Exception{
        Connection conexion = pool.getConnection();
        try{
            if(articulo == null)
                return Response.status(400).entity(j.toJson(new Error("Se debe enviar un articulo"))).build();                
            if(articulo.getDescripcion() == null || articulo.getDescripcion().equals(""))
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una descripcion"))).build();
            if(articulo.getPrecio() == null || articulo.getPrecio().doubleValue()<=0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un precio valido"))).build();
            if(articulo.getInventario() == null || articulo.getInventario().longValue()<0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un inventario valido"))).build();
            if(articulo.getFoto() == null)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una fotografia"))).build();
            try{
                FotoDatos.insertarFoto(articulo.getFoto(), conexion);
            }catch(Exception e){
                e.printStackTrace();
                return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
            }
            try{
                ArticuloDatos.insertarArticulo(articulo, conexion);
            }catch(Exception e){
                e.printStackTrace();
                return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
            }
            return Response.ok().build();
        }catch(Exception e){
            e.printStackTrace();
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        }finally{
            conexion.close();
        }
    } 
    @POST
    @Path("buscar_articulos")
    public Response buscar_articulos(@FormParam("texto") String texto) throws Exception{
        Connection conexion = pool.getConnection();
        try{
            if(texto == null)
                texto = "";
            ArrayList<Articulo> articulos = new ArrayList<>();
            try{
                articulos = ArticuloDatos.findArticulosByTexto(texto, conexion);
            }catch(Exception e){
                e.printStackTrace();
                return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
            }
            Map<String,ArrayList<Articulo>> respuestaAlCliente = new HashMap<>();
            
            respuestaAlCliente.put("articulos",articulos);
            return Response.ok().entity(j.toJson(respuestaAlCliente)).build();
        }catch(Exception e){
            e.printStackTrace();
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        }finally{
            conexion.close();
        }
    }
    @POST
    @Path("comprar_articulo")
    public Response comprar_articulo(@FormParam("idArticulo") Integer idArticulo, @FormParam("cantidad") Integer cantidad) throws Exception{
        Connection conexion = pool.getConnection();
        int codigoError = 500;
        try{
            if(idArticulo == null || idArticulo.intValue() <= 0){
                codigoError = 400;
                throw new Exception("Se debe ingresar un id de articulo valido");
            }            
            if(cantidad == null || cantidad.intValue() <= 0){
                codigoError = 400;
                throw new Exception("Se debe ingresar una cantidad valida");
            }
            Articulo articuloComprado = ArticuloDatos.getArticuloById(idArticulo.intValue(), conexion);            
            if(articuloComprado == null){
                codigoError = 404;
                throw new Exception("Articulo no encontrado");
            }else{
                if(articuloComprado.getInventario().intValue() >= cantidad.intValue()){
                    conexion.setAutoCommit(false);
                    Compra nuevaCompra = new Compra(0, cantidad, articuloComprado);
                    CompraDatos.insertarCompra(nuevaCompra, conexion);                
                    articuloComprado.setInventario(new Long(articuloComprado.getInventario().longValue() - cantidad.intValue()));
                    ArticuloDatos.updateArticulo(articuloComprado, conexion);
                    conexion.commit();
                }else{
                    codigoError = 409;
                    throw new Exception("La cantidad comprada, excede la disponible");
                }                                
            }            
        }catch(Exception e){
            e.printStackTrace();
            return Response.status(codigoError).entity(j.toJson(new Error(e.getMessage()))).build();
        }finally{
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.ok().build();
    }
    @GET
    @Path("ver_carrito")
    public Response ver_carrito() throws Exception{
        Connection conexion = pool.getConnection();
        try{
            ArrayList<Compra> carrito = CompraDatos.getAllCompras(conexion);
            Map<String,ArrayList<Compra>> respuestaAlCliente = new HashMap<>();            
            respuestaAlCliente.put("compras",carrito);
            return Response.ok().entity(j.toJson(respuestaAlCliente)).build();            
        }catch(Exception e){
            e.printStackTrace();
            return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
        }finally{
            conexion.close();
        }
    }
}
