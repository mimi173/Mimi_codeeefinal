
package Clases;

import java.util.Date;

/**
 *
 * @author Mimi
 */
public class Registro {
    
    private int codigoCli;
    private Date fechaIngreso;
    
    public Registro(int codigoCli,Date fechaIngreso){
        this.codigoCli = codigoCli;
        this.fechaIngreso = fechaIngreso;
    }

    public int getCodigoCli() {
        return codigoCli;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setCodigoCli(int codigoCli) {
        this.codigoCli = codigoCli;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    public String Info(){
        return "Ingreso en la fecha " + fechaIngreso;
    }
    
}
