package carritoDeCompras;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;  

public class CompraDatos {
    public static void insertarCompra(Compra compra, Connection conexion) throws Exception{
        PreparedStatement pstmInsert = conexion.prepareStatement("INSERT INTO carrito_compra (Articulo, Cantidad) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        pstmInsert.setInt(1, compra.getArticulo().getIdArticulo());
        pstmInsert.setInt(2, compra.getCantidad().intValue());
        pstmInsert.executeUpdate();
        ResultSet rsId = pstmInsert.getGeneratedKeys();
        if (rsId.next()) {
            compra.setIdCarrito((int) rsId.getInt(1));
        }
        rsId.close();
        pstmInsert.close();
    }
    public static Compra getCompraById(int idCompra, Connection conexion) throws Exception{
        Compra respuesta;
        PreparedStatement pstmGet = conexion.prepareStatement("SELECT Articulo,Cantidad FROM carrito_compra WHERE idCarrito = ? LIMIT 1");
        pstmGet.setInt(1, idCompra);
        ResultSet rsGet = pstmGet.executeQuery();
        if(rsGet.next()){
            respuesta = new Compra(idCompra, new Integer(rsGet.getInt(2)), ArticuloDatos.getArticuloById(rsGet.getInt(1), conexion));
        }else{
            respuesta = null;
        }
        rsGet.close();
        pstmGet.close();
        return respuesta;
    }
    public static ArrayList<Compra> getAllCompras(Connection conexion) throws Exception{
        ArrayList<Compra> respuesta = new ArrayList<Compra>();
        PreparedStatement pstmGet = conexion.prepareStatement("SELECT idCarrito FROM carrito_compra");
        ResultSet rsGet = pstmGet.executeQuery();
        while(rsGet.next()){
            respuesta.add(CompraDatos.getCompraById(rsGet.getInt(1), conexion));
        }
        rsGet.close();
        pstmGet.close();
        return respuesta;
    }
}
