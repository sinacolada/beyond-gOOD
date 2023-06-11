package beyondgood.view.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * A custom UI for drawing scroll bars.
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

  @Override
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    g.setColor(new Color(230, 230, 230));
    g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

    if (trackHighlight == DECREASE_HIGHLIGHT) {
      paintDecreaseHighlight(g);
    } else if (trackHighlight == INCREASE_HIGHLIGHT) {
      paintIncreaseHighlight(g);
    }
  }

  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
      return;
    }

    int w = thumbBounds.width;
    int h = thumbBounds.height;

    g.translate(thumbBounds.x, thumbBounds.y);

    g.setColor(new Color(170, 170, 170));
    g.drawRect(0, 0, w - 1, h - 1);
    g.setColor(new Color(210, 210, 210));
    g.fillRect(0, 0, w - 1, h - 1);

    g.setColor(new Color(230, 230, 230));
    // g.drawVLine(1, 1, h - 2);
    g.drawLine(1, 1, 1, h - 2);
    // g.drawHLine(2, w - 3, 1);
    g.drawLine(2, 1, w - 3, 1);

    g.setColor(new Color(200, 200, 200));
    // drawHLine(g, 2, w - 2, h - 2);
    g.drawLine(2, h - 2, w - 2, h - 2);
    // drawVLine(g, w - 2, 1, h - 3);
    g.drawLine(w - 2, 1, w - 2, h - 3);

    g.translate(-thumbBounds.x, -thumbBounds.y);
  }

  @Override
  protected JButton createDecreaseButton(int orientation) {
    return new BasicArrowButton(orientation,
        new Color(210, 210, 210),
        new Color(190, 190, 190),
        new Color(100, 100, 100),
        new Color(230, 230, 230));
  }

  @Override
  protected JButton createIncreaseButton(int orientation) {
    return new BasicArrowButton(orientation,
        new Color(210, 210, 210),
        new Color(190, 190, 190),
        new Color(100, 100, 100),
        new Color(230, 230, 230));
  }

}
