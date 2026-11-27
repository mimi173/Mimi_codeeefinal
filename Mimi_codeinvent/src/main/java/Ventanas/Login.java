package Ventanas;

import Utilidades.Autenticacion;
import Utilidades.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;

/** Login de Mimi_codeinvent: Swing + fondo PNG + formulario crema. */
public class Login extends JFrame {

    private static final Color CREMA = new Color(0xFAF5EC);
    private static final Color CREMA_CLARO = new Color(0xFFFCF7);
    private static final Color NEGRO = new Color(0x171717);
    private static final Color GRIS = new Color(0x5A5752);
    private static final Color BORDE = new Color(0xDED6C9);
    private static final int RADIO = 34;

    private final JTextField TXT_USUARIO = new JTextField();
    private final JPasswordField TXT_CONTRASENA = new JPasswordField();
    private final JCheckBox COP_MOSTRAR_CONTRA = new JCheckBox("Mostrar contraseña");
    private final JButton BTN_INGRESAR = new JButton("INICIAR SESIÓN");
    private final char caracterOriginal = TXT_CONTRASENA.getEchoChar();
    private Point origenArrastre;

    public Login() {
        configurarVentana();
        construirInterfaz();
        configurarEventos();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(TXT_USUARIO::requestFocusInWindow);
    }

    private void configurarVentana() {
        setTitle("Mimi Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(960, 560);
        setMinimumSize(new Dimension(960, 560));
        setResizable(false);
        try {
            setBackground(new Color(0, 0, 0, 0)); // Transparencia de las esquinas.
        } catch (UnsupportedOperationException ignored) {
            setBackground(CREMA);
        }
        URL logo = recurso("/Recursos/LOGO_BASE.png");
        if (logo != null) setIconImage(new ImageIcon(logo).getImage());
    }

    private void construirInterfaz() {
        JPanel exterior = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CREMA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO, RADIO);
                g2.dispose();
            }
        };
        exterior.setOpaque(false);
        exterior.setBorder(new EmptyBorder(9, 9, 9, 9));
        setContentPane(exterior);

        // ZONA IZQUIERDA: imagen elegida por ti y cabecera con fondo propio.
        JPanel zonaImagen = crearZonaImagen();
        zonaImagen.setPreferredSize(new Dimension(440, 540));
        exterior.add(zonaImagen, BorderLayout.WEST);

        // ZONA DERECHA: formulario crema, tipografía y acciones negras.
        JPanel zonaFormulario = new JPanel(new BorderLayout());
        zonaFormulario.setBackground(CREMA);
        zonaFormulario.setOpaque(true);
        zonaFormulario.setBorder(new EmptyBorder(22, 45, 30, 45));
        exterior.add(zonaFormulario, BorderLayout.CENTER);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JLabel etiquetaSistema = new JLabel("");
        etiquetaSistema.setFont(fuente(Font.BOLD, 12));
        etiquetaSistema.setForeground(GRIS);
        barra.add(etiquetaSistema, BorderLayout.WEST);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controles.setOpaque(false);
        JButton minimizar = crearControlVentana("−");
        JButton cerrar = crearControlVentana("×");
        minimizar.addActionListener(e -> setState(JFrame.ICONIFIED));
        cerrar.addActionListener(e -> dispose());
        controles.add(minimizar);
        controles.add(cerrar);
        barra.add(controles, BorderLayout.EAST);
        zonaFormulario.add(barra, BorderLayout.NORTH);

        JPanel centrador = new JPanel(new GridBagLayout());
        centrador.setOpaque(false);
        zonaFormulario.add(centrador, BorderLayout.CENTER);

        JPanel tarjeta = new PanelRedondeado(CREMA_CLARO, 28);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(30, 32, 32, 32));
        tarjeta.setPreferredSize(new Dimension(395, 410));
        centrador.add(tarjeta);

        JLabel titulo = new JLabel("Bienvenido");
        titulo.setFont(fuente(Font.BOLD, 31));
        titulo.setForeground(NEGRO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Ingresa tus datos para continuar.");
        subtitulo.setFont(fuente(Font.PLAIN, 13));
        subtitulo.setForeground(GRIS);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        estiloCampo(TXT_USUARIO);
        estiloCampo(TXT_CONTRASENA);
        configurarAltura(TXT_USUARIO, 44);
        configurarAltura(TXT_CONTRASENA, 44);

        COP_MOSTRAR_CONTRA.setOpaque(false);
        COP_MOSTRAR_CONTRA.setFont(fuente(Font.PLAIN, 12));
        COP_MOSTRAR_CONTRA.setForeground(NEGRO);
        COP_MOSTRAR_CONTRA.setFocusPainted(false);
        COP_MOSTRAR_CONTRA.setAlignmentX(Component.LEFT_ALIGNMENT);

        BTN_INGRESAR.setBackground(NEGRO);
        BTN_INGRESAR.setForeground(Color.WHITE);
        BTN_INGRESAR.setFont(fuente(Font.BOLD, 13));
        BTN_INGRESAR.setFocusPainted(false);
        BTN_INGRESAR.setBorderPainted(false);
        BTN_INGRESAR.setOpaque(true);
        BTN_INGRESAR.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        configurarAltura(BTN_INGRESAR, 46);
        BTN_INGRESAR.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(23));
        tarjeta.add(etiqueta("USUARIO"));
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(TXT_USUARIO);
        tarjeta.add(Box.createVerticalStrut(15));
        tarjeta.add(etiqueta("CONTRASEÑA"));
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(TXT_CONTRASENA);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(COP_MOSTRAR_CONTRA);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(BTN_INGRESAR);

        getRootPane().setDefaultButton(BTN_INGRESAR);
        instalarArrastre(exterior);
        instalarArrastre(barra);
        instalarArrastre(etiquetaSistema);
    }

    private JPanel crearZonaImagen() {
        URL urlFondo = recurso("/Recursos/FONDO_LOGIN.png");
        final Image fondo = urlFondo == null ? null : new ImageIcon(urlFondo).getImage();
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Double(0, 0,
                        getWidth(), getHeight(), 26, 26));
                g2.setColor(new Color(0xE8DDD0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (fondo != null && fondo.getWidth(null) > 0 && fondo.getHeight(null) > 0) {
                    // Recorte proporcional: la imagen no se estira ni se deforma.
                    double escala = Math.max((double) getWidth() / fondo.getWidth(null),
                            (double) getHeight() / fondo.getHeight(null));
                    int ancho = (int) Math.ceil(fondo.getWidth(null) * escala);
                    int alto = (int) Math.ceil(fondo.getHeight(null) * escala);
                    g2.drawImage(fondo, (getWidth() - ancho) / 2,
                            (getHeight() - alto) / 2, ancho, alto, this);
                } else {
                    g2.setFont(fuente(Font.PLAIN, 13));
                    g2.setColor(GRIS);
                    g2.drawString("Agrega Recursos/FONDO_LOGIN.png", 32, getHeight() / 2);
                }
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));



        // Recuadro morado de inicio de sesión


        JPanel cabecera = new PanelRedondeado(
                new Color(23, 23, 23), 18
        );

        cabecera.setLayout(
                new FlowLayout(FlowLayout.CENTER, 12, 7)
        );

        cabecera.setBackground(new Color(23, 23, 23));
        cabecera.setOpaque(false);

        JLabel login = new JLabel("Your Inventory, Simplified");
        login.setFont(fuente(Font.BOLD, 15));
        login.setForeground(Color.WHITE);

        cabecera.add(login);



        // Contenedor transparente para centrar el recuadro
        JPanel contenedor = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 10)
        );
        contenedor.setOpaque(false);
        contenedor.add(cabecera);

        // Agregar cabecera a la zona izquierda
        panel.add(contenedor, BorderLayout.NORTH);

        // Permitir arrastrar la ventana
        instalarArrastre(panel);
        instalarArrastre(cabecera);
        instalarArrastre(login);

        return panel;
    }


    private void estiloCampo(JTextField campo) {
        campo.setText("");
        campo.setEditable(true);
        campo.setEnabled(true);
        campo.setFocusable(true);
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.setForeground(NEGRO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(NEGRO);
        campo.setSelectionColor(new Color(0xF2E8D9));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void configurarAltura(JComponent comp, int alto) {
        comp.setPreferredSize(new Dimension(320, alto));
        comp.setMinimumSize(new Dimension(100, alto));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.BOLD, 12));
        label.setForeground(NEGRO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton crearControlVentana(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 21));
        boton.setForeground(NEGRO);
        boton.setBackground(CREMA);
        boton.setBorder(new EmptyBorder(0, 7, 2, 7));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private Font fuente(int estilo, int tamano) {
        String[] nombres = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        for (String candidata : new String[]{"Inter", "Segoe UI"}) {
            for (String nombre : nombres) {
                if (nombre.equalsIgnoreCase(candidata))
                    return new Font(candidata, estilo, tamano);
            }
        }
        return new Font("SansSerif", estilo, tamano);
    }

    private URL recurso(String ruta) {
        return getClass().getResource(ruta);
    }

    private void instalarArrastre(Component componente) {
        MouseAdapter listener = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                origenArrastre = e.getLocationOnScreen();
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (origenArrastre == null) return;
                Point actual = e.getLocationOnScreen();
                Point ventana = getLocation();
                setLocation(ventana.x + actual.x - origenArrastre.x,
                        ventana.y + actual.y - origenArrastre.y);
                origenArrastre = actual;
            }
            @Override public void mouseReleased(MouseEvent e) { origenArrastre = null; }
        };
        componente.addMouseListener(listener);
        componente.addMouseMotionListener(listener);
    }

    private void configurarEventos() {
        COP_MOSTRAR_CONTRA.addActionListener(e ->
                TXT_CONTRASENA.setEchoChar(COP_MOSTRAR_CONTRA.isSelected()
                        ? (char) 0 : caracterOriginal));
        BTN_INGRESAR.addActionListener(e -> ingresar());
        BTN_INGRESAR.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                BTN_INGRESAR.setBackground(new Color(0x333333));
            }
            @Override public void mouseExited(MouseEvent e) {
                BTN_INGRESAR.setBackground(NEGRO);
            }
        });
    }

    private void ingresar() {
        String usuario = TXT_USUARIO.getText().trim();
        char[] clave = TXT_CONTRASENA.getPassword();
        try {
            if (usuario.isEmpty() || clave.length == 0) {
                JOptionPane.showMessageDialog(this, "Ingresa tu usuario y contraseña.",
                        "Campos obligatorios", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BTN_INGRESAR.setEnabled(false);
            String nombre = Autenticacion.ingresar(usuario, clave);
            if (nombre != null) {
                Utils.UTR(nombre);
                new Menu().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                TXT_CONTRASENA.setText("");
                TXT_CONTRASENA.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No fue posible abrir la base de datos: " + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(clave, '\0');
            BTN_INGRESAR.setEnabled(true);
        }
    }

    /** JPanel con fondo propio y esquinas realmente redondeadas. */
    private static class PanelRedondeado extends JPanel {
        private final Color fondo;
        private final int radio;

        PanelRedondeado(Color fondo, int radio) {
            this.fondo = fondo;
            this.radio = radio;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground().getAlpha() < 255 ? getBackground() : fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
