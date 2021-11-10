/**
 * CREATE TABLE carrito_compra(
 *     idCarrito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *     Articulo INT NOT NULL,
 *     Cantidad INT NOT NULL,
 *     FOREIGN KEY (Articulo) REFERENCES articulos(idArticulo)
 * );
 */

package carritoDeCompras;
public class Carrito {
    private int idCarrito;
    private Integer cantidad;
    private Articulo articulo;
    public Carrito(int idCarrito, Int cantidad, Articulo articulo){
        this.idCarrito = idCarrito;
        this.cantidad = cantidad;
        this.articulo = articulo;
    }
}
