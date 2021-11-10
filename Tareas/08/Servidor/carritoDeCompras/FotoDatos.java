package carritoDeCompras;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FotoDatos {
    public static void insertarFoto(Foto foto, Connection conexion) throws Exception{
        PreparedStatement pstmInsert = conexion.prepareStatement("INSERT INTO fotos (Imagen, Tipo) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pstmInsert.setBytes(1, foto.getImagen());
        pstmInsert.setString(2, foto.getTipo());
        pstmInsert.executeUpdate();
        ResultSet rsId = pstmInsert.getGeneratedKeys();
        if (rsId.next()) {
            foto.setId((int) keyResultSet.getInt(1));
        }
        rsId.close();
        pstmInsert.close();
    }
    public static Foto getFotoById(int id, Connection conexion) throws Exception{
        Foto respuesta;
        PreparedStatement pstmSelect = conexion.prepareStatement("SELECT idFoto,Imagen,Tipo FROM fotos WHERE idFoto = ? LIMIT 1");
        pstmSelect.setInt(1, id);
        ResultSet rsSelect = pstmSelect.executeQuery();
        if(rsSelect.next()){
            respuesta = new Foto(rsSelect.getInt(1), rsSelect.getBytes(2), rsSelect.getString(3));       
        }else{
            respuesta = null;
        }
        rsSelect.close();
        pstmSelect.close();
        return respuesta;
    }
    
}
