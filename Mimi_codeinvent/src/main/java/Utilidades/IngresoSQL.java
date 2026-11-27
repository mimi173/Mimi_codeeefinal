package Utilidades;

import java.sql.*;

/** Operaciones individuales; para finalizar compras usar VentaService.registrar(). */
public final class IngresoSQL {
    private IngresoSQL() {}
    public static void ICliente(long ruc,String nombre,String razon,String correo) throws SQLException {
        if(ruc<=0 || nombre==null || nombre.trim().isEmpty()) throw new IllegalArgumentException("Cliente inválido");
        try(Connection c=Conexion.Conectar();PreparedStatement p=c.prepareStatement(
                "INSERT INTO TB_CLIENTE(rucCli,nombreCli,razonCli,correoCli) VALUES(?,?,?,?) " +
                "ON CONFLICT(rucCli) DO UPDATE SET nombreCli=excluded.nombreCli,razonCli=excluded.razonCli,correoCli=excluded.correoCli")) {
            p.setLong(1,ruc);p.setString(2,nombre);p.setString(3,razon);p.setString(4,correo);p.executeUpdate();
        }
    }
    public static void IVenta(int factura,int producto,int cantidad,double precio) throws SQLException {
        if(cantidad<=0 || !Double.isFinite(precio)||precio<0) throw new IllegalArgumentException("Venta inválida");
        try(Connection c=Conexion.Conectar();PreparedStatement p=c.prepareStatement(
                "INSERT INTO TB_VENTA_PRODUCTO(codFac,codProd,cantidad,precioVenta) VALUES(?,?,?,?)")) {
            p.setInt(1,factura);p.setInt(2,producto);p.setInt(3,cantidad);p.setDouble(4,precio);p.executeUpdate();
        }
    }
    public static void IFactura(int factura,String fecha,long ruc,long trabajador) throws SQLException {
        try(Connection c=Conexion.Conectar();PreparedStatement p=c.prepareStatement(
                "INSERT INTO TB_FACTURA(codFact,fechaEFact,rucClien,idTbja) VALUES(?,?,?,?)")) {
            p.setInt(1,factura);p.setString(2,fecha);p.setLong(3,ruc);p.setLong(4,trabajador);p.executeUpdate();
        }
    }
}
