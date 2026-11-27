package Utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 para contraseñas: nunca almacenar texto plano en TB_TRABAJADOR. */
public final class Autenticacion {
    private Autenticacion() {}
    public static String hash(char[] clave) {
        byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);
        return "pbkdf2$"+Base64.getEncoder().encodeToString(salt)+"$"+ derivar(clave,salt);
    }
    private static String derivar(char[] clave,byte[] salt) {
        PBEKeySpec spec=new PBEKeySpec(clave,salt,120000,256);
        try {
            byte[] resultado=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(resultado);
        } catch(Exception e) {throw new IllegalStateException("Error criptográfico",e);} finally {spec.clearPassword();}
    }
    public static String ingresar(String usuario,char[] clave) throws SQLException {
        try(Connection c=Conexion.Conectar();PreparedStatement p=c.prepareStatement(
                "SELECT nombreTbj,contrasenaTbj FROM TB_TRABAJADOR WHERE usuarioTbj=?")) {
            p.setString(1,usuario);
            try(ResultSet r=p.executeQuery()) {
                if(!r.next()) return null;
                String almacenado=r.getString("contrasenaTbj");
                String[] partes=almacenado.split("\\$");
                if(partes.length!=3 || !"pbkdf2".equals(partes[0])) return null;
                byte[] salt=Base64.getDecoder().decode(partes[1]);
                byte[] esperado=Base64.getDecoder().decode(partes[2]);
                byte[] actual=Base64.getDecoder().decode(derivar(clave,salt));
                return MessageDigest.isEqual(esperado,actual)?r.getString("nombreTbj"):null;
            }
        } catch(IllegalArgumentException e) {return null;}
    }
}
