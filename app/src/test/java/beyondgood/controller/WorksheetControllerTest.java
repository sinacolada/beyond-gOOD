package beyondgood.controller;

import static org.junit.Assert.assertEquals;

import beyondgood.TestUtils;
import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.SexpConverter;
import beyondgood.model.Worksheet;
import beyondgood.model.WorksheetModel;
import beyondgood.model.WorksheetReader;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.view.WorksheetTextualView;
import beyondgood.view.WorksheetView;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests methods in {@link WorksheetController}.
 */
public class WorksheetControllerTest {

  // TESTING INPUTS
  // CONTROLLER -> MODEL

  @Test
  public void testDeleteCellInput() {
    StringBuilder insignificantOutput = new StringBuilder();
    StringBuilder log = new StringBuilder();
    WorksheetModel model = new ConfirmsInputsWorksheet(log);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(new WorksheetTextualView(insignificantOutput));
    controller.deleteCell(new Coord(1, 1));
    // Query the model at the instantiation of the controller, then query again after a cell has
    // been updated
    assertEquals("queried all non-blank cell values\n"
        + "queried all non-blank cell raw contents\n"
        + "reset cell at coord = A1\n"
        + "queried all non-blank cell values\n"
        + "queried all non-blank cell raw contents\n", log.toString());
  }

  @Test
  public void testUpdateCellEnterFormulaInput() {
    StringBuilder insignificantOutput = new StringBuilder();
    StringBuilder log = new StringBuilder();
    WorksheetModel model = new ConfirmsInputsWorksheet(log);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(new WorksheetTextualView(insignificantOutput));
    controller.enterFormula(new Coord(5, 14), "=(SUM 1 2)");
    assertEquals("queried all non-blank cell values\n"
        + "queried all non-blank cell raw contents\n"
        + "updated cell at coord = E14, with newCell: =(SUM 1 2)\n"
        + "queried all non-blank cell values\n"
        + "queried all non-blank cell raw contents\n", log.toString());
  }

  // TESTING CONTROLLER FUNCTIONS
  // MODEL -> CONTROLLER

  @Test
  public void setView() throws IOException {

    StringBuilder appendable = new StringBuilder();

    WorksheetView textView = new WorksheetTextualView(appendable);
    WorksheetModel model = WorksheetReader.read(Worksheet.builder(), new FileReader(
        TestUtils.getResourcePath("scroll_test.txt")));
    WorksheetController controller = new WorksheetController(model);
    controller.setView(textView);
    assertEquals("Settings: \n"
        + "A1 2\n"
        + "AZ1 =(SUM A120 4)\n"
        + "A120 =(SUM A1 4)\n"
        + "C8 true\n"
        + "B5 \"hello\"\n", appendable.toString());
  }

  @Test
  public void enterFormula() {

    WorksheetModel model = Worksheet.builder().createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell blank = new Cell("", new BlankValue());
    assertEquals(blank, model.getCellAt(new Coord(2, 2)));
    Cell newCell = new Cell("2", new DoubleValue(2));
    controller.enterFormula(new Coord(2, 2), "2");
    assertEquals(model.getCellAt(new Coord(2, 2)), newCell);
  }

  @Test
  public void testCreatingCycle() {

    WorksheetModel model = Worksheet.builder().createCell(1, 1, "=B2").createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell a1 = new Cell("=B2", new DoubleValue(0));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(0), model.evaluate(new Coord(1, 1)));

    controller.enterFormula(new Coord(2, 2), "=A1");

    Cell newA1 = new Cell("=B2", ErrorValue.CIRC);
    assertEquals(newA1, model.getCellAt(new Coord(1, 1)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(1, 1)));
    Cell b2 = new Cell("=A1", ErrorValue.CIRC);
    assertEquals(b2, model.getCellAt(new Coord(2, 2)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(2, 2)));
  }

  @Test
  public void updatingEvaluatesDifferently() {

    WorksheetModel model = Worksheet.builder().createCell(1, 1, "=B2").createCell(2, 2, "3")
        .createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell a1 = new Cell("=B2", new Reference(new Coord(2, 2), new Coord(2, 2)));
    Cell b2 = new Cell("3", new DoubleValue(3));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(1, 1)));
    assertEquals(b2, model.getCellAt(new Coord(2, 2)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(2, 2)));

    controller.enterFormula(new Coord(2, 2), "=(SUM)");

    Formula blankSum = SexpConverter.convertString("=(SUM)");
    Cell newB2 = new Cell("=(SUM)", blankSum);
    assertEquals(newB2, model.getCellAt(new Coord(2, 2)));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(0), model.evaluate(new Coord(1, 1)));
    assertEquals(new DoubleValue(0), model.evaluate(new Coord(2, 2)));
  }

  @Test
  public void deleteCell() {

    WorksheetModel model = Worksheet.builder().createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell blank = new Cell("", new BlankValue());
    assertEquals(blank, model.getCellAt(new Coord(2, 2)));
    Cell newCell = new Cell("2", new DoubleValue(2));
    controller.enterFormula(new Coord(2, 2), "2");
    assertEquals(model.getCellAt(new Coord(2, 2)), newCell);
    controller.deleteCell(new Coord(2, 2));
    assertEquals(blank, model.getCellAt(new Coord(2, 2)));
  }

  @Test
  public void deletingEvaluatesDifferently() {

    WorksheetModel model = Worksheet.builder().createCell(1, 1, "=B2").createCell(2, 2, "3")
        .createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell a1 = new Cell("=B2", new Reference(new Coord(2, 2), new Coord(2, 2)));
    Cell b2 = new Cell("3", new DoubleValue(3));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(1, 1)));
    assertEquals(b2, model.getCellAt(new Coord(2, 2)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(2, 2)));

    controller.deleteCell(new Coord(2, 2));
    Cell blank = new Cell("", new BlankValue());
    assertEquals(blank, model.getCellAt(new Coord(2, 2)));
    // Empty cells themselves evaluate to blank
    assertEquals(new BlankValue(), model.evaluate(new Coord(2, 2)));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    // Using an empty cell reference outside of a function, they evaluate to 0
    assertEquals(new DoubleValue(0), model.evaluate(new Coord(1, 1)));
  }

  @Test
  public void testDeletingEmptyCellDoesNothing() {

    WorksheetModel model = Worksheet.builder().createCell(1, 1, "=B2").createCell(2, 2, "3")
        .createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    Cell a1 = new Cell("=B2", new Reference(new Coord(2, 2), new Coord(2, 2)));
    Cell b2 = new Cell("3", new DoubleValue(3));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(1, 1)));
    assertEquals(b2, model.getCellAt(new Coord(2, 2)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(2, 2)));

    Cell blank = new Cell("", new BlankValue());

    assertEquals(new BlankValue(), model.evaluate(new Coord(69, 420)));
    assertEquals(blank, model.getCellAt(new Coord(69, 420)));
    controller.deleteCell(new Coord(69, 420));
    assertEquals(new BlankValue(), model.evaluate(new Coord(69, 420)));
    assertEquals(blank, model.getCellAt(new Coord(69, 420)));

    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(1, 1)));
    assertEquals(b2, model.getCellAt(new Coord(2, 2)));
    assertEquals(new DoubleValue(3), model.evaluate(new Coord(2, 2)));
  }

  @Test
  public void save() throws IOException {

    // Create a model, edit is, save it to a file, read the file, and check the read in model
    // is expected

    WorksheetModel model = Worksheet.builder().createCell(1, 1, "2").createCell(2, 2, "=A1")
        .createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);
    String filePath = TestUtils.getResourcePath("test_save.txt");
    controller.save(filePath);
    FileReader savedFile = new FileReader(filePath);
    WorksheetModel savedModel = WorksheetReader.read(Worksheet.builder(), savedFile);
    Cell a1 = new Cell("2", new DoubleValue(2));
    Cell b2 = new Cell("=A1", new Reference(new Coord(1, 1), new Coord(1, 1)));
    assertEquals(a1, savedModel.getCellAt(new Coord(1, 1)));
    assertEquals(b2, savedModel.getCellAt(new Coord(2, 2)));

  }

  @Test
  public void load() throws IOException {

    WorksheetModel model = Worksheet.builder().createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    String loadPath = TestUtils.getResourcePath("scroll_test.txt");
    String savePath = TestUtils.getResourcePath("load_test.txt");

    controller.load(loadPath);
    controller.save(savePath);
    WorksheetModel savedModel = WorksheetReader.read(Worksheet.builder(), new FileReader(savePath));

    Cell a1 = new Cell("2", SexpConverter.convertString("2"));
    Cell b5 = new Cell("\"hello\"", SexpConverter.convertString("\"hello\""));
    Cell c8 = new Cell("true", SexpConverter.convertString("true"));
    Cell a120 = new Cell("=(SUM A1 4)", SexpConverter.convertString("=(SUM A1 4)"));
    Cell az1 = new Cell("=(SUM A120 4)", SexpConverter.convertString("=(SUM A120 4)"));

    assertEquals(a1, savedModel.getCellAt(new Coord(1, 1)));
    assertEquals(b5, savedModel.getCellAt(new Coord(2, 5)));
    assertEquals(c8, savedModel.getCellAt(new Coord(3, 8)));
    assertEquals(a120, savedModel.getCellAt(new Coord(1, 120)));
    assertEquals(az1, savedModel.getCellAt(new Coord(52, 1)));
  }

  @Test
  public void testSavesSettingsProperly() throws IOException {

    // load a model in with some settings
    WorksheetModel model = Worksheet.builder().createWorksheet();
    StringBuilder appendable = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(appendable);
    WorksheetController controller = new WorksheetController(model);
    controller.setView(view);

    String loadPath = TestUtils.getResourcePath("scroll_test.txt");
    String savePath = TestUtils.getResourcePath("setting_load_test.txt");

    // Load the initial file
    controller.load(loadPath);
    // Should have no settings
    assertEquals(model.getInitialColScales(), new HashMap<Integer, Double>());
    assertEquals(model.getInitialRowScales(), new HashMap<Integer, Double>());

    // Do actions that a user would do in the view (can't call a method b/c these are encapsulated
    // methods in the view)
    Map<Integer, Double> newColScales = new LinkedHashMap<>();
    Map<Integer, Double> newRowScales = new LinkedHashMap<>();
    newColScales.put(1, 0.45);
    newColScales.put(45, 4.34);
    newColScales.put(23, 4.13);
    // set the scales like the controller does in the save method()
    model.setInitialColScales(newColScales);
    model.setInitialRowScales(newRowScales);
    view.setScales(newColScales, newRowScales);

    controller.save(savePath);

    WorksheetModel savedModel = WorksheetReader.read(Worksheet.builder(), new FileReader(savePath));
    assertEquals(newColScales, savedModel.getInitialColScales());
    assertEquals(newRowScales, savedModel.getInitialRowScales());
  }
}