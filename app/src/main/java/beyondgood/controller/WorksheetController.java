package beyondgood.controller;

import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.SexpConverter;
import beyondgood.model.Worksheet;
import beyondgood.model.WorksheetModel;
import beyondgood.model.WorksheetReader;
import beyondgood.view.WorksheetTextualView;
import beyondgood.view.WorksheetView;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents a controller to run a spreadsheet application by communicating a {@link
 * WorksheetModel} to a {@link WorksheetView} when entering/deleting cells and saving/loading
 * files.
 */
public class WorksheetController implements Features {

  private WorksheetModel model;
  private WorksheetView view;

  /**
   * Constructs a {@link WorksheetController} given a model.
   *
   * @param model the model this controller will operate on
   */
  public WorksheetController(WorksheetModel model) {
    if (model == null) {

      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  /**
   * Sets the {@link WorksheetView} that this controller will communicate data to.
   *
   * @param view the view this controller will communicate to
   */
  public void setView(WorksheetView view) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    this.view = view;
    view.addFeatures(this);
    this.relayCells();
    view.setScales(model.getInitialColScales(), model.getInitialRowScales());
    view.refresh();
    view.makeVisible();
  }

  @Override
  public void enterFormula(Coord where, String formula) {
    model.updateCell(where, new Cell(formula, SexpConverter.convertString(formula)));
    this.relayCells();
    view.resetFocus();
    view.refresh();
  }

  @Override
  public void deleteCell(Coord where) {
    model.resetCell(where);
    this.relayCells();
    view.refresh();
  }

  @Override
  public void save(String filePath) throws IOException {
    FileWriter write = new FileWriter(filePath);
    WorksheetView textView = new WorksheetTextualView(write);

    // Give the model the new initial scales
    model.setInitialColScales(view.getColScales());
    model.setInitialRowScales(view.getRowScales());

    // Set the values used to save the file from the model
    textView.setCells(model.getAllValues(), model.getAllRawContents());
    textView.setScales(model.getInitialColScales(), model.getInitialRowScales());

    // write the file
    textView.refresh();
    write.close();
  }

  @Override
  public void load(String filePath) throws IOException {

    this.model = WorksheetReader.read(Worksheet.builder(), new FileReader(filePath));
    this.relayCells();
    view.refresh();
  }

  /**
   * Updates the contents of the view with the contents of the model.
   */
  private void relayCells() {
    view.setCells(model.getAllValues(), model.getAllRawContents());
  }
}
