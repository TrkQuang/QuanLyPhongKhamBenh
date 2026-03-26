package phongkham.gui.common.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class SidebarIcons {

  private static final int SIZE = 18;
  private static final String CLASSPATH_BASE = "/phongkham/gui/img/";
  private static final String FILESYSTEM_BASE = "src/main/java/phongkham/gui/img/";
  private static final Map<String, Icon> CACHE = new HashMap<>();

  private SidebarIcons() {}

  public static Icon forRoute(String route) {
    if (route == null || route.trim().isEmpty()) {
      return fallback("..");
    }
    return CACHE.computeIfAbsent(route, SidebarIcons::createIconByRoute);
  }

  private static Icon createIconByRoute(String route) {
    String key = route.toUpperCase();
    String fileName = resolveFileNameByRoute(key);
    Icon imageIcon = loadImageIcon(fileName);
    if (imageIcon != null) {
      return imageIcon;
    }
    return fallback(resolveFallbackLabel(key));
  }

  private static String resolveFileNameByRoute(String key) {
    if (key.contains("DASHBOARD")) return "Dashboard.png";
    if (key.contains("QL_QUAN_LY")) return "QuanLy.png";
    if (key.contains("QL_TAI_KHOAN")) return "QuanLy.png";
    if (key.contains("QL_ROLE")) return "QuanLy.png";
    if (key.contains("PHAN_QUYEN")) return "QuanLy.png";
    if (key.contains("QL_BAC_SI")) return "QuanLyBacSi.png";
    if (key.contains("QL_DUYET_LICH_LAM")) return "DuyetLichLam.png";
    if (key.contains("QL_KHOA")) return "QuanLyKhoa.png";
    if (key.contains("QL_GOI_DICH_VU")) return "GoiDichVu.png";

    if (key.contains("BACSI_LICH_LAM_VIEC")) return "LichLamViec.png";
    if (key.contains("BACSI_LICH_KHAM")) return "LichKham.png";
    if (key.contains("BACSI_HOA_DON_KHAM")) return "HoaDonKham.png";
    if (key.contains("BACSI_BENH_AN")) return "BenhAn.png";
    if (key.contains("BACSI_PROFILE")) return "Profile.png";

    if (key.contains("THUOC")) return "QuanLyThuoc.png";
    if (key.contains("NHA_CUNG_CAP")) return "NhaCungCap.png";
    if (key.contains("PHIEU_NHAP")) return "PhieuNhap.png";
    if (key.contains("HOA_DON_THUOC")) return "HoaDonThuoc.png";

    if (key.contains("DAT_LICH")) return "LichKham.png";
    if (key.contains("MUA_THUOC")) return "QuanLyThuoc.png";

    return "QuanLy.png";
  }

  private static Icon loadImageIcon(String fileName) {
    BufferedImage source = loadBufferedImage(fileName);
    if (source == null) {
      return null;
    }
    Image scaled = source.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
    return new ImageIcon(scaled);
  }

  private static BufferedImage loadBufferedImage(String fileName) {
    try {
      URL resourceUrl = SidebarIcons.class.getResource(CLASSPATH_BASE + fileName);
      if (resourceUrl != null) {
        return ImageIO.read(resourceUrl);
      }

      File file = new File(FILESYSTEM_BASE + fileName);
      if (file.exists()) {
        return ImageIO.read(file);
      }
    } catch (IOException ignored) {
      return null;
    }
    return null;
  }

  private static String resolveFallbackLabel(String key) {
    if (key.contains("DASHBOARD")) return "DB";
    if (key.contains("QL_QUAN_LY")) return "QL";
    if (key.contains("QL_BAC_SI")) return "BS";
    if (key.contains("QL_DUYET_LICH_LAM")) return "DL";
    if (key.contains("QL_KHOA")) return "KH";
    if (key.contains("QL_GOI_DICH_VU")) return "DV";
    if (key.contains("LICH")) return "LK";
    if (key.contains("HOA_DON")) return "HD";
    if (key.contains("THUOC")) return "TH";
    if (key.contains("NHA_CUNG_CAP")) return "NC";
    if (key.contains("PHIEU_NHAP")) return "PN";
    return "..";
  }

  private static Icon fallback(String text) {
    return buildBadge(text, new Color(71, 85, 105));
  }

  private static Icon buildBadge(String text, Color fillColor) {
    BufferedImage image = new BufferedImage(
      SIZE,
      SIZE,
      BufferedImage.TYPE_INT_ARGB
    );
    Graphics2D g2 = image.createGraphics();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    g2.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );

    g2.setColor(fillColor);
    g2.fillRoundRect(0, 0, SIZE, SIZE, 6, 6);

    Font font = new Font("Segoe UI", Font.BOLD, 9);
    g2.setFont(font);
    g2.setColor(Color.WHITE);
    int textWidth = g2.getFontMetrics().stringWidth(text);
    int textHeight = g2.getFontMetrics().getAscent();
    int x = Math.max(0, (SIZE - textWidth) / 2);
    int y = (SIZE + textHeight) / 2 - 2;
    g2.drawString(text, x, y);

    g2.dispose();
    return new ImageIcon(image);
  }
}
