package phongkham.gui.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.UIUtils;

public class HomePanel extends BasePanel {

  @Override
  protected void init() {

    JPanel stats = new JPanel(new GridLayout(1, 3, 12, 12));
    stats.setOpaque(false);
    stats.add(createCard("Guest", "Đặt lịch khám và mua thuốc nhanh"));
    stats.add(createCard("Bác sĩ", "Quản lý lịch khám và hồ sơ bệnh án"));
    stats.add(createCard("Quản trị", "Theo dõi dashboard và phân quyền"));

    JPanel center = new JPanel(new BorderLayout());
    center.setOpaque(false);
    center.add(stats, BorderLayout.NORTH);
    add(center, BorderLayout.CENTER);
  }

  private JPanel createCard(String title, String description) {
    JLabel label = new JLabel(
      "<html><b>" + title + "</b><br/>" + description + "</html>",
      SwingConstants.LEFT
    );
    label.setFont(UIConstants.FONT_BODY);

    JPanel section = UIUtils.createSection("Module", label);
    section.setPreferredSize(new java.awt.Dimension(380, 170));
    return section;
  }
}
