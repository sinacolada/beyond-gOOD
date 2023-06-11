package beyondgood.view;

import beyondgood.controller.Features;
import beyondgood.model.Coord;
import beyondgood.model.WorksheetModel;
import beyondgood.model.formula.value.Value;
import java.util.Map;

/**
 * A view for a {@link WorksheetModel} that visualizes how the data is seen by displaying the
 * contents of the Cells by some fashion.
 */
public interface WorksheetView {

  /**
   * Signals to the view to re-display the data. Also updates the view of any data that has changed
   * since the last refresh.
   */
  void refresh();

  /**
   * Signals to the view to make the data visible.
   */
  void makeVisible();

  /**
   * Signals to the view to visualize the worksheet using the given data.
   *
   * @param values      the values of all the Cells at the given Coords
   * @param rawContents the raw contents of all the Cells at the given Coords
   */
  void setCells(Map<Coord, Value> values, Map<Coord, String> rawContents);

  /**
   * Sets the column and row scales that the view will use to visualize the dimensions of
   * each cell.
   *
   * @param colScales a map of column index to its scalar factor from the view's standard size
   * @param rowScales a map of row index to its scalar factor from the view's standard size
   */
  void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales);

  /**
   * Set the features that this view interacts with.
   *
   * @param features the features that the view interact with.
   */
  void addFeatures(Features features);

  /**
   * Adds any additional reading capabilities a view should be able to perform; these have no impact
   * on the model and are thus separate from the controller features.
   */
  void addReadCapabilities();

  /**
   * Signal to the view to set itself as the main thing to interact with on the screen.
   */
  void resetFocus();

  Map<Integer, Double> getColScales();

  Map<Integer, Double> getRowScales();
}
