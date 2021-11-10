/**
 * CREATE TABLE articulos(
 *     idArticulo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *     Descripcion LONGTEXT NOT NULL,
 *     Foto INT NOT NULL, 
 *     Precio Double NOT NULL,
 *     Inventario INT NOT NULL,
 *     FOREIGN KEY (Foto) REFERENCES carrito_foto(idFoto)
 * );
 */

package carritoDeCompras;
public class Articulo {
    private id idArticulo;
    private String descripcion;
    private Dobule precio;
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
    public int setIdArticulo(int idArticulo){
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

    public void getInventario(){
        return this.inventario;
    }
    public Long setInventario(Long inventario){
        this.inventario = inventario;
    }

    public void getFoto(){
        return this.foto;
    }
    public Foto setFoto(Foto foto){
        this.foto = foto;
    }
}
