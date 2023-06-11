package beyondgood.model;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A factory for reading inputs and producing Worksheets: given a {@link WorksheetBuilder} to
 * produce the new Worksheet itself, this class can parse the given input and populate the
 * worksheet.
 */
public final class WorksheetReader {

  /**
   * A builder pattern for producing Worksheets.
   *
   * @param <T> the type of Worksheet to produce
   */
  public interface WorksheetBuilder<T> {

    /**
     * Creates a new cell at the given coordinates and fills in its raw contents.
     *
     * @param col      the column of the new cell (1-indexed)
     * @param row      the row of the new cell (1-indexed)
     * @param contents the raw contents of the new cell: may be {@code null}, or any string. Strings
     *                 beginning with an {@code =} character should be treated as formulas; all
     *                 other strings should be treated as number or boolean values if possible, and
     *                 string values otherwise.
     * @return this {@link WorksheetBuilder}
     */
    WorksheetBuilder<T> createCell(int col, int row, String contents);

    /**
     * Adds a scale to the default height of a given row.
     *
     * @param rowIndex the row index
     * @param scale    the scale factor it should, a positive real number
     * @return this {@link WorksheetBuilder}
     */
    WorksheetBuilder<T> addRowScale(int rowIndex, double scale);

    /**
     * Adds a scale to the default width of a given col.
     *
     * @param colIndex the col index
     * @param scale    the scale factor it should, a positive real number
     * @return this {@link WorksheetBuilder}
     */
    WorksheetBuilder<T> addColScale(int colIndex, double scale);

    /**
     * Finalizes the construction of the worksheet and returns it.
     *
     * @return the fully-filled-in worksheet
     */
    T createWorksheet();
  }

  /**
   * <p>A factory for producing Worksheets.  The file format is</p>
   * <pre>
   *   &lt;cell coordinates, in A# format&gt; &lt;the raw contents of the cell&gt;
   *   ...
   * </pre>
   * <p>e.g.</p>
   * <pre>
   *   A1 5
   *   A2 6
   *   A3 =(SUM A1 A2:A2)
   * </pre>
   * <p>Line-comments are indicated by <code>#</code>, and are allowed either at the start
   * of a line or between the cell coordinate and its contents.</p>
   * <p>There is no requirement that cells are filled in in order of their dependencies,
   * since no cell evaluation occurs during this creation process.</p>
   *
   * @param builder  The source of the new Worksheet object
   * @param readable the input source for the contents of this Worksheet
   * @param <T>      the type of Worksheet to produce
   * @return the fully-filled-in Worksheet
   */
  public static <T> T read(WorksheetBuilder<T> builder, Readable readable) {
    Scanner scan = new Scanner(readable);
    final Pattern cellRef = Pattern.compile("([A-Za-z]+)([1-9][0-9]*)");

    // CHANGES START HERE
    String header = "Settings:";
    final Pattern colScale = Pattern.compile("([A-Za-z]+)\\|([0-9]+\\.[0-9]{2})");
    final Pattern rowScale = Pattern.compile("([0-9][0-9]*)\\|([0-9]+\\.[0-9]{2})");
    if (!scan.hasNext(header)) {
      scan.close();
      throw new IllegalArgumentException("Expected \"Settings: \" on first line");
    }
    // Skip the header
    scan.next();
    // Read the scales in another scanner
    Scanner settingsReader = new Scanner(scan.nextLine());
    while (settingsReader.hasNext()) {

      String pref = settingsReader.next();
      Matcher colMatch = colScale.matcher(pref);
      Matcher rowMatch = rowScale.matcher(pref);
      if (colMatch.matches()) {

        int col = Coord.colNameToIndex(colMatch.group(1));
        double scale = Double.parseDouble(colMatch.group(2));
        builder = builder.addColScale(col, scale);
      } else if (rowMatch.matches()) {

        int row = Integer.parseInt(rowMatch.group(1));
        double scale = Double.parseDouble(rowMatch.group(2));
        builder = builder.addRowScale(row, scale);
      } else {
        scan.close();
        throw new IllegalArgumentException("Expected row or col scale");
      }
    }
    // CHANGES END HERE

    scan.useDelimiter("\\s+");
    while (scan.hasNext()) {
      int col;
      int row;
      while (scan.hasNext("#.*")) {
        scan.nextLine();
        scan.skip("\\s*");
      }
      String cell = scan.next();
      Matcher m = cellRef.matcher(cell);
      if (m.matches()) {
        col = Coord.colNameToIndex(m.group(1));
        row = Integer.parseInt(m.group(2));
      } else {
        scan.close();
        throw new IllegalStateException("Expected cell ref");
      }
      scan.skip("\\s*");
      while (scan.hasNext("#.*")) {
        scan.nextLine();
        scan.skip("\\s*");
      }
      String contents = scan.nextLine();
      builder = builder.createCell(col, row, contents);
    }

    scan.close();
    return builder.createWorksheet();
  }
}
