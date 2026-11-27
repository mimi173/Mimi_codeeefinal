package Utilidades;

import Clases.Cliente;
import Clases.Factura;
import Clases.Venta;
import java.sql.*;
import java.util.*;

/** Una venta es una sola transacción: cliente + factura + detalles + stock. */
public final class VentaService {
    private VentaService() {}
    public static void registrar(Cliente cliente, Factura factura, List<Venta> items) throws SQLException {
        if(cliente==null || factura==null || items==null || items.isEmpty())
            throw new IllegalArgumentException("Complete cliente, factura y productos");
        if(cliente.getCodigo()!=factura.getRucCli()) throw new IllegalArgumentException("RUC diferente en cliente y factura");
        String upsert="INSERT INTO TB_CLIENTE(rucCli,nombreCli,razonCli,correoCli) VALUES(?,?,?,?) " +
                "ON CONFLICT(rucCli) DO UPDATE SET nombreCli=excluded.nombreCli,razonCli=excluded.razonCli,correoCli=excluded.correoCli";
        String sqlFactura="INSERT INTO TB_FACTURA(codFact,fechaEFact,rucClien,idTbja) VALUES(?,?,?,?)";
        String sqlStock="UPDATE TB_PRODUCTO SET stockPro=stockPro-? WHERE codPro=? AND stockPro>=?";
        String sqlVenta="INSERT INTO TB_VENTA_PRODUCTO(codFac,codProd,cantidad,precioVenta) VALUES(?,?,?,?)";
        // Consolida productos repetidos antes del descuento (una fila por producto).
        Map<Integer,Venta> consolidados=new LinkedHashMap<>();
        for(Venta v:items) {
            if(v.getNumeroFactura()!=factura.getCodigoFactura() || v.getCantidad()<=0 ||
                    !Double.isFinite(v.getPrecioVenta()) || v.getPrecioVenta()<0)
                throw new IllegalArgumentException("Item de venta inválido");
            Venta anterior=consolidados.get(v.getCodigoPro());
            if(anterior==null) consolidados.put(v.getCodigoPro(),new Venta(v.getNumeroFactura(),v.getCodigoPro(),v.getCantidad(),v.getPrecioVenta()));
            else {
                if(Double.compare(anterior.getPrecioVenta(),v.getPrecioVenta())!=0)
                    throw new IllegalArgumentException("Precio diferente para el mismo producto");
                anterior.setCantidad(Math.addExact(anterior.getCantidad(),v.getCantidad()));
            }
        }
        try(Connection c=Conexion.Conectar()) {
            c.setAutoCommit(false);
            try(PreparedStatement pc=c.prepareStatement(upsert);
                PreparedStatement pf=c.prepareStatement(sqlFactura);
                PreparedStatement ps=c.prepareStatement(sqlStock);
                PreparedStatement pv=c.prepareStatement(sqlVenta)) {
                pc.setLong(1,cliente.getCodigo());pc.setString(2,cliente.getNombre());
                pc.setString(3,cliente.getRazonSocial());pc.setString(4,cliente.getCorreo());pc.executeUpdate();
                pf.setInt(1,factura.getCodigoFactura());pf.setString(2,factura.getFechaEmision());
                pf.setLong(3,factura.getRucCli());pf.setLong(4,factura.getIdTrabajador());pf.executeUpdate();
                for(Venta v:consolidados.values()) {
                    ps.setInt(1,v.getCantidad());ps.setInt(2,v.getCodigoPro());ps.setInt(3,v.getCantidad());
                    if(ps.executeUpdate()!=1) throw new SQLException("Stock insuficiente o producto no encontrado: "+v.getCodigoPro());
                    pv.setInt(1,factura.getCodigoFactura());pv.setInt(2,v.getCodigoPro());
                    pv.setInt(3,v.getCantidad());pv.setDouble(4,v.getPrecioVenta());pv.executeUpdate();
                }
                c.commit();
            } catch(Exception ex) {
                c.rollback();
                if(ex instanceof SQLException) throw (SQLException)ex;
                throw new SQLException("No se pudo registrar la venta",ex);
            }
        }
    }
}
