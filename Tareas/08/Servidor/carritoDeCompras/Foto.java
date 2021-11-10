/**
 * CREATE TABLE fotos(
 *     idFoto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *     Tipo VARCHAR(100) NOT NULL,
 *     Imagen LONGBLOB NOT NULL
 * );
 */

package carritoDeCompras;
public class Foto {
    private int idFoto;
    private byte[] imagen;
    private String tipo;

    public Foto(int idFoto, byte[] imagen, String tipo){
        this.idFoto = idFoto;
        this.imagen = imagen;
        this.tipo = tipo;
    }
    
    public int getIdFoto(){
        return this.idFoto;
    }
    public void setIdFoto(int idFoto){
        this.idFoto = idFoto;
    }
    
    public byte[] getImagen(){
        return this.imagen;
    }
    public void setImagen(byte[] imagen){
        this.imagen = imagen;
    }

    public String getTipo(){
        return this.tipo;
    }
    public void setTipo(String tipo){
        this.tipo = tipo;
    }
}
