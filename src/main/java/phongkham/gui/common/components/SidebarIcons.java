package phongkham.gui.common.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class SidebarIcons {

  private static final int SIZE = 16;
  private static final Map<String, Icon> CACHE = new HashMap<>();
  private static final String[] EMOJI_FONT_CANDIDATES = new String[] {
    "Segoe UI Emoji",
    "Apple Color Emoji",
    "Noto Color Emoji",
    "Twemoji Mozilla",
    "Segoe UI Symbol",
  };

  private SidebarIcons() {}

  public static Icon forRoute(String route) {
    if (route == null || route.trim().isEmpty()) {
      return fallback("•");
    }
    return CACHE.computeIfAbsent(route, SidebarIcons::createIconByRoute);
  }

  private static Icon createIconByRoute(String route) {
    String key = route.toUpperCase();

    if (key.contains("DASHBOARD")) return buildEmoji("📊");
    if (key.contains("QL_QUAN_LY")) return buildEmoji("⚙️");
    if (key.contains("QL_TAI_KHOAN")) return buildEmoji("👤");
    if (key.contains("QL_ROLE")) return buildEmoji("🧩");
    if (key.contains("PHAN_QUYEN")) return buildEmoji("🔐");
    if (key.contains("QL_BAC_SI")) return buildEmoji("🩺");
    if (key.contains("QL_DUYET_LICH_LAM")) return buildEmoji("✅");
    if (key.contains("QL_KHOA")) return buildEmoji("🏥");
    if (key.contains("QL_GOI_DICH_VU")) return buildEmoji("📦");

    if (key.contains("BACSI_LICH_LAM_VIEC")) return buildEmoji("🗓️");
    if (key.contains("BACSI_LICH_KHAM")) return buildEmoji("📅");
    if (key.contains("BACSI_HOA_DON_KHAM")) return buildEmoji("🧾");
    if (key.contains("BACSI_BENH_AN")) return buildEmoji("📋");
    if (key.contains("BACSI_PROFILE")) return buildEmoji("🧑‍⚕️");

    if (key.contains("THUOC")) return buildEmoji("💊");
    if (key.contains("NHA_CUNG_CAP")) return buildEmoji("🚚");
    if (key.contains("PHIEU_NHAP")) return buildEmoji("📥");
    if (key.contains("HOA_DON_THUOC")) return buildEmoji("🛒");

    if (key.contains("DAT_LICH")) return buildEmoji("📝");
    if (key.contains("MUA_THUOC")) return buildEmoji("🛍️");

    return fallback("•");
  }

  private static Icon fallback(String text) {
    return buildEmoji(text);
  }

  private static Icon buildEmoji(String emoji) {
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

    Font font = selectEmojiFont();
    g2.setFont(font);
    int textWidth = g2.getFontMetrics().stringWidth(emoji);
    int textHeight = g2.getFontMetrics().getAscent();
    int x = Math.max(0, (SIZE - textWidth) / 2);
    int y = (SIZE + textHeight) / 2 - 2;
    g2.drawString(emoji, x, y);

    g2.dispose();
    return new ImageIcon(image);
  }

  private static Font selectEmojiFont() {
    Set<String> families = new HashSet<>();
    for (String name : GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getAvailableFontFamilyNames()) {
      families.add(name);
    }

    for (String candidate : EMOJI_FONT_CANDIDATES) {
      if (families.contains(candidate)) {
        return new Font(candidate, Font.PLAIN, 13);
      }
    }
    return new Font("Dialog", Font.PLAIN, 13);
  }
}
