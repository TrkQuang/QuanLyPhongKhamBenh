package phongkham.gui.common.components;

import java.awt.Color;

public class ShadowPanel extends RoundedPanel {

  public ShadowPanel() {
    this(18);
  }

  public ShadowPanel(int radius) {
    super(radius);
    setBackground(Color.WHITE);
    setDrawShadow(true);
  }
}
