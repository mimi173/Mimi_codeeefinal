package Ventanas;

import Clases.Cliente;
import Clases.Factura;
import Clases.Producto;
import Clases.Trabajador;
import Clases.Venta;
import Utilidades.AppTheme;
import Utilidades.Listas;
import Utilidades.Utils;
import Utilidades.VentaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Tienda extends JFrame {

    // Registro.java usa este valor para reconocer las ventas sin DNI.
    public static final long PUBLICO_GENERAL = Long.MIN_VALUE;

    private static final Color CREMA = new Color(249, 246, 240);
    private static final Color TARJETA = new Color(255, 253, 250);
    private static final Color LILA = new Color(235, 225, 246);
    private static final Color MORADO = new Color(94, 57, 128);
    private static final Color TEXTO = new Color(48, 39, 54);
    private static final Color BORDE = new Color(218, 208, 227);

    private final JCheckBox identificar = new JCheckBox("Ingresar DNI o RUC");
    private final JTextField documento = new JTextField();
    private final JTextField nombre = new JTextField();
    private final JTextField razon = new JTextField();
    private final JTextField correo = new JTextField();
    private final JPanel datosCliente = new JPanel(new GridLayout(0, 2, 12, 8));

    private final JLabel productoActual = new JLabel("Selecciona un producto");
    private final JLabel precioActual = new JLabel("—");
    private final JLabel stockActual = new JLabel("—");
    private final JSpinner cantidad = new JSpinner(
            new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final DefaultTableModel modeloDetalle = modelo(
            "Producto", "Cant.", "P. unitario", "Subtotal");
    private final JTable tablaDetalle = new JTable(modeloDetalle);
    private final JLabel total = new JLabel("Total   S/ 0.00");
    private final JButton agregar = new JButton("Agregar a la venta");
    private final JButton quitar = new JButton("Quitar producto");
    private final JButton registrar = new JButton("Registrar venta");

    private final Map<String, Linea> carrito = new LinkedHashMap<>();
    private Producto seleccionado;

    private static class Linea {
        final Producto producto;
        final BigDecimal precio;
        int unidades;

        Linea(Producto producto, int unidades) {
            this.producto = producto;
            this.precio = BigDecimal.valueOf(producto.getPrecioUnitario());
            this.unidades = unidades;
        }

        BigDecimal subtotal() {
            return precio.multiply(BigDecimal.valueOf(unidades));
        }
    }

    public Tienda() throws SQLException {
        super("Mimi_codeinvent | Tienda · Ventas y stock");
        AppTheme.installGlobalTheme();

        URL icono = getClass().getResource("/Recursos/LOGO_BASE.png");
        if (icono != null) {
            setIconImage(new ImageIcon(icono).getImage());
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirPantalla();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Menu().setVisible(true);
            }
        });

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        setSize(Math.min(1080, pantalla.width), Math.min(680, pantalla.height));
        setLocationRelativeTo(null);
    }

    private void construirPantalla() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(CREMA);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(LILA);
        cabecera.setBorder(new EmptyBorder(12, 20, 12, 20));
        cabecera.add(titulo("Tienda", 24), BorderLayout.WEST);
        raiz.add(cabecera, BorderLayout.NORTH);

        JPanel cliente = tarjeta("Cliente");
        identificar.setOpaque(false);
        identificar.setForeground(TEXTO);
        cliente.add(identificar, BorderLayout.CENTER);

        datosCliente.setOpaque(false);
        datosCliente.add(campo("DNI / RUC", documento));
        datosCliente.add(campo("Nombre", nombre));
        datosCliente.add(campo("Razón social", razon));
        datosCliente.add(campo("Correo (opcional)", correo));
        datosCliente.setVisible(false);
        cliente.add(datosCliente, BorderLayout.SOUTH);

        identificar.addActionListener(e -> {
            datosCliente.setVisible(identificar.isSelected());
            cliente.revalidate();
            cliente.repaint();
            if (identificar.isSelected()) documento.requestFocusInWindow();
        });

        JPanel productos = tarjeta("Producto y stock");
        JPanel datosProducto = new JPanel(new GridLayout(0, 2, 12, 10));
        datosProducto.setOpaque(false);

        JButton consultar = new JButton("Consultar stock");
        vestirBoton(consultar, false);
        consultar.addActionListener(e -> consultarStock());

        datosProducto.add(campo("Producto seleccionado", productoActual));
        datosProducto.add(campo("Buscar por nombre o código", consultar));
        datosProducto.add(campo("Precio unitario", precioActual));
        datosProducto.add(campo("Disponible para agregar", stockActual));
        datosProducto.add(campo("Cantidad a vender", cantidad));
        productos.add(datosProducto, BorderLayout.CENTER);

        vestirBoton(agregar, true);
        agregar.setEnabled(false);
        agregar.addActionListener(e -> agregarProducto());
        JPanel accionesProducto = fila();
        accionesProducto.add(agregar);
        productos.add(accionesProducto, BorderLayout.SOUTH);

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);
        izquierda.add(cliente, BorderLayout.NORTH);
        izquierda.add(productos, BorderLayout.CENTER);

        JPanel detalle = tarjeta("Detalle de la venta");
        prepararTabla(tablaDetalle);
        JScrollPane scrollTabla = new JScrollPane(tablaDetalle);
        scrollTabla.setPreferredSize(new Dimension(390, 230));
        detalle.add(scrollTabla, BorderLayout.CENTER);

        total.setFont(new Font("SansSerif", Font.BOLD, 20));
        total.setForeground(MORADO);
        total.setHorizontalAlignment(SwingConstants.RIGHT);

        vestirBoton(quitar, false);
        vestirBoton(registrar, true);
        quitar.setEnabled(false);
        registrar.setEnabled(false);
        quitar.addActionListener(e -> quitarProducto());
        registrar.addActionListener(e -> registrarVenta());
        tablaDetalle.getSelectionModel().addListSelectionListener(
                e -> quitar.setEnabled(tablaDetalle.getSelectedRow() >= 0));

        JPanel accionesVenta = fila();
        accionesVenta.add(quitar);
        accionesVenta.add(registrar);

        JPanel pie = new JPanel(new BorderLayout(0, 8));
        pie.setOpaque(false);
        pie.add(total, BorderLayout.NORTH);
        pie.add(accionesVenta, BorderLayout.SOUTH);
        detalle.add(pie, BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new GridLayout(1, 2, 14, 0));
        contenido.setBackground(CREMA);
        contenido.setBorder(new EmptyBorder(16, 16, 16, 16));
        contenido.add(izquierda);
        contenido.add(detalle);

        JScrollPane vista = new JScrollPane(contenido);
        vista.setBorder(null);
        vista.getVerticalScrollBar().setUnitIncrement(18);
        raiz.add(vista, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private void consultarStock() {
        try {
            ArrayList<Producto> productos = Listas.LProductos();
            DefaultTableModel datos = modelo("Código", "Producto", "Stock", "Precio");
            for (Producto p : productos) {
                datos.addRow(new Object[]{
                        p.getCodigoProducto(), p.getDescripcionProducto(),
                        p.getStockProducto(), dinero(BigDecimal.valueOf(p.getPrecioUnitario()))
                });
            }

            JTable tabla = new JTable(datos);
            prepararTabla(tabla);
            TableRowSorter<DefaultTableModel> filtro = new TableRowSorter<>(datos);
            tabla.setRowSorter(filtro);

            JTextField buscar = new JTextField();
            buscar.getDocument().addDocumentListener(new DocumentListener() {
                private void aplicar() {
                    String valor = buscar.getText().trim();
                    filtro.setRowFilter(valor.isEmpty() ? null :
                            RowFilter.regexFilter("(?iu)" + Pattern.quote(valor), 0, 1));
                    if (tabla.getRowCount() > 0) tabla.setRowSelectionInterval(0, 0);
                }

                public void insertUpdate(DocumentEvent e) { aplicar(); }
                public void removeUpdate(DocumentEvent e) { aplicar(); }
                public void changedUpdate(DocumentEvent e) { aplicar(); }
            });

            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBackground(CREMA);
            panel.add(campo("Nombre o código del producto", buscar), BorderLayout.NORTH);

            JScrollPane resultados = new JScrollPane(tabla);
            resultados.setPreferredSize(new Dimension(510, 230));
            panel.add(resultados, BorderLayout.CENTER);
            if (tabla.getRowCount() > 0) tabla.setRowSelectionInterval(0, 0);

            Object[] opciones = {"Usar en la venta", "Cerrar"};
            int respuesta = JOptionPane.showOptionDialog(this, panel, "Consulta de stock",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    opciones, opciones[1]);

            if (respuesta == 0 && tabla.getSelectedRow() >= 0) {
                int indice = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                seleccionado = productos.get(indice);
                cantidad.setValue(1);
                mostrarSeleccion();
            }
        } catch (SQLException ex) {
            error("No se pudo consultar el inventario", ex);
        }
    }

    private void mostrarSeleccion() {
        if (seleccionado == null) {
            productoActual.setText("Selecciona un producto");
            precioActual.setText("—");
            stockActual.setText("—");
            agregar.setEnabled(false);
            return;
        }

        String codigo = String.valueOf(seleccionado.getCodigoProducto());
        Linea linea = carrito.get(codigo);
        int reservado = linea == null ? 0 : linea.unidades;
        int disponible = Math.max(0, seleccionado.getStockProducto() - reservado);

        productoActual.setText(seleccionado.getDescripcionProducto());
        productoActual.setToolTipText(seleccionado.getDescripcionProducto());
        precioActual.setText(dinero(BigDecimal.valueOf(seleccionado.getPrecioUnitario())));
        stockActual.setText(String.valueOf(disponible));
        agregar.setEnabled(disponible > 0);
    }

    private void agregarProducto() {
        if (seleccionado == null) return;

        try {
            cantidad.commitEdit();
        } catch (java.text.ParseException ex) {
            aviso("Escribe una cantidad entera mayor que cero.");
            return;
        }

        int unidades = ((Number) cantidad.getValue()).intValue();
        String codigo = String.valueOf(seleccionado.getCodigoProducto());
        Linea linea = carrito.get(codigo);
        int reservado = linea == null ? 0 : linea.unidades;

        if (unidades < 1 || unidades > seleccionado.getStockProducto() - reservado) {
            aviso("La cantidad supera el stock disponible o es menor que uno.");
            return;
        }

        if (linea == null) carrito.put(codigo, new Linea(seleccionado, unidades));
        else linea.unidades += unidades;

        cantidad.setValue(1);
        actualizarDetalle();
    }

    private void quitarProducto() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila < 0) return;
        String clave = new ArrayList<>(carrito.keySet()).get(
                tablaDetalle.convertRowIndexToModel(fila));
        carrito.remove(clave);
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        modeloDetalle.setRowCount(0);
        BigDecimal suma = BigDecimal.ZERO;

        for (Linea linea : carrito.values()) {
            modeloDetalle.addRow(new Object[]{
                    linea.producto.getDescripcionProducto(), linea.unidades,
                    dinero(linea.precio), dinero(linea.subtotal())
            });
            suma = suma.add(linea.subtotal());
        }

        total.setText("Total   " + dinero(suma));
        registrar.setEnabled(!carrito.isEmpty());
        mostrarSeleccion();
    }

    private void registrarVenta() {
        if (carrito.isEmpty()) return;

        boolean identificado = identificar.isSelected();
        String doc = documento.getText().trim();
        String nom = nombre.getText().trim();
        String empresa = razon.getText().trim();
        String mail = correo.getText().trim();

        if (identificado) {
            if (!doc.matches("(?:[0-9]{8}|[0-9]{11})")) {
                aviso("Ingresa un DNI de 8 dígitos o un RUC de 11 dígitos.");
                documento.requestFocusInWindow();
                return;
            }
            if (doc.length() == 8 && nom.isEmpty()) {
                aviso("Ingresa el nombre del cliente.");
                nombre.requestFocusInWindow();
                return;
            }
            if (doc.length() == 11 && empresa.isEmpty()) {
                aviso("Ingresa la razón social.");
                razon.requestFocusInWindow();
                return;
            }
        }

        String clienteVisible = identificado
                ? (doc.length() == 11 ? empresa : nom) : "Público general";
        long numeroDocumento = identificado ? Long.parseLong(doc) : PUBLICO_GENERAL;
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
        int numeroVenta;

        try {
            Trabajador trabajador = Utils.getData_Trabajador();
            if (trabajador == null) {
                aviso("Ingresa desde Login para registrar la venta con tu usuario.");
                return;
            }

            Utils.setFactura_Original();
            numeroVenta = Utils.getFactura_Original();

            Cliente cliente = new Cliente(numeroDocumento,
                    identificado && !nom.isEmpty() ? nom : clienteVisible,
                    identificado ? empresa : "", identificado ? mail : "");

            Factura factura = new Factura(numeroVenta, fecha,
                    numeroDocumento, trabajador.getCodigo());

            ArrayList<Venta> ventas = new ArrayList<>();
            for (Linea linea : carrito.values()) {
                ventas.add(new Venta(numeroVenta, linea.producto.getCodigoProducto(),
                        linea.unidades, linea.precio.doubleValue()));
            }

            // VentaService guarda la operación y actualiza el inventario.
            VentaService.registrar(cliente, factura, ventas);
        } catch (SQLException | IllegalArgumentException ex) {
            error("No se pudo registrar la venta", ex);
            return;
        }

        String boleta = crearBoleta(numeroVenta, fecha, clienteVisible,
                identificado ? doc : "Sin documento");
        limpiarVenta();

        if (identificado) {
            abrirBoleta(boleta);
        } else {
            int opcion = JOptionPane.showOptionDialog(this,
                    "Venta registrada. El stock fue actualizado.", "Venta completada",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new Object[]{"Seguir en Tienda", "Ver boleta"}, "Seguir en Tienda");
            if (opcion == 1) abrirBoleta(boleta);
        }
    }

    private String crearBoleta(int numero, String fecha, String cliente, String doc) {
        StringBuilder b = new StringBuilder("Mimi_codeinvent · Boleta interna\n\n");
        b.append("Venta N.º ").append(numero).append("     Fecha: ").append(fecha)
                .append("\nCliente: ").append(cliente)
                .append("\nDocumento: ").append(doc).append("\n\n");
        BigDecimal suma = BigDecimal.ZERO;
        for (Linea linea : carrito.values()) {
            b.append(linea.producto.getDescripcionProducto()).append("\n  ")
                    .append(linea.unidades).append(" × ").append(dinero(linea.precio))
                    .append(" = ").append(dinero(linea.subtotal())).append('\n');
            suma = suma.add(linea.subtotal());
        }
        return b.append("\nTOTAL: ").append(dinero(suma)).toString();
    }

    private void abrirBoleta(String contenido) {
        JTextArea hoja = new JTextArea(contenido, 17, 36);
        hoja.setEditable(false);
        hoja.setMargin(new Insets(12, 12, 12, 12));
        hoja.setCaretPosition(0);
        int opcion = JOptionPane.showOptionDialog(this, new JScrollPane(hoja),
                "Boleta de venta", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{"Imprimir", "Cerrar"}, "Cerrar");
        if (opcion == 0) {
            try {
                hoja.print();
            } catch (PrinterException ex) {
                error("No se pudo imprimir la boleta", ex);
            }
        }
    }

    private void limpiarVenta() {
        carrito.clear();
        seleccionado = null;
        documento.setText("");
        nombre.setText("");
        razon.setText("");
        correo.setText("");
        identificar.setSelected(false);
        datosCliente.setVisible(false);
        cantidad.setValue(1);
        actualizarDetalle();
        revalidate();
        repaint();
    }

    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static String dinero(BigDecimal importe) {
        return "S/ " + importe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static JLabel titulo(String texto, int tamano) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(MORADO);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, tamano));
        return etiqueta;
    }

    private static JPanel tarjeta(String nombre) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(14, 16, 14, 16)));
        panel.add(titulo(nombre, 18), BorderLayout.NORTH);
        return panel;
    }

    private static JPanel campo(String nombre, JComponent control) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setLabelFor(control);
        etiqueta.setForeground(TEXTO);
        panel.add(etiqueta, BorderLayout.NORTH);
        control.setPreferredSize(new Dimension(150, 32));
        panel.add(control, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel fila() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 2));
        panel.setOpaque(false);
        return panel;
    }

    private static void vestirBoton(JButton boton, boolean principal) {
        boton.setBackground(principal ? MORADO : LILA);
        boton.setForeground(principal ? Color.WHITE : MORADO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(7, 12, 7, 12));
    }

    private static void prepararTabla(JTable tabla) {
        AppTheme.applyTable(tabla);
        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Tienda",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(this, mensaje + "\n" + ex.getMessage(),
                "Tienda", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Tienda().setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "No se pudo abrir Tienda", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}