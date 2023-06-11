package beyondgood.view;

import static org.junit.Assert.assertEquals;

import beyondgood.TestUtils;
import beyondgood.model.Coord;
import beyondgood.model.Worksheet;
import beyondgood.model.Worksheet.Builder;
import beyondgood.model.WorksheetReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * To test the {@link WorksheetTextualView}.
 */
public class TextualViewTest {

  @Test
  public void testErrorWkst() throws IOException {

    FileReader file = new FileReader(TestUtils.getResourcePath("error_test.gOOD"));
    Worksheet model = WorksheetReader.read(new Builder(), file);
    StringBuilder actualOutput = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(actualOutput);
    view.setCells(model.getAllValues(), model.getAllRawContents());
    view.refresh();

    String expectedOutput = "Settings: \n"
        + "A1 2\n"
        + "A12 (SUM A1:A3)\n"
        + "A11 =(SUM A1:A3)\n"
        + "A10 =A1:A9\n"
        + "A9 =(BAD-NAME 1 2)\n"
        + "A8 =(SUM)\n"
        + "A7 =(< A1 A7)\n"
        + "A6 =A5\n"
        + "A5 =A6\n"
        + "A4 =A4\n"
        + "A3 =(SUM A1 A1)\n";
    assertEquals(expectedOutput, actualOutput.toString());

    view.makeVisible(); // Should do nothing
    assertEquals(expectedOutput, actualOutput.toString());
    view.setCells(model.getAllValues(), model.getAllRawContents());
    assertEquals(expectedOutput, actualOutput.toString());
    // Should add to the appendable again
    view.refresh();
    assertEquals(expectedOutput + expectedOutput, actualOutput.toString());
  }

  @Test
  public void testEmptyWkst() {

    StringBuilder actualOutput = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(actualOutput);
    view.setCells(new HashMap<>(), new HashMap<>());
    view.refresh();

    String expectedOutput = "Settings: \n";
    assertEquals(expectedOutput, actualOutput.toString());
    view.makeVisible(); // Should do nothing
    assertEquals(expectedOutput, actualOutput.toString());
  }

  @Test
  public void testAllCellTypesWkst() throws IOException {

    FileReader file = new FileReader(TestUtils.getResourcePath("test_all_cell_types.txt"));
    Worksheet model = WorksheetReader.read(new Builder(), file);
    StringBuilder actualOutput = new StringBuilder();
    WorksheetView view = new WorksheetTextualView(actualOutput);
    view.setCells(model.getAllValues(), model.getAllRawContents());
    view.refresh();

    String expectedOutput = "Settings: \n"
        + "A2 0\n"
        + "A1 -4\n"
        + "A13 =A1\n"
        + "A12 =(REPEAT A6 A3)\n"
        + "A11 =(< A2 A3)\n"
        + "A10 =(PRODUCT A1 A3)\n"
        + "A9 =(SUM A2 A3)\n"
        + "A8 \"\\\"hello\\\"\"\n"
        + "A7 \"\"\n"
        + "A6 \"hello\"\n"
        + "A5 false\n"
        + "A4 true\n"
        + "A3 4\n";
    assertEquals(expectedOutput, actualOutput.toString());
    view.makeVisible(); // Should do nothing
    assertEquals(expectedOutput, actualOutput.toString());

    view.setCells(model.getAllValues(), model.getAllRawContents());
    assertEquals(expectedOutput, actualOutput.toString());
    // Should add to the appendable again
    view.refresh();
    assertEquals(expectedOutput + expectedOutput, actualOutput.toString());
  }

  /**
   * Reads in a model, then writes it to an appendable with the textual view, then reads this view
   * again as a model.
   */
  @Test
  public void readWriteRead() throws IOException {

    String filePath = TestUtils.getResourcePath("error_test.gOOD");
    Worksheet originalModel = WorksheetReader
        .read(new Worksheet.Builder(), new FileReader(filePath));
    StringBuilder stringBuilder = new StringBuilder();
    WorksheetTextualView view = new WorksheetTextualView(stringBuilder);
    view.setCells(originalModel.getAllValues(), originalModel.getAllRawContents());
    view.refresh();
    Worksheet copyModel = WorksheetReader
        .read(new Worksheet.Builder(), new StringReader(stringBuilder.toString()));

    List<Coord> cellCoords = Stream
        .of("A1", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12").map(Coord::new)
        .collect(Collectors.toList());

    for (Coord coord : cellCoords) {

      assertEquals(originalModel.getCellAt(coord).getRawCellContents(),
          copyModel.getCellAt(coord).getRawCellContents());
      assertEquals(copyModel.evaluate(coord), originalModel.evaluate(coord));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAppendableError() throws IOException {

    String filePath = TestUtils.getResourcePath("error_test.gOOD");
    Worksheet model = WorksheetReader
        .read(new Worksheet.Builder(), new FileReader(filePath));
    WorksheetView view = new WorksheetTextualView(new ErrorAppendable());
    view.setCells(model.getAllValues(), model.getAllRawContents());
    view.refresh(); // This errors
  }

  private static class ErrorAppendable implements Appendable {

    @Override
    public Appendable append(CharSequence csq) throws IOException {
      throw new IOException();
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      throw new IOException();
    }

    @Override
    public Appendable append(char c) throws IOException {
      throw new IOException();
    }
  }
}
