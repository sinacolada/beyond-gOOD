package beyondgood.view;

import beyondgood.controller.Features;
import beyondgood.model.Coord;
import beyondgood.model.formula.value.Value;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A textual view of a {@link beyondgood.model.WorksheetModel} that displays each cell
 * on a appendable with the following format: "Coord Formula\n".
 */
public class WorksheetTextualView implements WorksheetView {

  private static final String HEADER = "Settings: ";

  private Appendable ap;
  private Map<Coord, String> rawContents;
  private Map<Integer, Double> colScales;
  private Map<Integer, Double> rowScales;

  /**
   * Constructs an empty textual view.
   *
   * @param ap the Appendable that holds the textual view
   */
  public WorksheetTextualView(Appendable ap) {

    this.ap = ap;
    this.rawContents = new HashMap<>();
    this.colScales = new HashMap<>();
    this.rowScales = new HashMap<>();
  }

  @Override
  public void refresh() {

    try {
      // Write the "Settings: " header
      ap.append(HEADER);
      // Write the column scales
      for (Entry<Integer, Double> entry : colScales.entrySet()) {

        String col = Coord.colIndexToName(entry.getKey());
        ap.append(
            String.format("%s|%.2f ", col, entry.getValue()));
      }
      // Write the row scales
      for (Entry<Integer, Double> entry : rowScales.entrySet()) {

        ap.append(String.format("%s|%.2f ", entry.getKey(), entry.getValue()));
      }
      // Add a new line at the end
      ap.append("\n");
      // Write everything in the sheet
      for (Map.Entry<Coord, String> entry : rawContents.entrySet()) {

        ap.append(String.format("%s %s\n", entry.getKey().toString(), entry.getValue()));
      }
    } catch (IOException e) {

      throw new IllegalArgumentException("Appendable cannot be written to");
    }
  }

  @Override
  public void makeVisible() {
    // Does nothing in this textual view, everything is already "visible"
  }

  @Override
  public void setCells(Map<Coord, Value> values, Map<Coord, String> rawContents) {

    this.rawContents = rawContents;
    // values are not displayed in this textual view, so no need to store it
  }

  @Override
  public void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales) {

    this.colScales = colScales;
    this.rowScales = rowScales;
  }

  @Override
  public void addFeatures(Features features) {
    // This textual view does not have interactions with any features.
    // do nothing - this will make testing controller inputs with insignificant output easier
  }

  @Override
  public void addReadCapabilities() {
    // All the data is readable in a textual view, so no additional things to add
  }

  @Override
  public void resetFocus() {
    // The textual view can never get out of visual focus, so no need to reset it. It always is in
    // focus.
  }

  @Override
  public Map<Integer, Double> getColScales() {
    return colScales;
  }

  @Override
  public Map<Integer, Double> getRowScales() {
    return rowScales;
  }
}
