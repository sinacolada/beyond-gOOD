package beyondgood.view.gui;

import beyondgood.model.Coord;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueToStringVisitor;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.AdjustmentListener;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.UIManager;

/**
 * A Custom Scroll panel for a {@link WorksheetPanel}.
 */
public class BeyondGoodScrollPane extends JPanel {

  private WorksheetPanel worksheetPanel;
  private JScrollBar vertical;
  private JScrollBar horizontal;
  private int prevVertVal;
  private int prevHorizVal;

  /**
   * Constructs an empty scroll pane, with not data in it.
   */
  public BeyondGoodScrollPane() {
    this.worksheetPanel = new WorksheetPanel(new ValueToStringVisitor());
    UIManager.put("ScrollBar.width", 15);
    this.vertical = new JScrollBar(JScrollBar.VERTICAL);
    this.horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
    this.vertical.setUI(new CustomScrollBarUI());
    this.horizontal.setUI(new CustomScrollBarUI());
    this.prevVertVal = vertical.getValue();
    this.prevHorizVal = horizontal.getValue();
    this.setLayout(new BorderLayout());
    this.add(vertical, BorderLayout.EAST);
    this.add(horizontal, BorderLayout.SOUTH);
    this.add(worksheetPanel, BorderLayout.CENTER);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  /**
   * Sets the vertical scroll bar's listener.
   *
   * @param l the AdjustmentListener
   */
  public void setVerticalListener(AdjustmentListener l) {
    vertical.addAdjustmentListener(l);
  }

  /**
   * Sets the horizontal scroll bar's listener.
   *
   * @param l the AdjustmentListener
   */
  public void setHorizontalListener(AdjustmentListener l) {
    horizontal.addAdjustmentListener(l);
  }

  /**
   * Adjusted the contained panel based on the new given value for the vertical scroll bar value.
   *
   * @param newValue the new value for the position of the scroll bar
   */
  public void adjustVertical(int newValue) {
    if (newValue < 0 || newValue + vertical.getModel().getExtent() > vertical.getMaximum()) {
      throw new IllegalArgumentException("Invalid value for vertical scroll bar");
    }

    int dy = newValue - prevVertVal;

    prevVertVal = newValue;
    try {
      this.worksheetPanel.adjustTopLeft(0, dy);
    } catch (IllegalArgumentException ignored) {

    }
    if (newValue + vertical.getModel().getExtent() == vertical.getMaximum()) {
      vertical.setMaximum(vertical.getMaximum() + 10);
    }
  }

  /**
   * Adjusted the contained panel based on the new given value for the horizontal scroll bar value.
   *
   * @param newValue the new value for the horizontal of the scroll bar
   */
  public void adjustHorizontal(int newValue) {
    if (newValue < 0 || newValue + horizontal.getModel().getExtent() > horizontal.getMaximum()) {
      throw new IllegalArgumentException("Invalid value for horizontal scroll bar");
    }

    int dx = newValue - prevHorizVal;

    prevHorizVal = newValue;
    try {
      this.worksheetPanel.adjustTopLeft(dx, 0);
    } catch (IllegalArgumentException ignored) {

    }

    if (newValue + horizontal.getModel().getExtent() == horizontal.getMaximum()) {
      horizontal.setMaximum(horizontal.getMaximum() + 10);
    }
  }

  /**
   * Adjust the top left cell and the scroll bar(s) respectively to reflect the newly set top left
   * coord.
   *
   * @param newCoord the new coordinate of the top left cell
   */
  public void moveScrollBarToNewCurrent(Coord newCoord) {

    int dx = newCoord.col - getCurrentCoord().col;
    int dy = newCoord.row - getCurrentCoord().row;

    // seems odd to always make max larger, but this works so
    if (dx > 0) {
      horizontal.setMaximum(horizontal.getMaximum() + dx);
    }
    if (dy > 0) {
      vertical.setMaximum(vertical.getMaximum() + dy);
    }
    horizontal.setValue(newCoord.col - 1);
    vertical.setValue(newCoord.row - 1);
    this.worksheetPanel.setTopLeftAndCurrent(newCoord);
  }

  /**
   * Sets the current coord that is selected in the contained panel.
   *
   * @param mouseX the x coordinate of where the mouse was clicked
   * @param mouseY the y coordinate of where the mouse was clicked
   */
  public void setCurrentCoord(int mouseX, int mouseY) {
    worksheetPanel.setCurrentCoord(mouseX, mouseY);
  }

  /**
   * Adjusts the current coord that is selected in the contained panel by the given x and y deltas.
   *
   * @param dx the change in the col
   * @param dy the change in the row
   */
  public void adjustCurrentCoord(int dx, int dy) {
    worksheetPanel.adjustCurrentCoord(dx, dy);
  }

  /**
   * Sets the data int the contained panel will need to draw the worksheet.
   *
   * @param values the values of all the cells
   */
  public void setValues(Map<Coord, Value> values) {
    worksheetPanel.setValues(values);
  }

  /**
   * Sets the column and row scales that the pane will use to visualize the dimensions of each
   * cell.
   *
   * @param colScales a map of column index to its scalar factor from the pane's standard size
   * @param rowScales a map of row index to its scalar factor from the pane's standard size
   */
  public void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales) {

    worksheetPanel.setScales(colScales, rowScales);
  }

  public Map<Integer, Double> getColScales() {

    return worksheetPanel.getColScales();
  }

  public Map<Integer, Double> getRowScales() {

    return worksheetPanel.getRowScales();
  }

  /**
   * Gets the coordinate of currently selected cell.
   *
   * @return the coordinate that is currently selected
   */
  public Coord getCurrentCoord() {
    return this.worksheetPanel.getCurrentCoord();
  }

  public void setDragClick(Point point) {

    worksheetPanel.setDragClick(point);
  }

  public void drag(Point point) {

    worksheetPanel.drag(point);
  }
}
