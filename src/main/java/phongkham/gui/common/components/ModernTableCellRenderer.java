package phongkham.gui.common.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ModernTableCellRenderer extends DefaultTableCellRenderer {

  private int hoverRow = -1;

  public void setHoverRow(int hoverRow) {
    this.hoverRow = hoverRow;
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
    super.getTableCellRendererComponent(
      table,
      value,
      isSelected,
      hasFocus,
      row,
      column
    );

    if (isSelected) {
      setBackground(new Color(219, 234, 254));
      setForeground(new Color(30, 64, 175));
    } else if (row == hoverRow) {
      setBackground(new Color(241, 245, 249));
      setForeground(new Color(15, 23, 42));
    } else {
      setBackground(Color.WHITE);
      setForeground(new Color(15, 23, 42));
    }
    setBorder(noFocusBorder);
    return this;
  }
}
