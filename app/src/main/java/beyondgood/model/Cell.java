package beyondgood.model;

import beyondgood.model.formula.Formula;
import beyondgood.model.formula.value.Value;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represent a single Cell in a {@link Worksheet}.
 */
public class Cell {

  private final String rawCellContents;
  private final Formula formula;

  /**
   * Constructs a Cell given what the raw contents of the Cell are and the formula within it.
   *
   * @param rawCellContents the raw contents of this Cell represented as a String
   * @param formula         the {@link Formula} that this Cell holds
   */
  public Cell(String rawCellContents, Formula formula) {
    this.rawCellContents = rawCellContents;
    this.formula = formula;
  }

  /**
   * Evaluates this Cell to a {@link Value}.
   *
   * @param refs      the Cells that this Cell refers to
   * @param cache     a cache of what the Cells at the Coords evaluate to (can just be empty map,
   *                  i.e. {@code new HasMap<>()}, but may hinder performance)
   * @param ancestors the Coords of the Cells that are currently being evaluated in a tree of
   *                  evaluation
   * @return what this Cell evaluates to
   */
  public Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors) {

    return formula.evaluate(refs, cache, ancestors);
  }

  /**
   * Gets the raw cell contents of the Cell as a String.
   *
   * @return the String representation of the raw contents of this Cell.
   */
  public String getRawCellContents() {
    return rawCellContents;
  }

  @Override
  public String toString() {
    return rawCellContents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Cell other = (Cell) o;
    return this.rawCellContents.equals(other.rawCellContents) ||
        this.formula.equals(other.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rawCellContents, formula);
  }
}
