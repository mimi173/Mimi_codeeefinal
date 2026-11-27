package Ventanas;

import Utilidades.AppTheme;
import Utilidades.Conexion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Registro de proveedores y vínculo con productos. */
public class GestionProveedores extends JFrame {
    private final JTextField codigo = new JTextField(12);
    private final JTextField nombre = new JTextField(22);
    private final JTextField producto = new JTextField(10);
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Código", "Nombre", "Cód. producto"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public GestionProveedores() {
        super("Mimi_codeinvent | Proveedores");
        AppTheme.installGlobalTheme();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);
        construirInterfaz();
        cargar();
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(AppTheme.FONDO_GENERAL);

        JPanel header = new JPanel(new BorderLayout());
        AppTheme.applyHeaderPanel(header);
        JLabel titulo = new JLabel("Proveedores");
        JLabel subtitulo = new JLabel("Administra la relación entre proveedores y productos del sistema");
        AppTheme.applyInverseLabel(titulo, 24f);
        subtitulo.setForeground(AppTheme.CELESTE);
        subtitulo.setFont(AppTheme.fontRegular(13f));
        JPanel textHeader = new JPanel();
        textHeader.setOpaque(false);
        textHeader.setLayout(new BoxLayout(textHeader, BoxLayout.Y_AXIS));
        textHeader.add(titulo);
        textHeader.add(subtitulo);
        header.add(textHeader, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);

        JPanel formCard = new JPanel(new BorderLayout(12, 12));
        AppTheme.applyCardPanel(formCard);
        JPanel labels = new JPanel(new GridLayout(1, 3, 10, 8));
        JPanel fields = new JPanel(new GridLayout(1, 3, 10, 8));
        labels.setOpaque(false);
        fields.setOpaque(false);
        for (String l : new String[]{"Código proveedor", "Nombre proveedor", "Código de producto"}) {
            JLabel label = new JLabel(l);
            AppTheme.applySectionTitle(label);
            label.setFont(AppTheme.fontMedium(13f));
            labels.add(label);
        }
        for (JTextField t : new JTextField[]{codigo, nombre, producto}) {
            AppTheme.applyField(t);
            fields.add(t);
        }
        JPanel formStack = new JPanel(new BorderLayout(0, 10));
        formStack.setOpaque(false);
        formStack.add(labels, BorderLayout.NORTH);
        formStack.add(fields, BorderLayout.CENTER);
        formCard.add(formStack, BorderLayout.NORTH);

        JButton agregar = new JButton("Agregar");
        JButton actualizar = new JButton("Actualizar");
        JButton eliminar = new JButton("Eliminar");
        JButton volver = new JButton("Volver al menú");
        for (JButton b : new JButton[]{agregar, actualizar, eliminar}) AppTheme.applyPrimaryButton(b);
        AppTheme.applySecondaryButton(volver);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        for (JButton b : new JButton[]{agregar, actualizar, eliminar, volver}) botones.add(b);
        formCard.add(botones, BorderLayout.SOUTH);
        center.add(formCard, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabla);
        AppTheme.applyScrollPane(scroll);
        AppTheme.applyTable(tabla);
        JPanel tableCard = new JPanel(new BorderLayout());
        AppTheme.applyCardPanel(tableCard);
        tableCard.add(scroll, BorderLayout.CENTER);
        center.add(tableCard, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int r = tabla.getSelectedRow();
                codigo.setText(modelo.getValueAt(r, 0).toString());
                nombre.setText(modelo.getValueAt(r, 1).toString());
                producto.setText(modelo.getValueAt(r, 2).toString());
            }
        });

        agregar.addActionListener(e -> guardar(false));
        actualizar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> eliminar());
        volver.addActionListener(e -> { dispose(); new Menu().setVisible(true); });
    }

    private void cargar() {
        modelo.setRowCount(0);
        try (Connection c = Conexion.Conectar(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT codProve,nombreProve,codProd FROM TB_PROVEEDOR ORDER BY codProve")) {
            while (r.next()) modelo.addRow(new Object[]{r.getLong(1), r.getString(2), r.getInt(3)});
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private void guardar(boolean editar) {
        try {
            long id = Long.parseLong(codigo.getText().trim());
            int prod = Integer.parseInt(producto.getText().trim());
            if (id <= 0 || prod <= 0 || nombre.getText().trim().isEmpty()) throw new IllegalArgumentException("Datos inválidos");
            String sql = editar
                    ? "UPDATE TB_PROVEEDOR SET nombreProve=?,codProd=? WHERE codProve=?"
                    : "INSERT INTO TB_PROVEEDOR(nombreProve,codProd,codProve) VALUES(?,?,?)";
            try (Connection c = Conexion.Conectar(); PreparedStatement q = c.prepareStatement(sql)) {
                q.setString(1, nombre.getText().trim());
                q.setInt(2, prod);
                q.setLong(3, id);
                if (q.executeUpdate() == 0) throw new SQLException("Proveedor no encontrado");
            }
            cargar();
            JOptionPane.showMessageDialog(this, "Proveedor guardado");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa códigos numéricos");
        } catch (SQLException | IllegalArgumentException ex) {
            error(ex);
        }
    }

    private void eliminar() {
        try {
            long id = Long.parseLong(codigo.getText().trim());
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try (Connection c = Conexion.Conectar(); PreparedStatement q = c.prepareStatement("DELETE FROM TB_PROVEEDOR WHERE codProve=?")) {
                q.setLong(1, id);
                q.executeUpdate();
            }
            cargar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor");
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private void error(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
