package Utilidades;

import java.sql.*;

/** Descuento seguro de existencias; usar VentaService para ventas completas. */
public final class ActualiSQL {
    private ActualiSQL() {}
    public static void UProducto(int codigoProducto, int cantidad) throws SQLException {
        if(cantidad<=0) throw new IllegalArgumentException("La cantidad debe ser positiva");
        String sql="UPDATE TB_PRODUCTO SET stockPro=stockPro-? WHERE codPro=? AND stockPro>=?";
        try(Connection c=Conexion.Conectar();PreparedStatement p=c.prepareStatement(sql)) {
            p.setInt(1,cantidad);p.setInt(2,codigoProducto);p.setInt(3,cantidad);
            if(p.executeUpdate()!=1) throw new SQLException("Producto inexistente o stock insuficiente: "+codigoProducto);
        }
    }
}
