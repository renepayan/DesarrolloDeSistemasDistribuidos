package carritoDeCompras;

import javax.sql.DataSource;

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
    public Response(@FormParam("articulo") Articulo articulo){
        Conexion conexion = pool.getConnection();
        try{
            if(articulo.getDescripcion() == null || articulo.getDescripcion().equals(""))
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una descripcion"))).build();
            if(articulo.getPrecio() == null || articulo.getPrecio().doubleValue()<=0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un precio valido"))).build();
            if(articulo.getInventario() == null || articulo.getInventario().longValue()<0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un inventario valido"))).build();
            if(articulo.getFoto() == null)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una fotografia"))).build();
            try{
                FotografiaDatos.insertarFotografia(articulo.getFoto(), conexion);
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
        }catch(Exception e){
            e.printStackTrace();
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        }finally{
            conexion.close();
        }
    } 
    @POST
    @Path("buscar_articulos")
    public Response(@FormParam("texto") String texto){
        Conexion conexion = pool.getConnection();
        try{
            if(texto == null)
                texto = "";
            ArrayList<Articulo> articulos;
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
    public Response(@FormParam("idArticulo") Integer idArticulo, @FormParam("cantidad") Integer cantidad){
        Conexion conexion = pool.getConnection();
        try{
            if(idArticulo == null || idArticulo.intValue() <= 0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un id de articulo valido"))).build();
            
            if(cantidad == null || cantidad.intValue() <= 0)
                return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una cantidad valida"))).build();

            Carrito nuevaCompra = new Carrito(0, cantidad, ArticuloDatos.getArticuloById(idArticulo.intValue()));
            try{
                CarritoDatos.insertCompra(nuevaCompra, conexion);
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
}
