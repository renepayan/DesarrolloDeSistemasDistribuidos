package carritoDeCompras;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ArticuloDatos {
    public static void insertarArticulo(Articulo articulo, Connection conexion) throws Exception{
        PreparedStatement pstmInsert = conexion.prepareStatement("INSERT INTO articulos (Descripcion, Foto, Precio, Inventario) VALUES (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pstmInsert.setString(1, articulo.getDescripcion());
        pstmInsert.setInt(2, articulo.getFoto().getIdFoto());
        pstmInsert.setDouble(3, articulo.getPrecio().doubleValue());
        pstmInsert.setLong(4, articulo.getInventario().longValue());
        pstmInsert.executeUpdate();
        ResultSet rsId = pstmInsert.getGeneratedKeys();
        if (rsId.next()) {
            articulo.setId((int) keyResultSet.getInt(1));
        }
        rsId.close();
        pstmInsert.close();
    }
    public static Articulo getArticuloById(int idArticulo, Connection conexion) throws Exception{
        Articulo respuesta;
        PreparedStatement pstmGet = conexion.prepareStatement("SELECT Descripcion, Foto, Precio, Inventario WHERE idArticulo = ? LIMIT 1");
        pstmGet.setInt(1, idArticulo);
        ResultSet rsGet = pstmGet.executeQuery();
        if(rsGet.next()){
            respuesta = new Articulo(idArticulo, rsGet.getString(1), FotoDatos.getFotoById(rsGet.getInt(2)), new Double(rsGet.getDouble(3)), new Long(rsGet.getLong(4)));
        }else{
            respuesta = null;
        }
        rsGet.close();
        pstmGet.close();
        return respuesta;
    }
    public static ArrayList<Articulo> findArticulosByTexto(String texto, Connection conexion) throws Exception{
        ArrayList<Articulo> respuesta = new ArrayList<Articulo>();
        PreparedStatement pstmFInd = conexion.prepareStatement("SELECT idArticulo FROM articulos WHERE Descripcion = ?");
        ResultSet rsFind = pstmFInd.executeQuery();
        while(rsFind.next()){
            respuesta.add(ArticuloDatos.getArticuloById(rsFind.getInt(1), conexion));
        }
        rsFind.close();
        pstmFInd.close();
        return respuesta;
    }
}
