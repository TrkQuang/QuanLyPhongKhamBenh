package phongkham;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import phongkham.GUI.LoginForm;

public class Main {

  public static void main(String[] args) {
    // Sử dụng FlatLaf Look and Feel
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Chạy ứng dụng
    SwingUtilities.invokeLater(() -> {
      LoginForm loginForm = new LoginForm();
      loginForm.setVisible(true);
    });
  }
}
