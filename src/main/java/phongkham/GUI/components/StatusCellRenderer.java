package phongkham.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Function;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer dùng chung cho cột trạng thái để tô màu theo giá trị hiển thị.
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {

  private final Function<String, Color> colorResolver;

  public StatusCellRenderer(Function<String, Color> colorResolver) {
    this.colorResolver = colorResolver;
  }

  @Override
  public Component getTableCellRendererComponent(
    JTable table,
    Object value,
    boolean isSelected,
    boolean hasFocus,
    int row,
    int column
  ) {
    Component c = super.getTableCellRendererComponent(
      table,
      value,
      isSelected,
      hasFocus,
      row,
      column
    );
    setHorizontalAlignment(SwingConstants.CENTER);
    if (!isSelected) {
      String text = value == null ? "" : value.toString();
      c.setForeground(colorResolver.apply(text));
    }
    return c;
  }
}
