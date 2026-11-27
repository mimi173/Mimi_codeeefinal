package Ventanas;

import Utilidades.AppTheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Proveedor de Mimi_codeinvent.
 * Misma paleta y estilo que el Menú, la Tienda, Registro y Stock (crema, negro, lila y azul),
 * ventana amplia y redimensionable.
 */
public class Proveedor extends JFrame {

    // ===== Paleta (la misma del Menú) =====
    private static final Color CREMA = new Color(0xFAF5EC);
    private static final Color CREMA_CLARO = new Color(0xFFFCF7);
    private static final Color NEGRO = new Color(0x171717);
    private static final Color GRIS = new Color(0x5A5752);
    private static final Color BORDE = new Color(0xDED6C9);
    private static final Color HOVER = new Color(0xF2E8D9);
    private static final Color LILA = new Color(183, 154, 228);
    private static final Color AZUL = new Color(145, 184, 232);
    private static final Color LECTURA = new Color(0xF6F0E4);
    private static final Color FILA_ALTERNA = new Color(0xFBF7EF);

    // ===== Componentes =====
    private Campo TXT_RUC, TXT_RAZON_SOCIAL, TXT_TIPO;
    private JTable TBL_PROVEEDORES;
    private Boton BTN_VOLVER, BTN_RESET, BTN_AGREGAR, BTN_ENTREGAS;

    public Proveedor() {
        AppTheme.installGlobalTheme();
        configurarVentana();
        construirInterfaz();
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  VENTANA E INTERFAZ
    // =====================================================================

    private void configurarVentana() {
        setTitle("Mimi_codeinvent | Proveedor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ventana amplia, pero sin pasarse de la pantalla.
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.min(1360, pantalla.width - 60);
        int h = Math.min(860, pantalla.height - 60);
        setSize(w, h);
        setMinimumSize(new Dimension(Math.min(1100, w), Math.min(700, h)));
        setResizable(true);

        URL icono = getClass().getResource("/Recursos/LOGO_BASE.png");
        if (icono != null) setIconImage(new ImageIcon(icono).getImage());

        // Cerrar con la X también regresa al menú.
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                new Menu().setVisible(true);
            }
        });
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new BorderLayout(0, 20));
        fondo.setBackground(CREMA);
        fondo.setBorder(new EmptyBorder(26, 40, 26, 40));
        setContentPane(fondo);

        fondo.add(construirEncabezado(), BorderLayout.NORTH);

        // Formulario a la izquierda (angosto), tabla a la derecha.
        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setOpaque(false);
        izquierda.add(construirTarjetaFormulario(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;

        g.gridx = 0; g.weightx = 0.36;
        g.insets = new Insets(0, 0, 0, 24);
        cuerpo.add(izquierda, g);

        g.gridx = 1; g.weightx = 0.64;
        g.insets = new Insets(0, 0, 0, 0);
        cuerpo.add(construirTarjetaTabla(), g);

        fondo.add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(22, 0));
        encabezado.setOpaque(false);

        // Botón de retroceso al menú inicial.
        BTN_VOLVER = new Boton("\u2190  Men\u00FA", CREMA_CLARO, HOVER, NEGRO, true);
        BTN_VOLVER.setToolTipText("Volver al menú principal");
        BTN_VOLVER.addActionListener(e -> volverAlMenu());
        encabezado.add(centrado(BTN_VOLVER), BorderLayout.WEST);

        JLabel titulo = titulo("Proveedor", 30);
        JLabel subtitulo = new JLabel("Gestionar proveedores y productos");
        subtitulo.setFont(fuente(Font.PLAIN, 16));
        subtitulo.setForeground(GRIS);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(subtitulo);
        encabezado.add(textos, BorderLayout.CENTER);

        BTN_RESET = new Boton("Reset", CREMA_CLARO, HOVER, NEGRO, true);
        BTN_RESET.setToolTipText("Limpiar el formulario");
        BTN_RESET.addActionListener(e -> Reset());
        encabezado.add(centrado(BTN_RESET), BorderLayout.EAST);

        return encabezado;
    }

    private PanelRedondeado construirTarjetaFormulario() {
        PanelRedondeado tarjeta = tarjeta(LILA);
        tarjeta.setLayout(new GridBagLayout());

        TXT_RUC = new Campo("Ingrese su RUC");
        TXT_RAZON_SOCIAL = new Campo("Ingrese su Razón Social");
        TXT_TIPO = new Campo("Ingrese su Tipo");

        colocar(tarjeta, titulo("Proveedor", 20), 0, 0, 0);
        colocar(tarjeta, etiqueta("RUC"), 0, 1, 16);
        colocar(tarjeta, TXT_RUC, 0, 2, 4);
        colocar(tarjeta, etiqueta("Razón Social"), 0, 3, 14);
        colocar(tarjeta, TXT_RAZON_SOCIAL, 0, 4, 4);
        colocar(tarjeta, etiqueta("Tipo"), 0, 5, 14);
        colocar(tarjeta, TXT_TIPO, 0, 6, 4);

        // TODO: conectar el guardado del proveedor (el diseño original no tenía acción).
        BTN_AGREGAR = new Boton("Agregar proveedor", NEGRO, new Color(0x3A3A3A), Color.WHITE, false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 7;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(20, 0, 0, 0);
        tarjeta.add(BTN_AGREGAR, g);

        return tarjeta;
    }

    private PanelRedondeado construirTarjetaTabla() {
        PanelRedondeado tarjeta = tarjeta(AZUL);
        tarjeta.setLayout(new BorderLayout(0, 14));

        tarjeta.add(titulo("Proveedores", 20), BorderLayout.NORTH);

        TBL_PROVEEDORES = crearTabla();
        JScrollPane scroll = new JScrollPane(TBL_PROVEEDORES);
        scroll.setBorder(new LineBorder(BORDE, 1, true));
        scroll.getViewport().setBackground(CREMA_CLARO);
        tarjeta.add(scroll, BorderLayout.CENTER);

        // TODO: conectar el registro de entregas (el diseño original no tenía acción).
        BTN_ENTREGAS = new Boton("Registrar entregas", NEGRO, new Color(0x3A3A3A), Color.WHITE, false);
        BTN_ENTREGAS.setFont(fuente(Font.BOLD, 16));
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);
        pie.add(BTN_ENTREGAS);
        tarjeta.add(pie, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JTable crearTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{}, new String[]{"RUC", "Razón Social", "Tipo"}) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setFont(fuente(Font.PLAIN, 14));
        tabla.setForeground(NEGRO);
        tabla.setBackground(CREMA_CLARO);
        tabla.setRowHeight(36);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(LILA);
        tabla.setSelectionForeground(NEGRO);
        tabla.setFillsViewportHeight(true);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object valor,
                                                                     boolean seleccionada, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setForeground(NEGRO);
                setBackground(seleccionada ? LILA : (fila % 2 == 0 ? CREMA_CLARO : FILA_ALTERNA));
                return this;
            }
        });

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object valor,
                                                                     boolean seleccionada, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setOpaque(true);
                setBackground(NEGRO);
                setForeground(Color.WHITE);
                setFont(fuente(Font.BOLD, 13));
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);

        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { tabla.clearSelection(); }
        });
        return tabla;
    }

    // =====================================================================
    //  ACCIONES
    // =====================================================================

    private void Reset() {
        TXT_RUC.setText("");
        TXT_RAZON_SOCIAL.setText("");
        TXT_TIPO.setText("");
    }

    /** Cierra Proveedor y regresa al menú inicial. */
    private void volverAlMenu() {
        new Menu().setVisible(true);
        dispose();
    }

    // =====================================================================
    //  UTILIDADES DE INTERFAZ
    // =====================================================================

    private PanelRedondeado tarjeta(Color acento) {
        PanelRedondeado t = new PanelRedondeado(CREMA_CLARO, 28, acento);
        t.setBorder(new EmptyBorder(24, 26, 22, 26));
        return t;
    }

    private JLabel titulo(String texto, int tamano) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, tamano));
        l.setForeground(NEGRO);
        return l;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 12));
        l.setForeground(GRIS);
        return l;
    }

    private JPanel centrado(Component c) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private void colocar(Container panel, Component c, int x, int y, int arriba) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(arriba, 0, 0, 0);
        panel.add(c, g);
    }

    private static String familiaFuente;

    private static Font fuente(int estilo, int tamano) {
        if (familiaFuente == null) {
            familiaFuente = "SansSerif";
            Set<String> instaladas = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String preferida : new String[]{"Poppins", "Inter", "Segoe UI"}) {
                if (instaladas.contains(preferida)) { familiaFuente = preferida; break; }
            }
        }
        return new Font(familiaFuente, estilo, tamano);
    }

    // =====================================================================
    //  COMPONENTES PERSONALIZADOS
    // =====================================================================

    /** Tarjeta redondeada con franja de color arriba (igual que en el Menú). */
    private static class PanelRedondeado extends JPanel {
        private final Color color;
        private final int radio;
        private final Color acento;

        PanelRedondeado(Color color, int radio, Color acento) {
            this.color = color;
            this.radio = radio;
            this.acento = acento;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape forma = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radio, radio);
            g2.setColor(color);
            g2.fill(forma);

            Shape clipAnterior = g2.getClip();
            g2.clip(forma);
            g2.setColor(acento);
            g2.fillRect(0, 0, getWidth(), 6);
            g2.setClip(clipAnterior);

            g2.setColor(BORDE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 1.2, getHeight() - 1.2, radio, radio));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botón redondeado con efecto hover. */
    private static class Boton extends JButton {
        private final Color base;
        private final Color hover;
        private final boolean conBorde;
        private boolean encima;

        Boton(String texto, Color base, Color hover, Color colorTexto, boolean conBorde) {
            super(texto);
            this.base = base;
            this.hover = hover;
            this.conBorde = conBorde;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(colorTexto);
            setFont(fuente(Font.BOLD, 14));
            setBorder(new EmptyBorder(11, 24, 11, 24));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = !isEnabled() ? new Color(0xE9E3D8) : (encima ? hover : base);
            int arco = Math.min(getHeight(), 26);
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arco, arco));
            if (conBorde) {
                g2.setColor(BORDE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 1.2, getHeight() - 1.2, arco, arco));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Campo de texto con borde redondeado y texto de ayuda (placeholder). */
    private static class Campo extends JTextField {
        private final String ayuda;
        private final Border normal =
                BorderFactory.createCompoundBorder(new LineBorder(BORDE, 1, true), new EmptyBorder(8, 12, 8, 12));
        private final Border enfocado =
                BorderFactory.createCompoundBorder(new LineBorder(LILA, 2, true), new EmptyBorder(7, 11, 7, 11));

        Campo(String ayuda) {
            this.ayuda = ayuda;
            setFont(fuente(Font.PLAIN, 14));
            setForeground(NEGRO);
            setDisabledTextColor(GRIS);
            setCaretColor(NEGRO);
            setSelectionColor(LILA);
            setBackground(Color.WHITE);
            setBorder(normal);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (isEditable()) setBorder(enfocado);
                    repaint();
                }
                @Override public void focusLost(FocusEvent e) {
                    setBorder(normal);
                    repaint();
                }
            });
        }

        @Override public void setEnabled(boolean activo) {
            super.setEnabled(activo);
            setBackground(activo && isEditable() ? Color.WHITE : LECTURA);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner() && ayuda != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(0x9A958C));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2.getFontMetrics();
                Insets in = getInsets();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(ayuda, in.left, y);
                g2.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Proveedor().setVisible(true));
    }
}