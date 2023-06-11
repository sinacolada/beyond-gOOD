package beyondgood.model.formula.reference;

import beyondgood.model.Cell;
import beyondgood.model.Coord;

import java.util.function.Predicate;

/**
 * Represents a reference to all the {@link Cell}s between any two rows.
 */
public class RowReference extends AbstractInfiniteReference {

  private int topRow;
  private int botRow;

  /**
   * Constructs a row reference given the top row and bottom row to refer to. Though the names of
   * the variables suggest the top row must be the first int, this is not a requirement.
   *
   * @param topRow the top row index
   * @param botRow the bot row index
   * @throws IllegalArgumentException if either of the indexes are below 1
   */
  public RowReference(int topRow, int botRow) {

    if (topRow < 1 || botRow < 1) {

      throw new IllegalArgumentException("Invalid row reference");
    }

    this.topRow = Math.min(topRow, botRow);
    this.botRow = Math.max(topRow, botRow);
  }

  @Override
  protected Predicate<Coord> isInReference() {
    return coord -> coord.row >= topRow && coord.row <= botRow;
  }
}
