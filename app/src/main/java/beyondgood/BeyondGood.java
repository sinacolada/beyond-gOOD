package beyondgood;

import beyondgood.controller.WorksheetController;
import beyondgood.model.Coord;
import beyondgood.model.Worksheet;
import beyondgood.model.Worksheet.Builder;
import beyondgood.model.WorksheetModel;
import beyondgood.model.WorksheetReader;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueToStringVisitor;
import beyondgood.view.gui.ReadableWorksheetGUIView;
import beyondgood.view.WorksheetTextualView;
import beyondgood.view.WorksheetView;
import beyondgood.view.gui.WritableWorksheetGUIView;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

/**
 * The main class for our program.
 */
public class BeyondGood {

  /**
   * The main entry point.
   *
   * @param args any command-line arguments
   */
  public static void main(String[] args) {

    Mode mode = checkMainArgs(args);
    if (mode == null) {

      printUsage();
      System.exit(1);
    }

    switch (mode) {
      case EVAL:
        eval(args);
        break;
      case SAVE_COPY:
        saveCopy(args);
        break;
      case NEW_GUI:
        newGui();
        break;
      case LOAD_GUI:
        loadGui(args);
        break;
      case LOAD_EDIT:
        loadEdit(args);
        break;
      case NEW_EDIT:
        newEdit();
        break;
      default:
        throw new AssertionError("End of switch");
    }
  }

  /**
   * Prints the Usage of BeyondGood to {@code System.out} and exits the program.
   */
  private static void printUsage() {

    String space = " ".repeat(18);
    System.out.println("Usage: BeyondGood -in filename -eval cell-name\n"
        + space + "-in filename -save new-filename\n"
        + space + "-in filename -gui\n"
        + space + "-in filename -edit\n"
        + space + "-gui\n"
        + space + "-edit\n"
        + "where filename is a .gOOD or .txt file and cell-name is a coordinate");
  }

  /**
   * Gets the requested {@link Mode} from the command line arguments.
   *
   * @param args the command line arguments
   * @return the {@link Mode} requested, or null if there exists none
   */
  private static Mode checkMainArgs(String[] args) {

    if (args.length == 1) {

      String flag = args[0];
      if (flag.equals("-gui")) {
        return Mode.NEW_GUI;
      } else if (flag.equals("-edit")) {
        return Mode.NEW_EDIT;
      }
    }

    if (args.length == 3 && args[0].equals("-in") && validateFileType(args[1])) {

      String flag = args[2];
      if (flag.equals("-gui")) {
        return Mode.LOAD_GUI;
      } else if (flag.equals("-edit")) {
        return Mode.LOAD_EDIT;
      }
    }

    if (args.length == 4 && args[0].equals("-in") && validateFileType(args[1])) {

      String flag = args[2];
      String arg3 = args[3];

      if (flag.equals("-eval") && validateCoord(arg3)) {

        return Mode.EVAL;
      }

      if (flag.equals("-save") && validateFileType(arg3)) {

        return Mode.SAVE_COPY;
      }
    }

    return null;
  }


  /**
   * A Nested Enum to describes the possible kinds of action that can be launched with BeyondGood.
   */
  private enum Mode {

    EVAL, SAVE_COPY, LOAD_GUI, LOAD_EDIT, NEW_GUI, NEW_EDIT
  }

  /**
   * Determines if the given String is a valid file type, either a .txt or .gOOD file.
   *
   * @param fileName the file name
   * @return true if the file is .txt or .gOOD, false otherwise
   * @throws IllegalArgumentException if {@code fileName} is null
   */
  private static boolean validateFileType(String fileName) {

    if (fileName == null) {

      throw new IllegalArgumentException();
    }

    int lastPeriod = fileName.lastIndexOf('.');
    if (lastPeriod == -1) {
      return false;
    }
    String fileType = fileName.substring(lastPeriod);

    return fileType.equals(".gOOD") || fileType.equals(".txt");
  }

  /**
   * Determines if the given String is a valid representation of a {@link Coord}.
   *
   * @param coord the given String
   * @return true if the String can be converted to a Coord, false otherwise
   * @throws IllegalArgumentException if the String is null
   */
  private static boolean validateCoord(String coord) {

    if (coord == null) {

      throw new IllegalArgumentException();
    }
    try {
      new Coord(coord);
      return true;
    } catch (IllegalArgumentException e) {

      return false;
    }
  }

  /**
   * ASSUMPTION: the args are in this form: -in some-filename -eval some-cell.
   * <p>
   * Reads the given worksheet file and evaluates the Cell at the given Coord. Prints any errors.
   * </p>
   *
   * @param args the command line arguments
   */
  private static void eval(String[] args) {

    String filePath = args[1];
    Coord evalCoord = new Coord(args[3]);
    FileReader worksheet;
    try {
      worksheet = new FileReader(filePath);
    } catch (FileNotFoundException e) {
      System.out.print(e.getMessage());
      return;
    }
    Worksheet model = WorksheetReader.read(new Worksheet.Builder(), worksheet);
    StringBuilder errorLog = new StringBuilder();
    ValueToStringVisitor toStringVisitor = new ValueToStringVisitor();

    for (Entry<Coord, Value> vals : model.getAllValues().entrySet()) {

      // Could use a visitor but this is so much easier
      if (vals.getValue() instanceof ErrorValue) {
        errorLog.append(String
            .format("%s %s\n", vals.getKey().toString(), vals.getValue().accept(toStringVisitor)));
      }
    }
    if (errorLog.length() == 0) {

      System.out.print(model.evaluate(evalCoord).accept(toStringVisitor));
    } else {
      System.out.print(errorLog.toString().stripTrailing());
    }
  }

  /**
   * ASSUMPTION: the args are in this form: -in some-filename -save some-new-filename.
   * <p>
   * Opens the first file, and saves it as the second file, using the {@link WorksheetTextualView}.
   * </p>
   *
   * @param args the command line arguments
   */
  private static void saveCopy(String[] args) {

    try {

      FileWriter write = new FileWriter(args[3]);

      Worksheet.Builder builder = new Builder();
      Worksheet model = WorksheetReader
          .read(builder, new FileReader(args[1]));
      WorksheetView view = new WorksheetTextualView(write);
      view.setCells(model.getAllValues(), model.getAllRawContents());
      view.refresh();
      write.close();

    } catch (IOException e) {

      System.out.print(e.getMessage());
    }
  }

  /**
   * Opens a graphical view with a blank new {@link ReadableWorksheetGUIView}.
   */
  private static void newGui() {

    WorksheetView guiView = new ReadableWorksheetGUIView();
    guiView.makeVisible();
  }

  /**
   * ASSUMPTION: the args are in this form: -in some-filename -gui.
   * <p>
   * Opens a {@link ReadableWorksheetGUIView} view and loads the requested file and evaluates it.
   * </p>
   *
   * @param args the command line arguments
   */
  private static void loadGui(String[] args) {

    FileReader worksheet;
    try {
      worksheet = new FileReader(args[1]);
    } catch (FileNotFoundException e) {
      System.out.print(e.getMessage());
      return;
    }
    Worksheet model = WorksheetReader.read(new Builder(), worksheet);
    String fullFilePath = args[1];
    String fileName;
    int lastDirWindows = fullFilePath.lastIndexOf("\\");
    if (lastDirWindows == -1) {

      fileName = fullFilePath;
    } else {

      fileName = fullFilePath.substring(lastDirWindows + 1);
    }
    WorksheetController controller = new WorksheetController(model);
    WorksheetView view = new ReadableWorksheetGUIView(fileName);
    controller.setView(view);
  }

  /**
   * ASSUMPTION: the args are in this form: -in some-filename -edit.
   * <p>
   * Opens a {@link WritableWorksheetGUIView} view and loads the requested file and evaluates it.
   * </p>
   *
   * @param args the command line arguments
   */
  private static void loadEdit(String[] args) {

    FileReader worksheet;
    try {
      worksheet = new FileReader(args[1]);
    } catch (FileNotFoundException e) {
      System.out.print(e.getMessage());
      return;
    }
    Worksheet model = WorksheetReader.read(new Builder(), worksheet);
    String fullFilePath = args[1];
    String fileName;
    int lastDirWindows = fullFilePath.lastIndexOf("\\");
    if (lastDirWindows == -1) {
      fileName = fullFilePath;
    } else {
      fileName = fullFilePath.substring(lastDirWindows + 1);
    }
    WorksheetController controller = new WorksheetController(model);
    WorksheetView view = new WritableWorksheetGUIView(fileName, fullFilePath);
    controller.setView(view);
  }

  private static void newEdit() {
    WorksheetModel model = Worksheet.builder().createWorksheet();
    WorksheetController controller = new WorksheetController(model);
    WorksheetView editView = new WritableWorksheetGUIView();
    controller.setView(editView);
  }
}