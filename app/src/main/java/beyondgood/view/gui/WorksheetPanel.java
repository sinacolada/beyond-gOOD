package beyondgood.view.gui;

import beyondgood.model.Coord;
import beyondgood.model.Worksheet;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueVisitor;
import beyondgood.view.gui.ColorTheme.beyondgoodTheme;
import beyondgood.view.gui.ColorTheme.Style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 * A Panel to draw the grid of Cells in a {@link Worksheet}. Each individual cell's size may be
 * resized.
 */
public class WorksheetPanel extends JPanel {

  private static final int INIT_CELL_WIDTH = 66;
  private static final int INIT_CELL_HEIGHT = 25;
  private static final int COL_HEADER_HEIGHT = 20;
  private static final int ROW_HEADER_WIDTH = 50;
  private static final int DRAG_CLICK_TOLERANCE = 10;

  private static final double LEFT_ALIGN = 0;
  // private static final double TOP_ALIGN = 0;
  //private static final double RIGHT_ALIGN = 1;
  private static final double BOT_ALIGN = 1;
  private static final double CENTER_ALIGN = 0.5;
  private static final int PADDING = 5;

  private int baseCellHeight;
  private int baseCellWidth;
  private Coord currentCoord;
  private Map<Coord, Value> values;
  private ValueVisitor<String> toStringVisitor;
  private Map<Integer, Double> colScales;
  private Map<Integer, Double> rowScales;
  private ColorTheme themeStrat;
  private Coord topLeft;

  // The point where the mouse was dragged to, null at start
  private Point dragClick;
  // The column that was clicked, -1 if none
  private int colClick;
  // The row that was clicked, -1 if none
  private int rowClick;

  /**
   * Constructs an empty panel with the {@link beyondgoodTheme}.
   */
  public WorksheetPanel(ValueVisitor<String> toStringVisitor) {
    this(new beyondgoodTheme(), toStringVisitor);
  }

  /**
   * Constructs an empty panel with the given {@link ColorTheme}.
   *
   * @param themeStrat the color strategy object to decide what colors to use
   */
  public WorksheetPanel(ColorTheme themeStrat, ValueVisitor<String> toStringVisitor) {
    super();
    this.baseCellHeight = INIT_CELL_HEIGHT;
    this.baseCellWidth = INIT_CELL_WIDTH;
    this.values = new HashMap<>();
    this.topLeft = new Coord(1, 1);
    this.currentCoord = new Coord(1, 1);
    this.themeStrat = themeStrat;
    this.toStringVisitor = toStringVisitor;
    this.rowScales = new HashMap<>();
    this.colScales = new HashMap<>();

    // no clicks at start
    this.dragClick = null;

    // no click at start
    this.colClick = -1;
    this.rowClick = -1;
  }

  @Override
  protected void paintComponent(Graphics g) {

    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    int width = this.getWidth();
    int height = this.getHeight();
    Map<Style, Color> theme = themeStrat.getTheme();

    // Draw the background
    g2d.setColor(theme.get(Style.BACKGROUND));
    g2d.fillRect(0, 0, width, height);

    // Draw the background color of the header containing the Coord cols and rows
    g2d.setColor(theme.get(Style.HEADER));
    g2d.fillRect(0, 0, width, COL_HEADER_HEIGHT);
    g2d.fillRect(0, COL_HEADER_HEIGHT, ROW_HEADER_WIDTH, height);

    // Draw the cell lines
    g2d.setColor(theme.get(Style.LINES));

    // Gets the maps of col/row index to the x/y pixel left/top values
    Map<Integer, Integer> leftXCols = getLeftXOfCols();
    Map<Integer, Integer> topYRows = getTopYOfRows();

    // Draw the lines
    leftXCols.values().forEach(x -> g2d.drawLine(x, 0, x, height));
    topYRows.values().forEach(y -> g2d.drawLine(0, y, width, y));

    // Color the selected Cell Green and draw Green lines on the header
    g2d.setColor(theme.get(Style.SELECT));
    Stroke initStroke = g2d.getStroke();
    g2d.setStroke(new BasicStroke(1.5f));
    int selectX = leftXCols.getOrDefault(currentCoord.col, -1);
    int selectY = topYRows.getOrDefault(currentCoord.row, -1);

    // If the selected coord in on the screen draw it
    if (selectX != -1 && selectY != -1) {
      g2d.drawRect(selectX, selectY, getWidthOfCol(currentCoord.col),
          getHeightOfRow(currentCoord.row));
    }
    // if the selected coord col is on screen draw it
    if (selectX != -1) {
      g2d.drawLine(selectX, COL_HEADER_HEIGHT, selectX + getWidthOfCol(currentCoord.col),
          COL_HEADER_HEIGHT);
    }
    // if the selected coord row is on screen draw it
    if (selectY != -1) {
      g2d.drawLine(ROW_HEADER_WIDTH, selectY, ROW_HEADER_WIDTH,
          selectY + getHeightOfRow(currentCoord.row));
    }

    g2d.setStroke(initStroke);
    g2d.setColor(Color.BLACK);
    Shape initClip = g2d.getClip();
    // Draw the col/row header names
    for (Entry<Integer, Integer> entry : leftXCols.entrySet()) {

      int col = entry.getKey();
      int leftX = entry.getValue();

      int xCenter = (int) (leftX + getWidthOfCol(col) * CENTER_ALIGN);
      int yCenter = (int) (COL_HEADER_HEIGHT * CENTER_ALIGN) + PADDING;
      g2d.setClip(leftX, 0, getWidthOfCol(col), COL_HEADER_HEIGHT);
      g2d.drawString(Coord.colIndexToName(col), xCenter, yCenter);
    }
    for (Entry<Integer, Integer> entry : topYRows.entrySet()) {

      int row = entry.getKey();
      int topY = entry.getValue();

      int xCenter = (int) (ROW_HEADER_WIDTH * CENTER_ALIGN) - PADDING;
      int yCenter = (int) (topY + getHeightOfRow(row) * CENTER_ALIGN);
      g2d.setClip(0, topY, ROW_HEADER_WIDTH, getHeightOfRow(row));
      g2d.drawString(Integer.toString(row), xCenter, yCenter);
    }

    // Finally draw all the values in the cells
    int centerX;
    int centerY;
    int topLeftCellX;
    int topLeftCellY;
    for (Entry<Coord, Value> entry : this.values.entrySet()) {

      Coord coord = entry.getKey();
      // Check if it is on screen
      if (leftXCols.containsKey(coord.col) && topYRows.containsKey(coord.row)) {

        topLeftCellX = leftXCols.get(coord.col);
        topLeftCellY = topYRows.get(coord.row);
        centerX = (int) (topLeftCellX + getWidthOfCol(coord.col) * LEFT_ALIGN) + PADDING;
        centerY = (int) (topLeftCellY + getHeightOfRow(coord.row) * BOT_ALIGN) - PADDING;

        g2d.setClip(new Rectangle(topLeftCellX, topLeftCellY, getWidthOfCol(coord.col),
            getHeightOfRow(coord.row)));
        g2d.drawString(entry.getValue().accept(toStringVisitor), centerX, centerY);
      }
    }
    g2d.setClip(initClip);
  }

  /**
   * Gets a map of col-index to x-pixel value of the left side of the column for all the visible
   * columns on the screen.
   *
   * @return maps col index to left x-pixel value
   */
  private Map<Integer, Integer> getLeftXOfCols() {

    Map<Integer, Integer> out = new HashMap<>();
    int currCol = topLeft.col;
    int currLeftPixelX = ROW_HEADER_WIDTH;
    int width = this.getWidth();
    while (currLeftPixelX <= width) {

      out.put(currCol, currLeftPixelX);
      currLeftPixelX += getWidthOfCol(currCol);
      currCol++;
    }

    return out;
  }

  /**
   * Gets a map of row-index to y-pixel value of the top side of the row for all the visible rows on
   * the screen.
   *
   * @return maps row index to top y-pixel value
   */
  private Map<Integer, Integer> getTopYOfRows() {

    Map<Integer, Integer> out = new HashMap<>();
    int currRow = topLeft.row;
    int currTopPixelY = COL_HEADER_HEIGHT;
    int height = this.getHeight();
    while (currTopPixelY <= height) {

      out.put(currRow, currTopPixelY);
      currTopPixelY += getHeightOfRow(currRow);
      currRow++;
    }

    return out;
  }

  /**
   * Gets the width of the corresponding col.
   *
   * @param col the col index
   * @return the pixel width of the column
   */
  private int getWidthOfCol(int col) {

    return (int) (baseCellWidth * colScales.getOrDefault(col, 1.0));
  }

  /**
   * Gets the height of the corresponding row.
   *
   * @param row the col index
   * @return the pixel width of the column
   */
  private int getHeightOfRow(int row) {

    return (int) (baseCellHeight * rowScales.getOrDefault(row, 1.0));
  }

  // ADJUSTERS

  /**
   * Adjusts the top left cell with the given deltas.
   *
   * @param dx the change in col index
   * @param dy the change in row index
   */
  public void adjustTopLeft(int dx, int dy) {
    this.topLeft = new Coord(topLeft.col + dx, topLeft.row + dy);
  }

  /**
   * Adjusts the current coord given the deltas.
   *
   * @param dx the change in col index
   * @param dy the change in row index
   */
  public void adjustCurrentCoord(int dx, int dy) {

    int newX = currentCoord.col + dx;
    int newY = currentCoord.row + dy;

    if (newX > 0 && newY > 0) {
      currentCoord = new Coord(newX, newY);
    }
  }

  /**
   * Sets the current coord with the given mouse x and y position.
   *
   * @param mouseX the mouse x position
   * @param mouseY the mouse y position
   */
  public void setCurrentCoord(int mouseX, int mouseY) {

    if (mouseX < ROW_HEADER_WIDTH || mouseY < COL_HEADER_HEIGHT) {
      return;
    }
    int col = -1;
    int row = -1;
    Map<Integer, Integer> xs = getLeftXOfCols();
    Map<Integer, Integer> ys = getTopYOfRows();
    for (Entry<Integer, Integer> entry : xs.entrySet()) {

      if (mouseX >= entry.getValue() && mouseX < entry.getValue() + getWidthOfCol(
          entry.getKey())) {

        col = entry.getKey();
        break;
      }
    }
    for (Entry<Integer, Integer> entry : ys.entrySet()) {

      if (mouseY >= entry.getValue() && mouseY < entry.getValue() + getHeightOfRow(
          entry.getKey())) {

        row = entry.getKey();
        break;
      }
    }

    // Invariant that you can only click on the screen, thus you must click some cell
    this.currentCoord = new Coord(col, row);
  }

  /**
   * Sets the current coord to the given coord.
   *
   * @param crd the given coord
   */
  public void setTopLeftAndCurrent(Coord crd) {

    this.currentCoord = crd;
    this.topLeft = crd;
  }

  /**
   * Sets where the user clicked to see if they can drag.
   *
   * @param click where the mouse is clicked
   */
  public void setDragClick(Point click) {

    this.dragClick = click;
    if (click.y <= COL_HEADER_HEIGHT) {
      for (Entry<Integer, Integer> entry : getLeftXOfCols().entrySet()) {

        int col = entry.getKey();
        int rightXPixel = entry.getValue() + getWidthOfCol(col);
        if (Math.abs(rightXPixel - click.x) <= DRAG_CLICK_TOLERANCE) {

          this.colClick = entry.getKey();
          this.rowClick = -1;
          return;
        }
      }
    }

    if (click.x <= ROW_HEADER_WIDTH) {
      for (Entry<Integer, Integer> entry : getTopYOfRows().entrySet()) {

        int row = entry.getKey();
        int botYPixel = entry.getValue() + getHeightOfRow(row);
        if (Math.abs(botYPixel - click.y) <= DRAG_CLICK_TOLERANCE) {

          this.colClick = -1;
          this.rowClick = entry.getKey();
          return;
        }
      }
    }

    // Didn't click on the header
    this.colClick = -1;
    this.rowClick = -1;
  }

  /**
   * Called when the users drags the mouse to the point.
   *
   * @param dragPoint the point where the user dragged to.
   */
  public void drag(Point dragPoint) {

    if (colClick != -1) {

      int dx = dragPoint.x - dragClick.x;
      double newScale = Math.max(0.1, (getWidthOfCol(colClick) + dx) / (double) baseCellWidth);
      colScales.put(colClick, newScale);
      this.dragClick = dragPoint;
      return;
    }

    if (rowClick != -1) {

      int dy = dragPoint.y - dragClick.y;
      double newScale = Math.max(0.1, (getHeightOfRow(rowClick) + dy) / (double) baseCellHeight);
      rowScales.put(rowClick, newScale);
      this.dragClick = dragPoint;
      return;
    }
  }

  // SETTERS

  /**
   * Sets the data that will be needed to draw the worksheet.
   *
   * @param values the values of all the cells
   */
  public void setValues(Map<Coord, Value> values) {
    this.values = values;
  }

  /**
   * Sets the column and row scales that the view will use to visualize the dimensions of each
   * cell.
   *
   * @param colScales a map of column index to its scalar factor from the view's standard size
   * @param rowScales a map of row index to its scalar factor from the view's standard size
   */
  public void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales) {

    this.colScales = colScales;
    this.rowScales = rowScales;
  }

  // GETTERS

  /**
   * Gets the currently selected coord.
   *
   * @return the currently selected Coord
   */
  public Coord getCurrentCoord() {
    return this.currentCoord;
  }

  /**
   * Gets the column scales that may have been altered by the user.
   *
   * @return the col scales
   */
  public Map<Integer, Double> getColScales() {

    return colScales;
  }

  /**
   * Gets the row scales that may have been altered by the user.
   *
   * @return the row scales
   */
  public Map<Integer, Double> getRowScales() {

    return rowScales;
  }
}
