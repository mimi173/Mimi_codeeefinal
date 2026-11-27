package Ventanas;

import Utilidades.AppTheme;
import Utilidades.Conexion;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestionInventario extends JFrame {

    // Paleta de Mimi_codeinvent
    private static final Color CREMA       = new Color(250, 247, 241);
    private static final Color SUPERFICIE  = new Color(255, 253, 250);
    private static final Color LILA_SUAVE  = new Color(244, 237, 250);
    private static final Color LILA        = new Color(230, 217, 243);
    private static final Color MORADO      = new Color(91, 53, 122);
    private static final Color TEXTO       = new Color(49, 40, 55);
    private static final Color BORDE       = new Color(211, 198, 221);
    private static final Color CELESTE     = new Color(211, 235, 250);
    private static final Color AZUL_OSCURO = new Color(32, 65, 89);
    private static final Color ROSA_SUAVE  = new Color(248, 233, 238);
    private static final Color VINO        = new Color(119, 48, 71);

    private final JTextField codigo = new JTextField(8);
    private final JTextField nombre = new JTextField(20);
    private final JTextField precio = new JTextField(8);
    private final JTextField stock = new JTextField(8);
    private final JTextField tipo = new JTextField(12);
    private final JTextField busqueda = new JTextField(18);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Descripción", "Precio (S/)", "Stock", "Tipo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);

    public GestionInventario() {
        super("Mimi_codeinvent | Inventario");
        AppTheme.installGlobalTheme();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        construirInterfaz();

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        setSize(Math.min(1000, pantalla.width), Math.min(650, pantalla.height));
        setLocationRelativeTo(null);
        cargar();
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(CREMA);
        raiz.setBorder(new EmptyBorder(16, 18, 18, 18));

        // Encabezado
        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setBackground(LILA);
        cabecera.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titulo = new JLabel("Inventario");
        titulo.setFont(AppTheme.fontMedium(24f));
        titulo.setForeground(MORADO);
        cabecera.add(titulo, BorderLayout.WEST);

        JButton volver = boton("Volver al menú", SUPERFICIE, MORADO);
        volver.addActionListener(e -> {
            dispose();
            new Menu().setVisible(true);
        });
        cabecera.add(volver, BorderLayout.EAST);
        raiz.add(cabecera, BorderLayout.NORTH);

        // Formulario
        JPanel formulario = new JPanel(new BorderLayout(0, 12));
        formulario.setBackground(LILA_SUAVE);
        formulario.setBorder(bordePanel());
        formulario.add(tituloSeccion("Datos del producto"), BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);

        agregarCampo(campos, 0, "Código", codigo, 0.13);
        agregarCampo(campos, 1, "Descripción", nombre, 0.34);
        agregarCampo(campos, 2, "Precio (S/)", precio, 0.17);
        agregarCampo(campos, 3, "Stock", stock, 0.14);
        agregarCampo(campos, 4, "Tipo", tipo, 0.22);
        formulario.add(campos, BorderLayout.CENTER);

        JButton agregar = boton("Agregar", MORADO, Color.WHITE);
        JButton actualizar = boton("Actualizar", LILA, MORADO);
        JButton eliminar = boton("Eliminar", ROSA_SUAVE, VINO);
        JButton limpiar = boton("Limpiar", SUPERFICIE, TEXTO);

        agregar.addActionListener(e -> guardar(false));
        actualizar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarCampos());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 0));
        acciones.setOpaque(false);
        acciones.add(agregar);
        acciones.add(actualizar);
        acciones.add(limpiar);
        acciones.add(eliminar);
        formulario.add(acciones, BorderLayout.SOUTH);

        // Listado y búsqueda
        JPanel listado = new JPanel(new BorderLayout(0, 10));
        listado.setBackground(SUPERFICIE);
        listado.setBorder(bordePanel());

        JPanel barraListado = new JPanel(new BorderLayout(12, 0));
        barraListado.setOpaque(false);
        barraListado.add(tituloSeccion("Productos registrados"), BorderLayout.WEST);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 0));
        filtro.setOpaque(false);

        JLabel etiquetaBuscar = new JLabel("Nombre o código");
        etiquetaBuscar.setForeground(TEXTO);
        etiquetaBuscar.setFont(AppTheme.fontRegular(13f));
        etiquetaBuscar.setLabelFor(busqueda);
        estilizarCampo(busqueda);

        JButton buscar = boton("Buscar", LILA, MORADO);
        buscar.addActionListener(e -> cargar());
        busqueda.addActionListener(e -> cargar());

        filtro.add(etiquetaBuscar);
        filtro.add(busqueda);
        filtro.add(buscar);
        barraListado.add(filtro, BorderLayout.EAST);
        listado.add(barraListado, BorderLayout.NORTH);

        AppTheme.applyTable(tabla);
        tabla.setFont(AppTheme.fontRegular(13f));
        tabla.setBackground(SUPERFICIE);
        tabla.setForeground(TEXTO);
        tabla.setSelectionBackground(CELESTE);
        tabla.setSelectionForeground(AZUL_OSCURO);
        tabla.setRowHeight(30);
        tabla.setGridColor(LILA);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(MORADO);
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(AppTheme.fontMedium(13f));
        encabezado.setReorderingAllowed(false);

        // Da más espacio al nombre del producto.
        tabla.getColumnModel().getColumn(1).setPreferredWidth(240);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(SUPERFICIE);
        scrollTabla.setBorder(BorderFactory.createLineBorder(BORDE));
        listado.add(scrollTabla, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(listado, BorderLayout.CENTER);

        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int filaSeleccionada = tabla.getSelectedRow();
            if (filaSeleccionada < 0) return;

            int fila = tabla.convertRowIndexToModel(filaSeleccionada);
            JTextField[] destino = {codigo, nombre, precio, stock, tipo};

            for (int i = 0; i < destino.length; i++) {
                Object valor = modelo.getValueAt(fila, i);
                destino[i].setText(valor == null ? "" : valor.toString());
            }
        });
    }

    private Border bordePanel() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(13, 15, 13, 15)
        );
    }

    private JLabel tituloSeccion(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(AppTheme.fontMedium(17f));
        etiqueta.setForeground(MORADO);
        return etiqueta;
    }

    private void estilizarCampo(JTextField campo) {
        AppTheme.applyField(campo);
        campo.setBackground(Color.WHITE);
        campo.setForeground(TEXTO);
        campo.setCaretColor(MORADO);
        campo.setSelectionColor(CELESTE);
        campo.setSelectedTextColor(AZUL_OSCURO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void agregarCampo(JPanel contenedor, int columna, String texto,
                              JTextField campo, double peso) {
        JPanel grupo = new JPanel(new BorderLayout(0, 5));
        grupo.setOpaque(false);

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(AppTheme.fontMedium(13f));
        etiqueta.setForeground(TEXTO);
        etiqueta.setLabelFor(campo);

        estilizarCampo(campo);
        grupo.add(etiqueta, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);

        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = columna;
        posicion.gridy = 0;
        posicion.weightx = peso;
        posicion.fill = GridBagConstraints.HORIZONTAL;
        posicion.insets = new Insets(0, columna == 0 ? 0 : 8, 0, 0);
        contenedor.add(grupo, posicion);
    }

    private JButton boton(String texto, Color fondo, Color frente) {
        JButton boton = new JButton(texto);
        boton.setFont(AppTheme.fontMedium(13f));
        boton.setBackground(fondo);
        boton.setForeground(frente);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.equals(MORADO) ? MORADO : BORDE),
                new EmptyBorder(7, 13, 7, 13)
        ));
        return boton;
    }

    private void limpiarCampos() {
        tabla.clearSelection();
        for (JTextField campo : new JTextField[]{codigo, nombre, precio, stock, tipo}) {
            campo.setText("");
        }
        codigo.requestFocusInWindow();
    }

    private void cargar() {
        String sql = "SELECT codPro, descripPro, precioUPro, stockPro, tipoPro "
                + "FROM TB_PRODUCTO "
                + "WHERE descripPro LIKE ? OR CAST(codPro AS TEXT) LIKE ? "
                + "ORDER BY codPro";

        try (Connection conexion = Conexion.Conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            String filtro = "%" + busqueda.getText().trim() + "%";
            consulta.setString(1, filtro);
            consulta.setString(2, filtro);

            try (ResultSet filas = consulta.executeQuery()) {
                modelo.setRowCount(0);
                while (filas.next()) {
                    modelo.addRow(new Object[]{
                            filas.getInt("codPro"),
                            filas.getString("descripPro"),
                            filas.getDouble("precioUPro"),
                            filas.getInt("stockPro"),
                            filas.getString("tipoPro")
                    });
                }
            }
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private void guardar(boolean edicion) {
        try {
            int cod = Integer.parseInt(codigo.getText().trim());
            int unidades = Integer.parseInt(stock.getText().trim());
            double importe = Double.parseDouble(precio.getText().trim());
            String descripcion = nombre.getText().trim();
            String categoria = tipo.getText().trim();

            if (cod <= 0 || unidades < 0 || !Double.isFinite(importe)
                    || importe < 0 || descripcion.isEmpty() || categoria.isEmpty()) {
                throw new IllegalArgumentException(
                        "Ingresa un código positivo, una descripción y un tipo. "
                                + "El precio y el stock deben ser iguales o mayores que cero.");
            }

            String sql = edicion
                    ? "UPDATE TB_PRODUCTO SET descripPro=?, precioUPro=?, "
                    + "stockPro=?, tipoPro=? WHERE codPro=?"
                    : "INSERT INTO TB_PRODUCTO "
                    + "(descripPro, precioUPro, stockPro, tipoPro, codPro) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (Connection conexion = Conexion.Conectar();
                 PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setString(1, descripcion);
                consulta.setDouble(2, importe);
                consulta.setInt(3, unidades);
                consulta.setString(4, categoria);
                consulta.setInt(5, cod);

                if (consulta.executeUpdate() == 0) {
                    throw new SQLException("No se encontró el producto con código " + cod);
                }
            }

            cargar();
            JOptionPane.showMessageDialog(this,
                    edicion ? "Producto actualizado." : "Producto agregado.",
                    "Inventario", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Código y stock deben ser enteros. Para el precio puedes usar 25.50.",
                    "Revisa los datos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException | IllegalArgumentException ex) {
            error(ex);
        }
    }

    private void eliminar() {
        int cod;
        try {
            cod = Integer.parseInt(codigo.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto con código " + cod + "?",
                "Eliminar producto", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM TB_PRODUCTO WHERE codPro=?";
        try (Connection conexion = Conexion.Conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, cod);

            if (consulta.executeUpdate() == 0) {
                throw new SQLException("Producto no encontrado.");
            }
        } catch (SQLException ex) {
            error(ex);
            return;
        }

        limpiarCampos();
        cargar();
    }

    private void error(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Inventario", JOptionPane.ERROR_MESSAGE);
    }
}