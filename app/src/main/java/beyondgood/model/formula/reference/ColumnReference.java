package beyondgood.model.formula.reference;

import beyondgood.model.Coord;
import beyondgood.model.Cell;

import java.util.function.Predicate;

/**
 * Represents a reference to all the {@link Cell}s between any two columns.
 */
public class ColumnReference extends AbstractInfiniteReference {

  private int leftCol;
  private int rightCol;

  /**
   * Constructs a column reference given the left column and right column to refer to. Though the
   * names of the variables suggest the left col must be the first int, this is not a requirement.
   *
   * @param leftCol the left column index
   * @param rightCol the right column index
   * @throws IllegalArgumentException if either of the indexes are below 1
   */
  public ColumnReference(int leftCol, int rightCol) {

    if (leftCol < 1 || rightCol < 1) {

      throw new IllegalArgumentException("Invalid column reference");
    }

    this.leftCol = Math.min(leftCol, rightCol);
    this.rightCol = Math.max(leftCol, rightCol);
  }

  @Override
  protected Predicate<Coord> isInReference() {
    return coord -> coord.col >= leftCol && coord.col <= rightCol;
  }
}
