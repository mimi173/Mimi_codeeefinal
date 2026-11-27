
package Clases;

/**
 *
 * @author Mimi
 */
public class Factura {
  
    private int codigoFactura;
    private String fechaEmision;
    private long RucCli;
    private long idTrabajador; 
    
    public Factura(int codigoFactura,String fechaEmision,long RucCli,long idTrabajador){
        this.codigoFactura = codigoFactura;
        this.fechaEmision = fechaEmision;
        this.RucCli = RucCli;
        this.idTrabajador = idTrabajador;
    }    

    public int getCodigoFactura() {
        return codigoFactura;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public long getRucCli() {
        return RucCli;
    }

    public long getIdTrabajador() {
        return idTrabajador;
    }

    public void setCodigoFactura(int codigoFactura) {
        this.codigoFactura = codigoFactura;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
   
    public void setRucCli(long RucCli) {
        this.RucCli = RucCli;
    }

    public void setIdTrabajador(long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
      
}
