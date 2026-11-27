
package Clases;

/**
 *
 * @author Roy
 */
public class Trabajador extends Persona{
    private String usuario;
    private String contraseña;
    private String tipo;
    
    public Trabajador(long Codigo, String nombre,String usuario,
            String contraseña,String tipo) {
        super(Codigo, nombre);
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.tipo = tipo;
    }  
    
    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getTipo() {
        return tipo;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }   
    
    public boolean Permisos(){
        return "Jefe".equals(this.tipo);
    }
}
