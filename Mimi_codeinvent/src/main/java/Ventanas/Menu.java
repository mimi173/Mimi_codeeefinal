package Ventanas;

import Utilidades.AppTheme;
import Utilidades.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.SQLException;

/** Menú principal de Mimi_codeinvent. Solo modifica la presentación visual. */
public class Menu extends JFrame {

    private static final Color CREMA = new Color(0xFAF5EC);
    private static final Color CREMA_CLARO = new Color(0xFFFCF7);
    private static final Color NEGRO = new Color(0x171717);
    private static final Color GRIS = new Color(0x5A5752);
    private static final Color BORDE = new Color(0xDED6C9);
    private static final Color HOVER = new Color(0xF2E8D9);
    private static final Color LILA = new Color(183, 154, 228);
    private static final Color AZUL = new Color(145, 184, 232);

    public Menu() {
        AppTheme.installGlobalTheme();
        configurarVentana();
        construirInterfaz();
        setLocationRelativeTo(null);
    }

    private void configurarVentana() {
        setTitle("");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setResizable(true);
        URL icono = getClass().getResource("/Recursos/LOGO_BASE.png");
        if (icono != null) setIconImage(new ImageIcon(icono).getImage());
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new BorderLayout(0, 26));
        fondo.setBackground(CREMA);
        fondo.setBorder(new EmptyBorder(32, 46, 32, 46));
        setContentPane(fondo);

        // Cabecera con los mismos tonos del Login.
        JPanel encabezado = new JPanel(new BorderLayout(22, 0));
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("Panel principal");
        titulo.setFont(fuente(Font.BOLD, 30));
        titulo.setForeground(NEGRO);

        String trabajador = Utils.UTRV();
        if (trabajador == null || trabajador.isBlank()) trabajador = "Usuario";
        JLabel saludo = new JLabel("Hola, " + trabajador);
        saludo.setFont(fuente(Font.PLAIN, 16));
        saludo.setForeground(GRIS);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        saludo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(9));
        textos.add(saludo);
        encabezado.add(textos, BorderLayout.CENTER);

        JLabel etiqueta = new JLabel("");
        etiqueta.setFont(fuente(Font.BOLD, 6));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setBackground(NEGRO);
        etiqueta.setOpaque(true);
        etiqueta.setBorder(new EmptyBorder(12, 17, 12, 17));
        JPanel etiquetaContenedor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        etiquetaContenedor.setOpaque(false);
        etiquetaContenedor.add(etiqueta);
        encabezado.add(etiquetaContenedor, BorderLayout.EAST);
        fondo.add(encabezado, BorderLayout.NORTH);

        // Cuatro módulos en una cuadrícula espaciosa, que crece con la ventana.
        JPanel cuadricula = new JPanel(new GridLayout(2, 2, 24, 24));
        cuadricula.setOpaque(false);
        cuadricula.setBorder(new EmptyBorder(8, 0, 8, 0));

        // TIENDA - LILA
        cuadricula.add(crearTarjeta(
                "Tienda",
                "Registrar compras y ventas",
                "/Recursos/TIENDA_V2.png",
                this::abrirTienda,
                LILA
        ));

        // INVENTARIO - AZUL
        cuadricula.add(crearTarjeta(
                "Inventario",
                "Consultar y administrar el stock",
                "/Recursos/PROVEEDORES.png",
                this::abrirInventario,
                AZUL
        ));

        // REGISTROS - AZUL
        cuadricula.add(crearTarjeta(
                "Registros",
                "Consultar información registrada",
                "/Recursos/STOCK.png",
                this::abrirRegistros,
                AZUL
        ));

        // PROVEEDORES - LILA
        cuadricula.add(crearTarjeta(
                "Proveedores",
                "Gestionar proveedores y productos",
                "/Recursos/REGISTROS.png",
                this::abrirProveedores,
                LILA
        ));

        fondo.add(cuadricula, BorderLayout.CENTER);

        JLabel pie = new JLabel(
                "Mimi_codeinvent  •  Sistema de gestión comercial",
                SwingConstants.CENTER
        );
        pie.setFont(fuente(Font.PLAIN, 12));
        pie.setForeground(LILA);
        fondo.add(pie, BorderLayout.SOUTH);
    }

    private JPanel crearTarjeta(String nombre, String descripcion, String rutaIcono,
                                Runnable accion, Color acento) {
        PanelRedondeado tarjeta = new PanelRedondeado(CREMA_CLARO, 28, acento);
        tarjeta.setLayout(new BorderLayout(0, 8));
        tarjeta.setBorder(new EmptyBorder(24, 26, 24, 26));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.setToolTipText("Abrir " + nombre);

        JLabel icono = new JLabel();
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(95, 95));
        URL recurso = getClass().getResource(rutaIcono);
        if (recurso != null) {
            ImageIcon original = new ImageIcon(recurso);
            int ancho = original.getIconWidth();
            int alto = original.getIconHeight();
            // Reducir solo cuando sea necesario y siempre conservar la proporción.
            if (ancho > 0 && alto > 0) {
                double escala = Math.min(1.0, Math.min(85.0 / ancho, 85.0 / alto));
                int w = Math.max(1, (int) Math.round(ancho * escala));
                int h = Math.max(1, (int) Math.round(alto * escala));
                icono.setIcon(escala < 1.0
                        ? new ImageIcon(original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH))
                        : original);
            }
        }

        JLabel titulo = new JLabel(nombre, SwingConstants.CENTER);
        titulo.setFont(fuente(Font.BOLD, 22));
        titulo.setForeground(NEGRO);
        JLabel detalle = new JLabel(descripcion, SwingConstants.CENTER);
        detalle.setFont(fuente(Font.PLAIN, 13));
        detalle.setForeground(GRIS);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(Box.createVerticalGlue());
        contenido.add(icono);
        contenido.add(Box.createVerticalStrut(11));
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(7));
        contenido.add(detalle);
        contenido.add(Box.createVerticalGlue());
        tarjeta.add(contenido, BorderLayout.CENTER);

        MouseAdapter eventos = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { tarjeta.setColor(HOVER); }
            @Override public void mouseExited(MouseEvent e) {
                // No quitar el hover mientras el cursor sigue dentro de la tarjeta.
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), tarjeta);
                if (!tarjeta.contains(p)) tarjeta.setColor(CREMA_CLARO);
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) accion.run();
            }
        };
        activarClicEnHijos(tarjeta, eventos);
        return tarjeta;
    }

    private void activarClicEnHijos(Component componente, MouseAdapter listener) {
        componente.addMouseListener(listener);
        if (componente instanceof Container contenedor) {
            for (Component hijo : contenedor.getComponents()) activarClicEnHijos(hijo, listener);
        }
    }

    // Se conservan las mismas ventanas y operaciones del menú original.
    private void abrirTienda() {
        try {
            Tienda tienda = new Tienda();
            tienda.setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir Tienda: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirInventario() {
        new GestionInventario().setVisible(true);
        dispose();
    }

    private void abrirRegistros() {
        new Registro().setVisible(true);
        dispose();
    }

    private void abrirProveedores() {
        new GestionProveedores().setVisible(true);
        dispose();
    }

    private Font fuente(int estilo, int tamano) {
        for (String preferida : new String[]{"Poppins", "Inter", "Segoe UI"}) {
            for (String instalada : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                if (preferida.equalsIgnoreCase(instalada)) return new Font(preferida, estilo, tamano);
            }
        }
        return new Font("SansSerif", estilo, tamano);
    }

    private static class PanelRedondeado extends JPanel {
        private Color color;
        private final int radio;
        private final Color acento;

        PanelRedondeado(Color color, int radio, Color acento) {
            this.color = color;
            this.radio = radio;
            this.acento = acento;
            setOpaque(false);
        }

        void setColor(Color nuevo) {
            this.color = nuevo;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo redondeado de la tarjeta.
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radio, radio));

            // Franja de color en la parte superior, recortada a la forma de la tarjeta.
            Shape forma = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radio, radio);
            Shape clipAnterior = g2.getClip();
            g2.clip(forma);
            g2.setColor(acento);
            g2.fillRect(0, 0, getWidth(), 6);
            g2.setClip(clipAnterior);

            // Borde fino.
            g2.setColor(BORDE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 1.2, getHeight() - 1.2, radio, radio));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
    }
}