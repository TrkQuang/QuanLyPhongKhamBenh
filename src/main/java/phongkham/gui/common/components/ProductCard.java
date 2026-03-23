package phongkham.gui.common.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import phongkham.gui.common.UIConstants;

public class ProductCard extends RoundedPanel {

  private final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(
    new Locale("vi", "VN")
  );

  public ProductCard(
    String tenThuoc,
    double gia,
    String loai,
    Runnable onAddCart
  ) {
    super(18);
    setLayout(new BorderLayout(10, 10));
    setBackground(UIConstants.BG_SURFACE);
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    setPreferredSize(new Dimension(220, 220));

    JPanel image = new RoundedPanel(14);
    image.setBackground(new Color(226, 232, 240));
    image.setPreferredSize(new Dimension(180, 90));
    image.setLayout(new BorderLayout());
    JLabel icon = new JLabel("MED", JLabel.CENTER);
    icon.setForeground(new Color(71, 85, 105));
    icon.setFont(new Font("Segoe UI", Font.BOLD, 16));
    image.add(icon, BorderLayout.CENTER);

    JLabel lblTen = new JLabel(tenThuoc);
    lblTen.setFont(UIConstants.FONT_BODY_BOLD);

    JLabel lblLoai = new JLabel(loai);
    lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblLoai.setForeground(UIConstants.TEXT_MUTED);

    JLabel lblGia = new JLabel(moneyFormat.format(gia));
    lblGia.setFont(new Font("Segoe UI", Font.BOLD, 15));
    lblGia.setForeground(UIConstants.PRIMARY_DARK);

    CustomButton btnAdd = new CustomButton(
      "Thêm vào giỏ",
      UIConstants.PRIMARY,
      new Color(29, 78, 216),
      new Color(30, 64, 175),
      12
    );
    btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnAdd.addActionListener(e -> onAddCart.run());

    JPanel info = new JPanel();
    info.setOpaque(false);
    info.setLayout(
      new javax.swing.BoxLayout(info, javax.swing.BoxLayout.Y_AXIS)
    );
    info.add(lblTen);
    info.add(javax.swing.Box.createVerticalStrut(2));
    info.add(lblLoai);
    info.add(javax.swing.Box.createVerticalStrut(8));
    info.add(lblGia);

    add(image, BorderLayout.NORTH);
    add(info, BorderLayout.CENTER);
    add(btnAdd, BorderLayout.SOUTH);
  }
}
