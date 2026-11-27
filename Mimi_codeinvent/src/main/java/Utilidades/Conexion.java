package Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Acceso a SQLite local, sin servidor ni contraseñas en el código. */
public final class Conexion {
    private Conexion() {}
    public static Connection Conectar() throws SQLException {
        Path db = Paths.get(System.getProperty("user.dir"), "tecpro.db");
        if (!Files.exists(db)) {
            throw new SQLException("Falta tecpro.db en " + db + ". Copia el archivo de ejemplo junto al proyecto.");
        }
        Connection cn = DriverManager.getConnection("jdbc:sqlite:" + db.toAbsolutePath());
        try (Statement s = cn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            s.execute("PRAGMA busy_timeout = 5000");
        }
        return cn;
    }
}
