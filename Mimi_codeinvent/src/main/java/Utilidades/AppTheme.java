package Utilidades;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

public final class AppTheme {
    // Identidad visual de Mimi_codeinvent: crema, negro, morado y azul.
    // Se conservan los nombres antiguos para no romper las otras ventanas.
    public static final Color NEGRO = new Color(0x171717);
    public static final Color MORADO = new Color(0x673A8E);
    public static final Color LILA = new Color(0xB79AE4);
    public static final Color AZUL = new Color(0x5278B9);
    public static final Color AZUL_CLARO = new Color(0x91B8E8);
    public static final Color CREMA = new Color(0xFAF5EC);
    public static final Color CREMA_CLARO = new Color(0xFFFCF7);

    public static final Color AZUL_NOCHE = NEGRO;
    public static final Color AZUL_PROFUNDO = MORADO;
    public static final Color AZUL_PRINCIPAL = AZUL;
    public static final Color CELESTE = AZUL_CLARO;
    public static final Color AZUL_HIELO = new Color(0xE8EFFB);
    public static final Color FONDO_GENERAL = CREMA;
    public static final Color TARJETA = CREMA_CLARO;
    public static final Color TEXTO_PRINCIPAL = NEGRO;
    public static final Color TEXTO_SECUNDARIO = new Color(0x5A5752);
    public static final Color BORDE_SUAVE = new Color(0xDED6C9);

    private static final String[] FONT_PREFERENCES = {"Poppins", "Inter", "Segoe UI", "SansSerif"};
    private static boolean installed;

    private AppTheme() {}

    public static void installGlobalTheme() {
        if (installed) return;
        installed = true;
        UIManager.put("Panel.background", FONDO_GENERAL);
        UIManager.put("OptionPane.background", TARJETA);
        UIManager.put("OptionPane.messageForeground", TEXTO_PRINCIPAL);
        UIManager.put("TextField.background", TARJETA);
        UIManager.put("TextField.foreground", TEXTO_PRINCIPAL);
        UIManager.put("PasswordField.background", TARJETA);
        UIManager.put("PasswordField.foreground", TEXTO_PRINCIPAL);
        UIManager.put("Table.background", TARJETA);
        UIManager.put("Table.foreground", TEXTO_PRINCIPAL);
        UIManager.put("Table.selectionBackground", new Color(0xE8DAF4));
        UIManager.put("Table.selectionForeground", NEGRO);
        UIManager.put("Button.font", fontMedium(14f));
        UIManager.put("Label.font", fontRegular(14f));
        UIManager.put("TextField.font", fontRegular(14f));
        UIManager.put("PasswordField.font", fontRegular(14f));
        UIManager.put("TextArea.font", fontRegular(14f));
        UIManager.put("ComboBox.font", fontRegular(14f));
        UIManager.put("Table.font", fontRegular(13f));
        UIManager.put("TableHeader.font", fontSemiBold(13f));
        UIManager.put("CheckBox.font", fontRegular(13f));
        UIManager.put("ToolTip.font", fontRegular(12f));
    }

    public static Font fontRegular(float size) {
        return resolveFont(Font.PLAIN, size);
    }

    public static Font fontMedium(float size) {
        return resolveFont(Font.BOLD, size);
    }

    public static Font fontSemiBold(float size) {
        return resolveFont(Font.BOLD, size);
    }

    private static Font resolveFont(int style, float size) {
        String[] available = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String preferred : FONT_PREFERENCES) {
            if (Arrays.stream(available).anyMatch(f -> f.equalsIgnoreCase(preferred))) {
                return new Font(preferred, style, Math.round(size));
            }
        }
        return new Font("SansSerif", style, Math.round(size));
    }

    public static void applyRootPanel(JPanel panel) {
        if (panel == null) return;
        panel.setBackground(FONDO_GENERAL);
        if (panel.getBorder() == null) {
            panel.setBorder(new EmptyBorder(14, 14, 14, 14));
        }
    }

    public static void applyHeaderPanel(JPanel panel) {
        if (panel == null) return;
        panel.setBackground(NEGRO);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
    }

    public static void applyAccentPanel(JPanel panel) {
        if (panel == null) return;
        panel.setBackground(new Color(0xF0E6F8));
        panel.setBorder(new CompoundBorder(new RoundedBorder(BORDE_SUAVE, 16, 1), new EmptyBorder(10, 12, 10, 12)));
    }

    public static void applyCardPanel(JPanel panel) {
        if (panel == null) return;
        panel.setOpaque(true);
        panel.setBackground(TARJETA);
        panel.setBorder(new CompoundBorder(new RoundedBorder(BORDE_SUAVE, 18, 1), new EmptyBorder(12, 12, 12, 12)));
    }

    public static void applyTitle(JLabel label) {
        if (label == null) return;
        label.setFont(fontSemiBold(24f));
        label.setForeground(TEXTO_PRINCIPAL);
    }

    public static void applySectionTitle(JLabel label) {
        if (label == null) return;
        label.setFont(fontSemiBold(18f));
        label.setForeground(TEXTO_PRINCIPAL);
    }

    public static void applyBodyLabel(JLabel label) {
        if (label == null) return;
        label.setFont(fontRegular(14f));
        label.setForeground(TEXTO_SECUNDARIO);
    }

    public static void applyInverseLabel(JLabel label, float size) {
        if (label == null) return;
        label.setFont(fontSemiBold(size));
        label.setForeground(Color.WHITE);
    }

    public static void applyField(JTextComponent field) {
        if (field == null) return;
        field.setFont(fontRegular(14f));
        field.setForeground(TEXTO_PRINCIPAL);
        field.setBackground(TARJETA);
        field.setCaretColor(MORADO);
        field.setBorder(fieldBorder());
        field.setSelectionColor(new Color(0xE8DAF4));
        field.setSelectedTextColor(NEGRO);
    }

    public static void applyComboBox(JComboBox<?> comboBox) {
        if (comboBox == null) return;
        comboBox.setFont(fontRegular(14f));
        comboBox.setForeground(TEXTO_PRINCIPAL);
        comboBox.setBackground(TARJETA);
        comboBox.setBorder(fieldBorder());
    }

    public static void applyCheckBox(JCheckBox checkBox) {
        if (checkBox == null) return;
        checkBox.setFont(fontRegular(13f));
        checkBox.setForeground(TEXTO_SECUNDARIO);
        checkBox.setBackground(FONDO_GENERAL);
        checkBox.setFocusPainted(false);
    }

    public static void applyPrimaryButton(AbstractButton button) {
        if (button == null) return;
        button.setFont(fontSemiBold(14f));
        button.setForeground(Color.WHITE);
        button.setBackground(NEGRO);
        button.setBorder(new CompoundBorder(new RoundedBorder(NEGRO, 12, 1), new EmptyBorder(10, 18, 10, 18)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(button, NEGRO, MORADO);
    }

    public static void applySecondaryButton(AbstractButton button) {
        if (button == null) return;
        button.setFont(fontMedium(14f));
        button.setForeground(MORADO);
        button.setBackground(new Color(0xF0E6F8));
        button.setBorder(new CompoundBorder(new RoundedBorder(BORDE_SUAVE, 12, 1), new EmptyBorder(10, 18, 10, 18)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(button, new Color(0xF0E6F8), new Color(0xE8DAF4));
    }

    public static void applyMenuCard(JLabel label) {
        if (label == null) return;
        label.setOpaque(true);
        label.setBackground(TARJETA);
        label.setForeground(TEXTO_PRINCIPAL);
        label.setFont(fontSemiBold(22f));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setIconTextGap(14);
        label.setBorder(new CompoundBorder(new RoundedBorder(BORDE_SUAVE, 18, 1), new EmptyBorder(12, 14, 12, 14)));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(label, TARJETA, new Color(0xF0E6F8));
    }

    public static void applyTable(JTable table) {
        if (table == null) return;
        table.setFont(fontRegular(13f));
        table.setForeground(TEXTO_PRINCIPAL);
        table.setRowHeight(34);
        table.setGridColor(BORDE_SUAVE);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(0xE8DAF4));
        table.setSelectionForeground(TEXTO_PRINCIPAL);
        table.setBackground(TARJETA);
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(fontSemiBold(13f));
            header.setForeground(Color.WHITE);
            header.setBackground(MORADO);
            header.setBorder(BorderFactory.createLineBorder(BORDE_SUAVE));
            header.setReorderingAllowed(false);
        }
    }

    public static void applyScrollPane(JScrollPane scrollPane) {
        if (scrollPane == null) return;
        scrollPane.setBorder(new RoundedBorder(BORDE_SUAVE, 16, 1));
        scrollPane.getViewport().setBackground(TARJETA);
    }

    public static void applySeparator(JSeparator separator) {
        if (separator == null) return;
        separator.setForeground(BORDE_SUAVE);
        separator.setBackground(BORDE_SUAVE);
    }

    public static void setScaledIcon(JLabel label, String resourcePath, int width, int height) {
        if (label == null) return;
        java.net.URL url = AppTheme.class.getResource(resourcePath);
        if (url == null) return;
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }

    public static void styleGeneratedForm(Container root) {
        if (root == null) return;
        walk(root, true);
    }

    private static void walk(Component component, boolean root) {
        if (component instanceof JPanel panel) {
            if (root) {
                applyRootPanel(panel);
            } else if (same(panel.getBackground(), new Color(64, 64, 64))) {
                applyHeaderPanel(panel);
            } else if (same(panel.getBackground(), new Color(255, 204, 0))) {
                applyAccentPanel(panel);
            } else if (same(panel.getBackground(), Color.WHITE)) {
                panel.setBackground(TARJETA);
            } else if (same(panel.getBackground(), new Color(0xF5F8FC))) {
                panel.setBackground(FONDO_GENERAL);
            }
        }
        if (component instanceof JLabel label) {
            // No modificar imágenes PNG usadas como botones o logos.
            if (label.getIcon() != null) {
                label.setOpaque(false);
            } else if (label.getParent() != null
                    && same(label.getParent().getBackground(), NEGRO)) {
                applyInverseLabel(label, Math.max(13f,
                        label.getFont() == null ? 14f : label.getFont().getSize2D()));
            } else if (label.getFont() != null && label.getFont().getSize() >= 20) {
                applyTitle(label);
            } else if (label.getFont() != null && label.getFont().isBold()) {
                applySectionTitle(label);
            } else {
                applyBodyLabel(label);
            }
        }
        if (component instanceof JTextField field) {
            applyField(field);
        }
        if (component instanceof JTextArea textArea) {
            applyField(textArea);
        }
        if (component instanceof JComboBox<?> comboBox) {
            applyComboBox(comboBox);
        }
        if (component instanceof JButton button) {
            applyPrimaryButton(button);
        }
        if (component instanceof JCheckBox checkBox) {
            applyCheckBox(checkBox);
        }
        if (component instanceof JTable table) {
            applyTable(table);
        }
        if (component instanceof JScrollPane scrollPane) {
            applyScrollPane(scrollPane);
        }
        if (component instanceof JViewport viewport) {
            viewport.setBackground(TARJETA);
        }
        if (component instanceof JSeparator separator) {
            applySeparator(separator);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                walk(child, false);
            }
        }
    }

    private static Border fieldBorder() {
        return new CompoundBorder(new RoundedBorder(BORDE_SUAVE, 12, 1), new EmptyBorder(8, 12, 8, 12));
    }

    private static void installHover(JComponent component, Color normal, Color hover) {
        // Evita añadir listeners repetidos al aplicar el tema varias veces.
        String clave = "mimi.themeHover";
        Object anterior = component.getClientProperty(clave);
        if (anterior instanceof MouseAdapter) component.removeMouseListener((MouseAdapter) anterior);
        MouseAdapter listener = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { component.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { component.setBackground(normal); }
        };
        component.addMouseListener(listener);
        component.putClientProperty(clave, listener);
    }

    private static boolean same(Color a, Color b) {
        if (a == null || b == null) return false;
        return a.getRGB() == b.getRGB();
    }

    public static final class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), radius, radius);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 10, 8, 10);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 10;
            insets.top = insets.bottom = 8;
            return insets;
        }
    }
}
