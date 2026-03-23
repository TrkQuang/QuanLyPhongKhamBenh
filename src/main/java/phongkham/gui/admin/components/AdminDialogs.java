package phongkham.gui.admin.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import phongkham.gui.common.UIUtils;

public final class AdminDialogs {

  @FunctionalInterface
  public interface SaveHandler {
    boolean onSave();
  }

  private AdminDialogs() {}

  public static void showFormDialog(
    Component parent,
    String title,
    JPanel formPanel,
    SaveHandler saveHandler,
    int width,
    int height
  ) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(parent),
      title,
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel content = UIUtils.createSection(title, formPanel);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    actions.setOpaque(false);
    JButton btnClose = UIUtils.ghostButton("Đóng");
    JButton btnSave = UIUtils.primaryButton("Lưu");

    btnClose.addActionListener(e -> dialog.dispose());
    btnSave.addActionListener(e -> {
      if (saveHandler == null || saveHandler.onSave()) {
        dialog.dispose();
      }
    });

    actions.add(btnClose);
    actions.add(btnSave);

    dialog.add(content, BorderLayout.CENTER);
    dialog.add(actions, BorderLayout.SOUTH);
    dialog.setSize(width, height);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  public static void showViewDialog(
    Component parent,
    String title,
    JPanel body,
    int width,
    int height
  ) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(parent),
      title,
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel content = UIUtils.createSection(title, body);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    actions.setOpaque(false);
    JButton btnClose = UIUtils.ghostButton("Đóng");
    btnClose.addActionListener(e -> dialog.dispose());
    actions.add(btnClose);

    dialog.add(content, BorderLayout.CENTER);
    dialog.add(actions, BorderLayout.SOUTH);
    dialog.setSize(width, height);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }
}
