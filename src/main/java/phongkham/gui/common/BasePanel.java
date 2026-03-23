package phongkham.gui.common;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class BasePanel extends JPanel {

  private boolean initialized;

  protected BasePanel() {
    setLayout(new BorderLayout(16, 16));
    setBackground(UIConstants.BG_APP);
    setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
  }

  @Override
  public void addNotify() {
    super.addNotify();
    if (!initialized) {
      initialized = true;
      init();
    }
  }

  protected abstract void init();
}
