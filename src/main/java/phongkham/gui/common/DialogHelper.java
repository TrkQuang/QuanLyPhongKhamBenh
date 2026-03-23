package phongkham.gui.common;

import java.awt.Component;
import javax.swing.JOptionPane;

public final class DialogHelper {

  private DialogHelper() {}

  public static void info(Component parent, String message) {
    JOptionPane.showMessageDialog(
      parent,
      message,
      "Thông báo",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  public static void warn(Component parent, String message) {
    JOptionPane.showMessageDialog(
      parent,
      message,
      "Cảnh báo",
      JOptionPane.WARNING_MESSAGE
    );
  }

  public static void error(Component parent, String message) {
    JOptionPane.showMessageDialog(
      parent,
      message,
      "Lỗi",
      JOptionPane.ERROR_MESSAGE
    );
  }

  public static boolean confirm(Component parent, String message) {
    int option = JOptionPane.showConfirmDialog(
      parent,
      message,
      "Xác nhận",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE
    );
    return option == JOptionPane.YES_OPTION;
  }
}
