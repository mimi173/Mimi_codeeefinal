
package Utilidades;

import Clases.Cliente;
import Clases.Factura;
import Clases.Producto;
import Clases.Trabajador;
import Clases.Venta;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Mimi
 */
public class Utils {
    
    public static String usuarioTrabajando;
    private static boolean estadoCambio;
    private static int Factura_Original;
    private static int Item = 0;
    private static ArrayList<Venta> Data_Venta = new ArrayList<>();
    private static Cliente Data_Cliente = null;
    private static Trabajador Data_Trabajador = null;
    private static Factura Data_Factura = null;
    
    public static boolean getItem() {
        return Item > 0;
    }

    public static void setItem() {
        Item++;
    }
    
    public static void Clear_Item() {
        Item = 0;
    }
       
    
    public static int getFactura_Original() {
        return Factura_Original;
    }

    public static void setFactura_Original() throws SQLException {
        try(java.sql.Connection c=Conexion.Conectar();
            java.sql.Statement st=c.createStatement();
            java.sql.ResultSet rs=st.executeQuery("SELECT COALESCE(MAX(codFact),999)+1 AS siguiente FROM TB_FACTURA")) {
            Factura_Original=rs.getInt("siguiente");
        }
    }

    public static boolean isEstadoCambio() {
        return estadoCambio;
    }

    public static void setEstadoCambio(boolean aEstadoCambio) {
        estadoCambio = aEstadoCambio;
    }
    
    public static void UTR(String Trabajador){
        usuarioTrabajando = Trabajador;
    }
    
    public static String UTRV(){
        return usuarioTrabajando;
    }  

    
    //--------------------------------------------------
    public static ArrayList<Venta> getData_Venta() {
        return Data_Venta;
    }

    public static void setData_Venta(Venta Lista_Venta) {
        Data_Venta.add(Lista_Venta);
    }
    
    public static void Clear_Data_Venta() {
        Data_Venta.clear();
    }
    
    //--------------------------------------------------
    
    public static Cliente getData_Cliente() {
        return Data_Cliente;
    }

    public static void setData_Cliente(Cliente aData_Cliente) {
        Data_Cliente = aData_Cliente;
    }
    
    public static void Clear_Data_Cliente() {
        Data_Cliente = null;
    }
     
    //--------------------------------------------------
     
    public static Trabajador getData_Trabajador() throws SQLException {
        ArrayList<Trabajador> lista = Listas.LTrabajadores();
        
        for(int i = 0; i < lista.size(); i++){
            if(String.valueOf(lista.get(i).getNombre()).equals(Utils.UTRV())){
                Data_Trabajador = lista.get(i);
            }
        }        
        return Data_Trabajador;
    }
  
    public static void Clear_Data_Trabajador() {
        Data_Trabajador = null;
    }
    
    //--------------------------------------------------
    
    public static Factura getData_Factura() {
        return Data_Factura;
    }

    public static void setData_Factura(Factura aData_Factura) {
        Data_Factura = aData_Factura;
    }
    
    public static void Clear_Data_Factura() {
        Data_Factura = null;
    }
}