package beyondgood.controller;

import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.WorksheetModel;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Mock {@link beyondgood.model.WorksheetModel} to confirm that the {@link
 * beyondgood.controller.WorksheetController} properly interacts with it.
 */
public class ConfirmsInputsWorksheet implements WorksheetModel {

  final StringBuilder log;

  public ConfirmsInputsWorksheet(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
  }

  @Override
  public void resetCell(Coord coord) throws IllegalArgumentException {
    log.append(String.format("reset cell at coord = %s\n", coord));
  }

  @Override
  public void updateCell(Coord coord, Cell newCell) throws IllegalArgumentException {
    log.append(String.format("updated cell at coord = %s, with newCell: %s\n", coord, newCell));
  }

  @Override
  public Value evaluate(Coord coord) throws IllegalArgumentException {
    log.append(String.format("evaluated cell at coord = %s\n", coord));
    return new BlankValue();
  }

  @Override
  public Cell getCellAt(Coord coord) throws IllegalArgumentException {
    log.append(String.format("queried cell at coord = %s\n", coord));
    return new Cell("", new BlankValue());
  }

  @Override
  public Map<Coord, Value> getAllValues() {
    log.append("queried all non-blank cell values\n");
    return new HashMap<>();
  }

  @Override
  public Map<Coord, String> getAllRawContents() {
    log.append("queried all non-blank cell raw contents\n");
    return new HashMap<>();
  }

  @Override
  public int getNumNonEmptyCells() {
    return 0;
  }

  @Override
  public Map<Integer, Double> getInitialRowScales() {
    return new HashMap<>();
  }

  @Override
  public Map<Integer, Double> getInitialColScales() {
    return new HashMap<>();
  }

  @Override
  public void setInitialRowScales(Map<Integer, Double> rowScales) {
    // Do nothing
  }

  @Override
  public void setInitialColScales(Map<Integer, Double> colScales) {
    // Do nothing
  }
}
