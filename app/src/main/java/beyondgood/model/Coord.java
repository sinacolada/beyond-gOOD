package beyondgood.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A value type representing coordinates in a {@link Worksheet}.
 */
public class Coord {

  public final int row;
  public final int col;

  /**
   * Constructs a Coord given its row and col index.
   *
   * @param col the column of the Coord
   * @param row the row of the Coord
   */
  public Coord(int col, int row) {
    if (row < 1 || col < 1) {
      throw new IllegalArgumentException("Coordinates should be strictly positive");
    }
    this.row = row;
    this.col = col;
  }

  /**
   * Constructs a coordinate using its string representation of a reference.
   *
   * @param coordStr the string representation of the coord to be constructed.
   * @throws IllegalArgumentException if the given String is null or not properly formatted
   */
  public Coord(String coordStr) {

    if (coordStr == null) {

      throw new IllegalArgumentException("Cannot create Coord with null String");
    }

    final Pattern cellRef = Pattern.compile("([A-Za-z]+)([1-9][0-9]*)");
    int col;
    int row;
    Matcher m = cellRef.matcher(coordStr);
    if (m.matches()) {
      col = Coord.colNameToIndex(m.group(1));
      row = Integer.parseInt(m.group(2));
    } else {
      throw new IllegalArgumentException("Badly formatted Cord: " + coordStr);
    }

    this.col = col;
    this.row = row;
  }

  /**
   * Converts from the A-Z column naming system to a 1-indexed numeric value.
   *
   * @param name the column name
   * @return the corresponding column index
   */
  public static int colNameToIndex(String name) {
    name = name.toUpperCase();
    int ans = 0;
    for (int i = 0; i < name.length(); i++) {
      ans *= 26;
      ans += (name.charAt(i) - 'A' + 1);
    }
    return ans;
  }

  /**
   * Converts a 1-based column index into the A-Z column naming system.
   *
   * @param index the column index
   * @return the corresponding column name
   */
  public static String colIndexToName(int index) {
    StringBuilder ans = new StringBuilder();
    while (index > 0) {
      int colNum = (index - 1) % 26;
      ans.insert(0, Character.toChars('A' + colNum));
      index = (index - colNum) / 26;
    }
    return ans.toString();
  }

  @Override
  public String toString() {
    return colIndexToName(this.col) + this.row;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Coord coord = (Coord) o;
    return row == coord.row
        && col == coord.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
}
