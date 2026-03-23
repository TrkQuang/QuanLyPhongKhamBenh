package phongkham.gui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import phongkham.gui.common.components.CustomButton;
import phongkham.gui.common.components.CustomTextField;
import phongkham.gui.common.components.ModernTableCellRenderer;
import phongkham.gui.common.components.RoundedPanel;

public final class UIUtils {

  private UIUtils() {}

  public static JPanel createPageHeader(String title, String subtitle) {
    JPanel wrapper = new JPanel(new BorderLayout(0, 6));
    wrapper.setOpaque(false);

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(UIConstants.FONT_TITLE);
    lblTitle.setForeground(UIConstants.TEXT_MAIN);

    JLabel lblSubtitle = new JLabel(subtitle);
    lblSubtitle.setFont(UIConstants.FONT_BODY);
    lblSubtitle.setForeground(UIConstants.TEXT_MUTED);

    wrapper.add(lblTitle, BorderLayout.NORTH);
    wrapper.add(lblSubtitle, BorderLayout.CENTER);
    return wrapper;
  }

  public static JPanel createSection(String title, Component content) {
    JPanel panel = new RoundedPanel(18);
    panel.setLayout(new BorderLayout(0, 12));
    panel.setBackground(UIConstants.BG_SURFACE);
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(UIConstants.FONT_HEADER);
    lblTitle.setForeground(UIConstants.TEXT_MAIN);

    panel.add(lblTitle, BorderLayout.NORTH);
    panel.add(content, BorderLayout.CENTER);
    return panel;
  }

  public static JButton primaryButton(String text) {
    JButton btn = new CustomButton(
      text,
      UIConstants.PRIMARY,
      new Color(37, 99, 235),
      new Color(29, 78, 216),
      12
    );
    btn.setFont(UIConstants.FONT_BODY_BOLD);
    return btn;
  }

  public static JButton ghostButton(String text) {
    JButton btn = new JButton(text);
    btn.setFont(UIConstants.FONT_BODY);
    btn.setFocusPainted(false);
    btn.setBackground(Color.WHITE);
    btn.setForeground(UIConstants.TEXT_MAIN);
    btn.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BORDER),
        BorderFactory.createEmptyBorder(10, 18, 10, 18)
      )
    );
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return btn;
  }

  public static JTextField roundedTextField(String placeholder, int columns) {
    CustomTextField field = new CustomTextField(placeholder, columns);
    field.setFont(UIConstants.FONT_BODY);
    return field;
  }

  public static void styleTable(JTable table) {
    table.setRowHeight(34);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setShowVerticalLines(false);
    table.setShowHorizontalLines(false);
    table.setGridColor(new Color(241, 245, 249));
    table.setFont(UIConstants.FONT_BODY);

    JTableHeader header = table.getTableHeader();
    header.setBackground(UIConstants.TABLE_HEADER_BG);
    header.setForeground(UIConstants.TEXT_MAIN);
    header.setFont(UIConstants.FONT_BODY_BOLD);
    header.setBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER)
    );

    ModernTableCellRenderer renderer = new ModernTableCellRenderer();
    for (int i = 0; i < table.getColumnCount(); i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    table.addMouseMotionListener(
      new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
          renderer.setHoverRow(table.rowAtPoint(e.getPoint()));
          table.repaint();
        }
      }
    );
    table.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
          renderer.setHoverRow(-1);
          table.repaint();
        }
      }
    );
  }

  public static JPanel row(Component... components) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    row.setOpaque(false);
    for (Component component : components) {
      row.add(component);
    }
    return row;
  }

  public static void fixedSize(Component component, int width, int height) {
    Dimension size = new Dimension(width, height);
    component.setPreferredSize(size);
    component.setMinimumSize(size);
    component.setMaximumSize(size);
  }
}
