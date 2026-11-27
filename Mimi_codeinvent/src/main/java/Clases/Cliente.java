
package Clases;

/**
 *
 * @author Mimi
 */
public class Cliente extends Persona {
    private String razonSocial;
    private String Correo;
    
    
    public Cliente(long Codigo, String nombre, String razonSocial, String Correo) {
        super(Codigo, nombre);
        this.razonSocial = razonSocial;
        this.Correo = Correo;
    }
    
    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }
    
    
}
