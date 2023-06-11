package beyondgood.model;

import static org.junit.Assert.assertEquals;

import beyondgood.TestUtils;
import beyondgood.model.Worksheet.Builder;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.function.BeyondGoodFunction;
import beyondgood.model.formula.function.Sum;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;

/**
 * To test the {@link Worksheet} class.
 */
public class WorksheetTest {

  @Test
  public void testEmptyWkst() {

    WorksheetModel model = Worksheet.builder().createWorksheet();
    assertEquals(model.getNumNonEmptyCells(), 0);
  }

  @Test
  public void testAddCellProperly() throws IOException {

    WorksheetModel model = WorksheetReader
        .read(Worksheet.builder(), new FileReader(TestUtils.getResourcePath("scroll_test.txt")));

    Cell a1 = new Cell("2", new DoubleValue(2));
    Cell b5 = new Cell("hello", new StringValue("hello"));
    Cell c8 = new Cell("true", new BooleanValue(true));
    Formula sumFunc = new BeyondGoodFunction(
        Arrays.asList(new Reference(new Coord(1, 1), new Coord(1, 1)), new DoubleValue(4)),
        new Sum());
    Cell a120 = new Cell("=(SUM A1 4)", sumFunc);
    Formula sumFunc2 = new BeyondGoodFunction(
        Arrays.asList(new Reference(new Coord(1, 120), new Coord(1, 120)), new DoubleValue(4)),
        new Sum());
    Cell az1 = new Cell("=(SUM A120 4)", sumFunc2);

    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(b5, model.getCellAt(new Coord(2, 5)));
    assertEquals(c8, model.getCellAt(new Coord(3, 8)));
    assertEquals(a120, model.getCellAt(new Coord(1, 120)));
    assertEquals(az1, model.getCellAt(new Coord(52, 1)));

  }

  @Test
  public void testReadWkst() throws IOException {

    Worksheet model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("error_test.gOOD")));

    assertEquals(new DoubleValue(2), model.evaluate(new Coord(1, 1)));
    assertEquals(new DoubleValue(4), model.evaluate(new Coord(1, 3)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(1, 4)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(1, 5)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(1, 6)));
    assertEquals(ErrorValue.CIRC, model.evaluate(new Coord(1, 7)));
    assertEquals(new DoubleValue(0), model.evaluate(new Coord(1, 8)));
    assertEquals(ErrorValue.NAME, model.evaluate(new Coord(1, 9)));
    assertEquals(ErrorValue.REF, model.evaluate(new Coord(1, 10)));
    assertEquals(new DoubleValue(6), model.evaluate(new Coord(1, 11)));
    assertEquals(ErrorValue.ARG, model.evaluate(new Coord(1, 12)));
  }

  @Test
  public void testAddSelfRef() {

    WorksheetModel model1 = Worksheet.builder().createCell(1, 1, "=A2").createCell(1, 2, "=A1")
        .createWorksheet();

    assertEquals(ErrorValue.CIRC, model1.evaluate(new Coord(1, 1)));
    assertEquals(ErrorValue.CIRC, model1.evaluate(new Coord(1, 2)));

    WorksheetModel model2 = Worksheet.builder().createCell(1, 1, "=A1").createWorksheet();
    assertEquals(ErrorValue.CIRC, model2.evaluate(new Coord(1, 1)));
  }

  @Test
  public void testGelAllValues() throws IOException {

    WorksheetModel model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("test_all_cell_types.txt")));

    Map<Coord, Value> expectedVals = new HashMap<>();
    expectedVals.put(new Coord(1, 1), new DoubleValue(-4));
    expectedVals.put(new Coord(1, 2), new DoubleValue(0));
    expectedVals.put(new Coord(1, 3), new DoubleValue(4));
    expectedVals.put(new Coord(1, 4), new BooleanValue(true));
    expectedVals.put(new Coord(1, 5), new BooleanValue(false));
    expectedVals.put(new Coord(1, 6), new StringValue("hello"));
    expectedVals.put(new Coord(1, 7), new StringValue(""));
    expectedVals.put(new Coord(1, 8), new StringValue("\"hello\""));
    expectedVals.put(new Coord(1, 9), new DoubleValue(4));
    expectedVals.put(new Coord(1, 10), new DoubleValue(-16));
    expectedVals.put(new Coord(1, 11), new BooleanValue(true));
    expectedVals.put(new Coord(1, 12), new StringValue("hellohellohellohello"));
    expectedVals.put(new Coord(1, 13), new DoubleValue(-4));

    Map<Coord, Value> actualVals = model.getAllValues();

    for (Entry<Coord, Value> entry : expectedVals.entrySet()) {

      assertEquals(entry.getValue(), actualVals.get(entry.getKey()));
    }
  }

  @Test
  public void testGetAllRawContents() throws IOException {

    WorksheetModel model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("error_test.gOOD")));

    Map<Coord, String> expectedRawContents = new HashMap<>();
    expectedRawContents.put(new Coord(1, 1), "2");
    expectedRawContents.put(new Coord(1, 3), "=(SUM A1 A1)");
    expectedRawContents.put(new Coord(1, 4), "=A4");
    expectedRawContents.put(new Coord(1, 5), "=A6");
    expectedRawContents.put(new Coord(1, 6), "=A5");
    expectedRawContents.put(new Coord(1, 7), "=(< A1 A7)");
    expectedRawContents.put(new Coord(1, 8), "=(SUM)");
    expectedRawContents.put(new Coord(1, 9), "=(BAD-NAME 1 2)");
    expectedRawContents.put(new Coord(1, 10), "=A1:A9");
    expectedRawContents.put(new Coord(1, 11), "=(SUM A1:A3)");
    expectedRawContents.put(new Coord(1, 12), "(SUM A1:A3)");

    Map<Coord, String> actualRawContents = model.getAllRawContents();

    for (Entry<Coord, String> entry : expectedRawContents.entrySet()) {

      assertEquals(entry.getValue(), actualRawContents.get(entry.getKey()));
    }
  }

  @Test
  public void testResetCell() throws IOException {

    WorksheetModel model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("scroll_test.txt")));

    Cell a1 = new Cell("2", new DoubleValue(2));
    assertEquals(a1, model.getCellAt(new Coord(1, 1)));
    assertEquals(5, model.getNumNonEmptyCells());
    model.resetCell(new Coord(1, 1));
    Cell blank = new Cell("", new BlankValue());
    assertEquals(blank, model.getCellAt(new Coord(1, 1)));
    assertEquals(4, model.getNumNonEmptyCells());
  }

  @Test
  public void testRestBlankCell() {

    WorksheetModel model = Worksheet.builder().createWorksheet();

    Cell blank = new Cell("", new BlankValue());
    assertEquals(blank, model.getCellAt(new Coord(1, 1)));
    assertEquals(0, model.getNumNonEmptyCells());
    model.resetCell(new Coord(1, 1));
    assertEquals(blank, model.getCellAt(new Coord(1, 1)));
    assertEquals(0, model.getNumNonEmptyCells());
  }

  @Test
  public void testUpdateCell() throws IOException {

    WorksheetModel model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("scroll_test.txt")));
    assertEquals(new Cell("2", new DoubleValue(2)), model.getCellAt(new Coord(1, 1)));
    model.updateCell(new Coord(1, 1), new Cell("4", new DoubleValue(4)));
    assertEquals(new Cell("2", new DoubleValue(4)), model.getCellAt(new Coord(1, 1)));
  }

  @Test
  public void testEvaluate() throws IOException {
    WorksheetModel model = WorksheetReader
        .read(new Builder(), new FileReader(TestUtils.getResourcePath("scroll_test.txt")));
    assertEquals(new DoubleValue(2), model.evaluate(new Coord(1, 1)));
    assertEquals(new StringValue("hello"), model.evaluate(new Coord(2, 5)));
    assertEquals(new DoubleValue(6), model.evaluate(new Coord(1, 120)));
    assertEquals(new DoubleValue(10), model.evaluate(new Coord(52, 1)));
    assertEquals(new BlankValue(), model.evaluate(new Coord(45, 45)));
  }

}
