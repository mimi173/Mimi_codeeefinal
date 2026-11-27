
package Clases;

/**
 *
 * @author Mimi
 */
public class Proveedor extends Persona{
    private String tipoProveedor;
    
    public Proveedor(long Codigo, String nombre, String tipoProveedor) {
        super(Codigo, nombre);
        this.tipoProveedor = tipoProveedor;
    }
    
    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProvedor) {
        this.tipoProveedor = tipoProvedor;
    }
    
    public int Relevancia(){
        int relev = 0;
        if(null == tipoProveedor){
            relev = 0;
        }
        else switch (tipoProveedor) {
            case "Empresarial":
                relev = 1;
                break;
            case "Standart":
                relev = 2;
                break;
            default:
                relev = 3;
                break;
        }
        
        return relev;
    }
}
