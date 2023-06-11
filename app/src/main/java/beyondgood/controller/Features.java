package beyondgood.controller;

import beyondgood.model.Coord;
import beyondgood.model.WorksheetModel;

import java.io.IOException;

/**
 * Represents the interactions a user can have with a {@link WorksheetModel}.
 */
public interface Features {

  /**
   * Add a new formula to the model at the given coordinate.
   *
   * @param formula the String representation of formula
   * @param where   where the formula should be in the model
   */
  void enterFormula(Coord where, String formula);

  /**
   * Delete the cell in the model at the given coordinate.
   *
   * @param where the coordinate of the cell that should be deleted
   */
  void deleteCell(Coord where);

  /**
   * Saves the current model as a .txt at the given file path.
   *
   * @param filePath the file path that will have the save
   * @throws IOException if there is an error when attempting to save the file
   */
  void save(String filePath) throws IOException;

  /**
   * Loads the given file as some {@link beyondgood.view.WorksheetView}.
   *
   * @param file the string representation of the file loaded
   * @throws IOException if there is an error when attempting to load the file
   * @throws IllegalStateException if the file being read is not in a valid file format
   */
  void load(String file) throws IOException;
}
