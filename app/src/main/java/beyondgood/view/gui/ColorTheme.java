package beyondgood.view.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A Strategy function object to get the corresponding colors for a given {@link Style}.
 */
public interface ColorTheme {

  /**
   * A nested enum to describe the different kind of features that will use distinct colors in a
   * {@link WorksheetPanel}.
   */
  enum Style {
    BACKGROUND, HEADER, LINES, SELECT, SHADED_HEADER
  }

  /**
   * Gets the corresponding Colors for each {@link Style}.
   *
   * @return a map of {@link Style} to Color for what each style should be colored
   */
  Map<Style, Color> getTheme();

  /**
   * A {@link ColorTheme} that uses colors similar to Microsoft beyondgood.
   */
  class beyondgoodTheme implements ColorTheme {

    @Override
    public Map<Style, Color> getTheme() {

      Map<Style, Color> colorMap = new HashMap<>();
      colorMap.put(Style.BACKGROUND, Color.WHITE);
      colorMap.put(Style.HEADER, new Color(230, 230, 230));
      colorMap.put(Style.LINES, new Color(200, 200, 200));
      colorMap.put(Style.SELECT, new Color(33, 115, 70));
      colorMap.put(Style.SHADED_HEADER, new Color(210, 210, 210));
      return colorMap;
    }
  }

  /**
   * A {@link ColorTheme} that uses colors similar to Google Sheets.
   */
  class GoogleSheetTheme implements ColorTheme {

    @Override
    public Map<Style, Color> getTheme() {

      Map<Style, Color> colorMap = new HashMap<>();
      colorMap.put(Style.BACKGROUND, Color.WHITE);
      colorMap.put(Style.HEADER, new Color(248, 249, 250));
      colorMap.put(Style.LINES, new Color(226, 227, 227));
      colorMap.put(Style.SELECT, new Color(26, 115, 232));
      colorMap.put(Style.SHADED_HEADER, new Color(232, 234, 237));
      return colorMap;
    }
  }

  /**
   * A {@link ColorTheme} that uses vibrant, bright colors.
   */
  class NeonTheme implements ColorTheme {

    @Override
    public Map<Style, Color> getTheme() {
      Map<Style, Color> colorMap = new HashMap<>();
      colorMap.put(Style.BACKGROUND, new Color(250, 16, 150));
      colorMap.put(Style.HEADER, new Color(158, 250, 36));
      colorMap.put(Style.LINES, new Color(0, 227, 220));
      colorMap.put(Style.SELECT, new Color(26, 115, 232));
      colorMap.put(Style.SHADED_HEADER, new Color(237, 165, 0));
      return colorMap;
    }
  }

  /**
   * A {@link ColorTheme} that gets a new random set of colors each time {@code getTheme()} is
   * called.
   */
  class EpilepsyTheme implements ColorTheme {

    @Override
    public Map<Style, Color> getTheme() {

      Map<Style, Color> colorMap = new HashMap<>();
      colorMap.put(Style.BACKGROUND, randomColor());
      colorMap.put(Style.HEADER, randomColor());
      colorMap.put(Style.LINES, randomColor());
      colorMap.put(Style.SELECT, randomColor());
      colorMap.put(Style.SHADED_HEADER, randomColor());
      return colorMap;
    }

    /**
     * Gets a new random color.
     *
     * @return a random color
     */
    private static Color randomColor() {

      Random random = new Random();
      int r = random.nextInt(256);
      int g = random.nextInt(256);
      int b = random.nextInt(256);
      return new Color(r, g, b);
    }
  }

}
