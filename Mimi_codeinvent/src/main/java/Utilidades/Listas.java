package Utilidades;

import Clases.Cliente;
import Clases.Factura;
import Clases.Producto;
import Clases.Trabajador;
import java.sql.*;
import java.util.ArrayList;

/** Consultas solo lectura, con liberación automática de recursos. */
public final class Listas {
    private Listas() {}
    public static ArrayList<Producto> LProductos() throws SQLException {
        ArrayList<Producto> out = new ArrayList<>();
        try (Connection c=Conexion.Conectar(); Statement s=c.createStatement();
             ResultSet r=s.executeQuery("SELECT * FROM TB_PRODUCTO ORDER BY codPro")) {
            while (r.next()) out.add(new Producto(r.getInt("codPro"),r.getString("descripPro"),
                    r.getDouble("precioUPro"),r.getInt("stockPro"),r.getString("tipoPro")));
        }
        return out;
    }
    public static ArrayList<Cliente> LClientes() throws SQLException {
        ArrayList<Cliente> out=new ArrayList<>();
        try (Connection c=Conexion.Conectar();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM TB_CLIENTE")) {
            while(r.next()) out.add(new Cliente(r.getLong("rucCli"),r.getString("nombreCli"),
                    r.getString("razonCli"),r.getString("correoCli")));
        }
        return out;
    }
    public static ArrayList<Trabajador> LTrabajadores() throws SQLException {
        ArrayList<Trabajador> out=new ArrayList<>();
        try (Connection c=Conexion.Conectar();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM TB_TRABAJADOR")) {
            while(r.next()) out.add(new Trabajador(r.getLong("idTbj"),r.getString("nombreTbj"),
                    r.getString("usuarioTbj"),"(protegida)",r.getString("tipoTbj")));
        }
        return out;
    }
    public static ArrayList<Factura> LFacturas() throws SQLException {
        ArrayList<Factura> out=new ArrayList<>();
        try (Connection c=Conexion.Conectar();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM TB_FACTURA ORDER BY codFact")) {
            while(r.next()) out.add(new Factura(r.getInt("codFact"),r.getString("fechaEFact"),
                    r.getLong("rucClien"),r.getLong("idTbja")));
        }
        return out;
    }
}
