/**
 * CREATE TABLE carrito_compra(
 *     idCarrito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *     Articulo INT NOT NULL,
 *     Cantidad INT NOT NULL,
 *     FOREIGN KEY (Articulo) REFERENCES articulos(idArticulo)
 * );
 */

package carritoDeCompras;

import com.google.gson.*;

public class Compra {
    private int idCarrito;
    private Integer cantidad;
    private Articulo articulo;
    public Compra(int idCarrito, Integer cantidad, Articulo articulo){
        this.idCarrito = idCarrito;
        this.cantidad = cantidad;
        this.articulo = articulo;
    }
    public int getIdCarrito(){
        return this.idCarrito;        
    }
    public void setIdCarrito(int idCarrito){
        this.idCarrito = idCarrito;
    }

    public Integer getCantidad(){
        return this.cantidad;
    }
    public void setCantidad(Integer cantidad){
        this.cantidad = cantidad;
    }
    
    public Articulo getArticulo(){
        return this.articulo;
    }
    public void setArticulo(Articulo articulo){
        this.articulo = articulo;
    }
    public static Compra valueOf(String s) throws Exception{
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
        return (Compra)j.fromJson(s,Compra.class);
    }
}
