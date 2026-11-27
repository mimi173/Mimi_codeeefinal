package Ventanas;

import Utilidades.Conexion;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Registro extends JFrame {
    private static final String HISTORIAL = """
        SELECT f.codFact, f.fechaEFact, c.rucCli, c.razonCli, c.nombreCli,
               COALESCE(SUM(d.cantidad * d.precioVenta), 0),
               CASE WHEN a.codFact IS NULL THEN 'Confirmada' ELSE 'Anulada' END
          FROM TB_FACTURA f JOIN TB_CLIENTE c ON c.rucCli = f.rucClien
          LEFT JOIN TB_VENTA_PRODUCTO d ON d.codFac = f.codFact
          LEFT JOIN TB_VENTA_ANULADA a ON a.codFact = f.codFact
         GROUP BY f.codFact ORDER BY f.codFact DESC
        """;
    private static final String DETALLE = """
        SELECT p.descripPro, d.cantidad, d.precioVenta
          FROM TB_VENTA_PRODUCTO d JOIN TB_PRODUCTO p ON p.codPro = d.codProd
         WHERE d.codFac = ? ORDER BY d.codProd
        """;
    private static final String CREAR_ANULACIONES = """
        CREATE TABLE IF NOT EXISTS TB_VENTA_ANULADA (
            codFact INTEGER PRIMARY KEY REFERENCES TB_FACTURA(codFact)
        )
        """;
    private static final String MARCAR_ANULADA =
        "INSERT INTO TB_VENTA_ANULADA(codFact) VALUES (?) ON CONFLICT(codFact) DO NOTHING";
    private static final String REPONER = """
        UPDATE TB_PRODUCTO
           SET stockPro = stockPro + (SELECT d.cantidad FROM TB_VENTA_PRODUCTO d
                                       WHERE d.codFac = ? AND d.codProd = TB_PRODUCTO.codPro)
         WHERE codPro IN (SELECT codProd FROM TB_VENTA_PRODUCTO WHERE codFac = ?)
        """;
    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"Venta", "Fecha", "DNI / RUC", "Razón social", "Cliente", "Total (S/)", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea boleta = new JTextArea();
    private final JButton imprimir = new JButton("Imprimir boleta / PDF");
    private final JButton anular = new JButton("Anular venta");
    private final JButton eliminar = new JButton("Eliminar venta");
    private final JButton limpiar = new JButton("Limpiar historial");
    private final JLabel estado = new JLabel();

    public Registro() {
        this(null);
    }

    public Registro(Long numeroVenta) {
        super("Mimi_codeinvent · Consultar información registrada");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        setContentPane(panel);
        JLabel titulo = new JLabel("Ventas registradas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setForeground(new Color(80, 52, 116));
        panel.add(titulo, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setReorderingAllowed(false);
        boleta.setEditable(false);
        boleta.setLineWrap(true);
        boleta.setWrapStyleWord(true);
        boleta.setMargin(new Insets(12, 14, 12, 14));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(tabla), new JScrollPane(boleta));
        division.setResizeWeight(0.5);
        panel.add(division, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton volver = new JButton("Volver al menú");
        JButton actualizar = new JButton("Actualizar");
        botones.add(volver);
        botones.add(actualizar);
        botones.add(imprimir);
        botones.add(anular);
        botones.add(eliminar);
        botones.add(limpiar);
        eliminar.addActionListener(e -> borrarVentas(false));
        limpiar.addActionListener(e -> borrarVentas(true));
        anular.addActionListener(e -> anularSeleccion());
        pie.add(estado, BorderLayout.NORTH);
        pie.add(botones, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        actualizar.addActionListener(e -> cargar());
        volver.addActionListener(e -> dispose());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { new Menu().setVisible(true); }
        });
        imprimir.addActionListener(e -> {
            try {
                boleta.print(null, new MessageFormat("Página {0}"));
            } catch (PrinterException ex) {
                error("No se pudo imprimir", ex);
            }
        });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalle();
        });
        setSize(930, 630);
        setLocationRelativeTo(null);
        cargar();
        if (numeroVenta != null) {
            tabla.clearSelection();
            for (int fila = 0; fila < modelo.getRowCount(); fila++) {
                if (numeroVenta.equals(modelo.getValueAt(fila, 0))) {
                    tabla.setRowSelectionInterval(fila, fila);
                    tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
                    break;
                }
            }
        }
    }

    private static String documentoVisible(String numero) {
        if (String.valueOf(Tienda.PUBLICO_GENERAL).equals(numero)) return "Sin documento";
        if (numero.length() < 8) return "0".repeat(8 - numero.length()) + numero;
        return numero;
    }

    private static String soles(BigDecimal importe) {
        return importe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void cargar() {
        limpiar.setEnabled(false);
        eliminar.setEnabled(false);
        modelo.setRowCount(0);
        boleta.setText("");
        imprimir.setEnabled(false);
        anular.setEnabled(false);
        try (Connection cn = Conexion.Conectar(); Statement st = cn.createStatement()) {
            st.execute(CREAR_ANULACIONES);
            try (ResultSet rs = st.executeQuery(HISTORIAL)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{rs.getLong(1), rs.getString(2), documentoVisible(rs.getString(3)),
                        rs.getString(4), rs.getString(5), soles(rs.getBigDecimal(6)), rs.getString(7)});
                }
            }
            limpiar.setEnabled(modelo.getRowCount() > 0);
            estado.setText("Ventas registradas: " + modelo.getRowCount());
            if (modelo.getRowCount() > 0) tabla.setRowSelectionInterval(0, 0);
            else boleta.setText("Las ventas confirmadas en Tienda aparecerán aquí.");
        } catch (SQLException ex) {
            modelo.setRowCount(0);
            estado.setText("Historial no disponible");
            error("No se pudo cargar el historial", ex);
        }
    }

    private void mostrarDetalle() {
        boleta.setText("");
        imprimir.setEnabled(false);
        anular.setEnabled(false);
        int fila = tabla.getSelectedRow();
        eliminar.setEnabled(fila >= 0);
        if (fila < 0) return;
        long id = (Long) modelo.getValueAt(fila, 0);
        StringBuilder texto = new StringBuilder("Mimi_codeinvent · Boleta interna\n");
        boolean confirmada = "Confirmada".equals(modelo.getValueAt(fila, 6));
        if (!confirmada) texto.append("VENTA ANULADA\n");
        texto.append("Venta N.º ").append(id).append("     Fecha: ").append(modelo.getValueAt(fila, 1))
             .append("\nCliente: ").append(modelo.getValueAt(fila, 4)).append("\nRazón social: ")
             .append(modelo.getValueAt(fila, 3)).append("     DNI / RUC: ").append(modelo.getValueAt(fila, 2))
             .append("\n\nArtículos\n");
        try (Connection cn = Conexion.Conectar(); PreparedStatement st = cn.prepareStatement(DETALLE)) {
            st.setLong(1, id);
            BigDecimal total = BigDecimal.ZERO;
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int cantidad = rs.getInt(2);
                    BigDecimal precio = rs.getBigDecimal(3);
                    BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
                    texto.append(rs.getString(1)).append("  ·  ").append(cantidad)
                         .append(" × S/ ").append(soles(precio)).append(" = S/ ")
                         .append(soles(subtotal)).append('\n');
                    total = total.add(subtotal);
                }
            }
            boleta.setText(texto.append("\nTotal: S/ ").append(soles(total)).toString());
            boleta.setCaretPosition(0);
            imprimir.setEnabled(confirmada);
            anular.setEnabled(confirmada);
        } catch (SQLException ex) {
            error("No se pudo cargar la venta", ex);
        }
    }

    private void anularSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        long id = (Long) modelo.getValueAt(fila, 0);
        String pregunta = "¿Anular la venta " + id + " y devolver sus unidades al inventario?";
        if (JOptionPane.showConfirmDialog(this, pregunta, "Anular venta",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection cn = Conexion.Conectar()) {
            cn.setAutoCommit(false);
            try (PreparedStatement marcar = cn.prepareStatement(MARCAR_ANULADA);
                 PreparedStatement reponer = cn.prepareStatement(REPONER)) {
                marcar.setLong(1, id);
                if (marcar.executeUpdate() == 1) {
                    reponer.setLong(1, id);
                    reponer.setLong(2, id);
                    reponer.executeUpdate();
                }
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            error("No se pudo anular la venta", ex);
        }
        cargar();
    }

    private void borrarVentas(boolean todas) {
        int fila = tabla.getSelectedRow();
        if (modelo.getRowCount() == 0 || (!todas && fila < 0)) return;
        Long id = todas ? null : (Long) modelo.getValueAt(fila, 0);
        String pregunta = todas
                ? "¿Eliminar definitivamente todas las ventas del historial y sus detalles?"
                : "¿Eliminar definitivamente la venta " + id + " y su detalle?";
        pregunta += "\nSe devolverán al inventario las unidades de las ventas confirmadas."
                + "\nLas ventas ya anuladas se borrarán sin sumar unidades otra vez.";
        Object[] opciones = {"Cancelar", todas ? "Limpiar historial" : "Eliminar venta"};
        if (JOptionPane.showOptionDialog(this, pregunta, "Confirmar eliminación",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]) != 1) return;
        try (Connection cn = Conexion.Conectar()) {
            eliminarDeBase(cn, id);
        } catch (SQLException ex) {
            error("No se pudo completar la eliminación", ex);
        }
        cargar();
    }

    private static void eliminarDeBase(Connection cn, Long id) throws SQLException {
        cn.setAutoCommit(false);
        // La tabla temporal fija el mismo conjunto de ventas para toda la operación.
        try (Statement st = cn.createStatement()) {
            st.execute("CREATE TEMP TABLE por_borrar (id INTEGER PRIMARY KEY)");
            String seleccionar = "INSERT INTO por_borrar SELECT codFact FROM TB_FACTURA";
            if (id != null) seleccionar += " WHERE codFact = ?";
            try (PreparedStatement ps = cn.prepareStatement(seleccionar)) {
                if (id != null) ps.setLong(1, id);
                ps.executeUpdate();
            }
            st.executeUpdate("""
                WITH devoluciones AS (
                    SELECT d.codProd, SUM(d.cantidad) AS unidades
                      FROM TB_VENTA_PRODUCTO d JOIN por_borrar b ON b.id = d.codFac
                      LEFT JOIN TB_VENTA_ANULADA a ON a.codFact = b.id
                     WHERE a.codFact IS NULL GROUP BY d.codProd
                )
                UPDATE TB_PRODUCTO SET stockPro = stockPro +
                    (SELECT unidades FROM devoluciones WHERE codProd = TB_PRODUCTO.codPro)
                 WHERE codPro IN (SELECT codProd FROM devoluciones)
                """);
            st.executeUpdate("DELETE FROM TB_VENTA_PRODUCTO WHERE codFac IN (SELECT id FROM por_borrar)");
            st.executeUpdate("DELETE FROM TB_VENTA_ANULADA WHERE codFact IN (SELECT id FROM por_borrar)");
            st.executeUpdate("DELETE FROM TB_FACTURA WHERE codFact IN (SELECT id FROM por_borrar)");
            cn.commit();
        } catch (SQLException ex) {
            cn.rollback();
            throw ex;
        }
    }

    private void error(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(this, mensaje + "\n" + ex.getMessage(), "Registros",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Registro().setVisible(true));
    }
}
