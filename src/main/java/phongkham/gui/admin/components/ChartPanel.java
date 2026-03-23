package phongkham.gui.admin.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.components.ShadowPanel;

public class ChartPanel extends ShadowPanel {

  private final ChartCanvas canvas;

  public ChartPanel(
    String title,
    String subtitle,
    String[] labels,
    double[] values
  ) {
    super(20);
    setLayout(new BorderLayout(0, 10));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

    JPanel heading = new JPanel(new BorderLayout());
    heading.setOpaque(false);

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblTitle.setForeground(UIConstants.TEXT_MAIN);

    JLabel lblSubtitle = new JLabel(subtitle);
    lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblSubtitle.setForeground(UIConstants.TEXT_MUTED);

    JPanel headingText = new JPanel();
    headingText.setOpaque(false);
    headingText.setLayout(
      new javax.swing.BoxLayout(headingText, javax.swing.BoxLayout.Y_AXIS)
    );
    headingText.add(lblTitle);
    headingText.add(lblSubtitle);

    heading.add(headingText, BorderLayout.WEST);
    add(heading, BorderLayout.NORTH);

    canvas = new ChartCanvas(labels, values);
    canvas.setPreferredSize(new Dimension(0, 250));
    add(canvas, BorderLayout.CENTER);
  }

  public void setData(String[] labels, double[] values) {
    canvas.setData(labels, values);
  }

  private static class ChartCanvas extends JPanel {

    private String[] labels;
    private double[] values;

    private ChartCanvas(String[] labels, double[] values) {
      this.labels = labels == null ? new String[0] : labels;
      this.values = values == null ? new double[0] : values;
      setOpaque(false);
    }

    private void setData(String[] labels, double[] values) {
      this.labels = labels == null ? new String[0] : labels;
      this.values = values == null ? new double[0] : values;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      );

      int width = getWidth();
      int height = getHeight();
      int leftPad = 42;
      int rightPad = 18;
      int topPad = 22;
      int bottomPad = 34;
      int chartWidth = Math.max(20, width - leftPad - rightPad);
      int chartHeight = Math.max(20, height - topPad - bottomPad);

      g2.setColor(new Color(226, 232, 240));
      g2.drawLine(
        leftPad,
        topPad + chartHeight,
        width - rightPad,
        topPad + chartHeight
      );
      g2.drawLine(leftPad, topPad, leftPad, topPad + chartHeight);

      if (values.length == 0 || labels.length == 0) {
        g2.setColor(UIConstants.TEXT_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.drawString("Chưa có dữ liệu", leftPad + 8, topPad + chartHeight / 2);
        g2.dispose();
        return;
      }

      double max = 1d;
      for (double v : values) {
        max = Math.max(max, v);
      }

      int n = Math.min(labels.length, values.length);
      int gap = Math.max(8, chartWidth / Math.max(1, n));
      int barWidth = Math.max(12, Math.min(36, gap - 12));

      g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
      g2.setStroke(new BasicStroke(1f));

      for (int i = 0; i < n; i++) {
        int centerX = leftPad + (i * gap) + gap / 2;
        int barX = centerX - barWidth / 2;
        int barHeight = (int) ((values[i] / max) * (chartHeight - 8));
        int barY = topPad + chartHeight - barHeight;

        g2.setColor(new Color(191, 219, 254));
        g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);
        g2.setColor(UIConstants.PRIMARY);
        g2.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        g2.setColor(UIConstants.TEXT_MUTED);
        String xLabel = labels[i];
        int tw = g2.getFontMetrics().stringWidth(xLabel);
        g2.drawString(xLabel, centerX - tw / 2, topPad + chartHeight + 16);
      }

      g2.setColor(UIConstants.PRIMARY_DARK);
      g2.setStroke(new BasicStroke(2f));
      int prevX = -1;
      int prevY = -1;
      for (int i = 0; i < n; i++) {
        int centerX = leftPad + (i * gap) + gap / 2;
        int y =
          topPad + chartHeight - (int) ((values[i] / max) * (chartHeight - 8));

        g2.setColor(UIConstants.PRIMARY_DARK);
        g2.fillOval(centerX - 3, y - 3, 6, 6);
        if (prevX >= 0) {
          g2.drawLine(prevX, prevY, centerX, y);
        }
        prevX = centerX;
        prevY = y;
      }

      g2.dispose();
    }
  }
}
