package beyondgood.model;

import beyondgood.model.formula.value.Value;

import java.util.Map;

/**
 * Represents a model for a worksheet.
 */
public interface WorksheetModel {

  /**
   * Resets the cell at the given location to contain nothing.
   *
   * @param coord the location of the Cell.
   * @throws IllegalArgumentException if coord is {@code null}.
   */
  void resetCell(Coord coord) throws IllegalArgumentException;

  /**
   * Updates the Cell at the given location to be the given Cell.
   *
   * @param coord   the location of the Cell that will be updated.
   * @param newCell the new Cell that will be at the location.
   * @throws IllegalArgumentException if the given location or Cell is null.
   */
  void updateCell(Coord coord, Cell newCell) throws IllegalArgumentException;

  /**
   * Evaluates the cell at the given location if possible.
   *
   * @param coord the location of the cell in the worksheet.
   * @return the string representation of evaluating the cell at the given location.
   * @throws IllegalArgumentException if the coord is null
   */
  Value evaluate(Coord coord) throws IllegalArgumentException;

  /**
   * Gets the cell at the given location.
   *
   * @param coord the location of the cell.
   * @return the Cell a the location.
   * @throws IllegalArgumentException if coord is null.
   */
  Cell getCellAt(Coord coord) throws IllegalArgumentException;

  /**
   * Gets all the cell that contain values in this Worksheet. Blank cells are excluded.
   *
   * @return a map of coord to the value the cell at that coord
   */
  Map<Coord, Value> getAllValues();

  /**
   * Gets all the cells raw contents in this Worksheet. Blank cells are excluded.
   *
   * @return a map of coord to the String raw contents of the cell at that coord
   */
  Map<Coord, String> getAllRawContents();

  /**
   * Gets the number of non empty Cells in this worksheet.
   *
   * @return the number of non-empty cells in the worksheet.
   */
  int getNumNonEmptyCells();

  /**
   * Gets the corresponding scalar factors for the width of a given row.
   *
   * @return a map of row index to scale
   */
  Map<Integer, Double> getInitialRowScales();

  /**
   * Gets the corresponding scalar factors for the height of a given col.
   *
   * @return a map of col index to scale
   */
  Map<Integer, Double> getInitialColScales();

  /**
   * Sets the corresponding scalar factors for the height of a given col.
   */
  void setInitialRowScales(Map<Integer, Double> rowScales);

  /**
   * Sets the corresponding scalar factors for the width of a given row.
   */
  void setInitialColScales(Map<Integer, Double> colScales);
}