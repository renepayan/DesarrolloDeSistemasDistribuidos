/**
 * CREATE TABLE articulos(
 *     idArticulo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *     Descripcion LONGTEXT NOT NULL,
 *     Foto INT NOT NULL, 
 *     Precio Double NOT NULL,
 *     Inventario INT NOT NULL,
 *     FOREIGN KEY (Foto) REFERENCES fotos(idFoto)
 * );
 */

package carritoDeCompras;
import com.google.gson.*;
public class Articulo {
    private int idArticulo;
    private String descripcion;
    private Double precio;
    private Long inventario;
    private Foto foto;
    public Articulo(int idArticulo, String descripcion, Double precio, Long inventario, Foto foto){
        this.idArticulo = idArticulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.inventario = inventario;
        this.foto = foto;
    }   
    public int getIdArticulo(){
        return this.idArticulo;
    }
    public void setIdArticulo(int idArticulo){
        this.idArticulo = idArticulo;
    }

    public String getDescripcion(){
        return this.descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public Double getPrecio(){
        return this.precio;
    }
    public void getPrecio(Double precio){
        this.precio = precio;
    }

    public Long getInventario(){
        return this.inventario;
    }
    public void setInventario(Long inventario){
        this.inventario = inventario;
    }

    public Foto getFoto(){
        return this.foto;
    }
    public void setFoto(Foto foto){
        this.foto = foto;
    }
    public static Articulo valueOf(String s) throws Exception{
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).create();
        return (Articulo)j.fromJson(s,Articulo.class);
    }
}
