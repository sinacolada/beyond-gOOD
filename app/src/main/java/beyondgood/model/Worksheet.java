package beyondgood.model;

import beyondgood.model.WorksheetReader.WorksheetBuilder;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
// import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a 2-Dimensional grid of {@link Cell}s.
 */
public class Worksheet implements WorksheetModel {

  private Map<Coord, Cell> sheet;
  private Map<Coord, Value> cache;
  private Map<Integer, Double> colScales;
  private Map<Integer, Double> rowScales;

  private Worksheet() {
    this.sheet = new HashMap<>();
    this.cache = new HashMap<>();
  }

  private Worksheet(Map<Coord, Cell> sheet, Map<Integer, Double> colScales,
      Map<Integer, Double> rowScales) {

    this.sheet = sheet;
    this.cache = new HashMap<>();
    this.colScales = colScales;
    this.rowScales = rowScales;
  }

  public static Builder builder() {

    return new Builder();
  }

  /**
   * Shifts cells passed to it over by a specified amount of rows and columns.
   *
   * @param notUpdated cells to be shifted, or 'updated'.
   * @param deltaCol   the number of columns to shift each given cell.
   * @param deltaRow   the number of rows to shift each given cell.
   */
  // private void shiftCells(Map<Coord, Cell> notUpdated, int deltaCol, int deltaRow) {

  //   while (notUpdated.size() != 0) {

  //     Map<Coord, Cell> displaced = new HashMap<>();

  //     for (Entry<Coord, Cell> e : notUpdated.entrySet()) {

  //       Coord crd = e.getKey();

  //       Cell c = notUpdated.remove(crd);
  //       Coord newCrd = new Coord(crd.col + deltaCol, crd.row + deltaRow);
  //       Cell dpc = this.sheet.get(newCrd);

  //       if (dpc != null) {
  //         displaced.put(newCrd, dpc);
  //       }

  //       this.sheet.put(newCrd, c);
  //     }

  //     shiftCells(displaced, deltaCol, deltaRow);

  //   }
  // }

  @Override
  public void resetCell(Coord coord) throws IllegalArgumentException {
    ensureCoord(coord);
    sheet.remove(coord);
    cache.clear();
  }

  @Override
  public void updateCell(Coord coord, Cell newCell) throws IllegalArgumentException {

    ensureCoord(coord);
    if (newCell == null) {

      throw new IllegalArgumentException("Cannot update Cell to null");
    }
    sheet.put(coord, newCell);
    cache.clear();
  }

  @Override
  public Value evaluate(Coord coord) throws IllegalArgumentException {

    ensureCoord(coord);

    Cell cell = sheet.get(coord);
    if (cell == null) {

      return new BlankValue();
    }
    if (cache.containsKey(coord)) {
      return cache.get(coord);
    }

    Set<Coord> ancestors = new HashSet<>();
    ancestors.add(coord);
    Value val = cell.evaluate(Collections.unmodifiableMap(sheet), cache, ancestors);
    cache.put(coord, val);
    return val;
  }

  @Override
  public Cell getCellAt(Coord coord) throws IllegalArgumentException {
    ensureCoord(coord);

    Cell cell = sheet.get(coord);
    if (cell == null) {

      return new Cell("", new BlankValue());
    }

    return cell;
  }

  @Override
  public Map<Coord, Value> getAllValues() {

    Map<Coord, Value> allValues = new HashMap<>();

    for (Coord coord : sheet.keySet()) {
      allValues.put(coord, evaluate(coord));
    }

    return allValues;
  }

  @Override
  public Map<Coord, String> getAllRawContents() {

    Map<Coord, String> allContents = new HashMap<>();

    for (Coord coord : sheet.keySet()) {
      allContents.put(coord, getCellAt(coord).toString());
    }

    return allContents;
  }

  @Override
  public int getNumNonEmptyCells() {
    return this.sheet.size();
  }

  @Override
  public Map<Integer, Double> getInitialRowScales() {
    return new HashMap<>(rowScales);
  }

  @Override
  public Map<Integer, Double> getInitialColScales() {
    return new HashMap<>(colScales);

  }

  @Override
  public void setInitialRowScales(Map<Integer, Double> rowScales) {

    this.rowScales = new HashMap<>(rowScales);
  }

  @Override
  public void setInitialColScales(Map<Integer, Double> colScales) {

    this.colScales = new HashMap<>(colScales);
  }

  /**
   * Throws an error if the given coord is null.
   *
   * @param coord the coord
   * @throws IllegalArgumentException if coord is null
   */
  private void ensureCoord(Coord coord) {
    if (coord == null) {
      throw new IllegalArgumentException("Coord cannot be null");
    }
  }

  /**
   * A nested builder class to construct a {@link Worksheet}.
   */
  public static class Builder implements WorksheetBuilder<Worksheet> {

    private Map<Coord, Cell> sheet;
    private Map<Integer, Double> colScales;
    private Map<Integer, Double> rowScales;

    /**
     * Constructs a builder by creating a worksheet to return and instantiating the converter that
     * will convert s-expressions to the cell representation.
     */
    public Builder() {

      this.sheet = new HashMap<>();
      this.colScales = new HashMap<>();
      this.rowScales = new HashMap<>();
    }

    @Override
    public WorksheetBuilder<Worksheet> createCell(int col, int row, String contents) {

      Cell newCell = new Cell(contents, SexpConverter.convertString(contents));
      Coord coord = new Coord(col, row);

      sheet.put(coord, newCell);
      return this;
    }

    @Override
    public WorksheetBuilder<Worksheet> addRowScale(int rowIndex, double scale) {

      rowScales.put(rowIndex, scale);
      return this;
    }

    @Override
    public WorksheetBuilder<Worksheet> addColScale(int colIndex, double scale) {
      colScales.put(colIndex, scale);
      return this;
    }

    @Override
    public Worksheet createWorksheet() {
      return new Worksheet(sheet, colScales, rowScales);
    }
  }
}
