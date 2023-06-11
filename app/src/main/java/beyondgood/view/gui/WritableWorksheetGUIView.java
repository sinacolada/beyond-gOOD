package beyondgood.view.gui;

import beyondgood.controller.Features;
import beyondgood.model.Coord;
import beyondgood.model.formula.value.Value;
import beyondgood.view.WorksheetView;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A Graphics User Interface for the {@link beyondgood.model.WorksheetModel}. It
 * displays the evaluated values of the cells in a 2D grid fashion. You can scroll infinitely in
 * either direction to view the rest of the sheet. Additionally it allows the ability to edit or
 * delete any cell.
 */
public class WritableWorksheetGUIView extends JFrame implements WorksheetView {

  private BeyondGoodScrollPane scrollPane;
  private HeaderPanel header;
  private CustomMenuBar menuBar;
  private JFileChooser fileChooser;

  /**
   * Constructs an empty editable worksheet with the title "Sheet1" and the current directory as the
   * default one provided by the system.
   */
  public WritableWorksheetGUIView() {
    this("Sheet1", null);
  }

  /**
   * Constructs a new empty gui view named title.
   *
   * @param title the title of the window
   */
  public WritableWorksheetGUIView(String title, String filePath) {

    super();
    this.scrollPane = new BeyondGoodScrollPane();
    this.header = new HeaderPanel();
    this.menuBar = new CustomMenuBar();
    this.setTitle(title + " -BeyondGOOD");
    this.addReadCapabilities();

    this.fileChooser = new JFileChooser();
    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(".txt or .gOOD permitted",
        "txt", "gOOD");
    fileChooser.setFileFilter(fileFilter);
    if (filePath != null) {
      fileChooser.setCurrentDirectory(new File(filePath));
    }

    // Nest border layouts to effectively get "North West" and "North East"
    JPanel comp = new JPanel();
    comp.setLayout(new BorderLayout());
    comp.add(header, BorderLayout.NORTH);
    comp.add(scrollPane, BorderLayout.CENTER);
    this.setLayout(new BorderLayout());
    this.add(comp, BorderLayout.CENTER);
    this.add(menuBar, BorderLayout.NORTH);

    this.relayCurrentCoordPaneToHeader(); // Give the initial coord to the header

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  @Override
  public void refresh() {
    this.relayCurrentCoordPaneToHeader();
    this.repaint();
    //this.relayCurrentCoordPaneToHeader();
  }

  @Override
  public void makeVisible() {

    this.pack();
    this.setVisible(true);
    this.resetFocus(); // At start up set the focus to the grid not the text boxes
  }

  @Override
  public void setCells(Map<Coord, Value> values, Map<Coord, String> rawContents) {
    scrollPane.setValues(values);
    header.setRawContents(rawContents);
  }

  @Override
  public void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales) {

    scrollPane.setScales(colScales, rowScales);
  }

  @Override
  public void addFeatures(Features features) {

    header.addRawContentsKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          features.enterFormula(scrollPane.getCurrentCoord(), header.getRawContentsText());
          WritableWorksheetGUIView.this.scrollPane.adjustCurrentCoord(0, 1);
          WritableWorksheetGUIView.this.refresh();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          header.cancelFormula();
          WritableWorksheetGUIView.this.resetFocus();
          WritableWorksheetGUIView.this.refresh();
        }

      }
    });

    this.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
          //header.cancelFormula();
          features.deleteCell(scrollPane.getCurrentCoord());
        }
      }
    });

    addMenuBarListeners(features);

  }

  private void addMenuBarListeners(Features features) {

    menuBar.addSaveListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        int returnVal = fileChooser.showSaveDialog(WritableWorksheetGUIView.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

          File file = fileChooser.getSelectedFile();
          if (file == null) {
            JOptionPane.showMessageDialog(null, "No file chosen.", "Error - No File Chosen",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          Optional<String> maybePath = validateFilePath(file.getPath());
          if (maybePath.isEmpty()) {

            JOptionPane.showMessageDialog(null, "Wrong File Type.", "Error - Wrong File Type",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          String filePath = maybePath.get();
          System.out.println(filePath);
          String title = file.getName();
          if (file.exists()) {
            int replace = JOptionPane.showConfirmDialog(new JFrame("Overwrite?"),
                "" + title + " already exists. Would you like to replace it?");
            if (replace == JOptionPane.NO_OPTION) {
              JOptionPane.showMessageDialog(null, "Did not overwrite " + title + ".",
                  "Overwrite Cancelled",
                  JOptionPane.INFORMATION_MESSAGE);
              return;
            }
          }

          // At this it should be known that the entered file path is valid
          try {
            features.save(filePath);
          } catch (IOException ioe) {
            // There was an issue saving the file
            JOptionPane.showMessageDialog(null, "Failed to save file.", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
          WritableWorksheetGUIView.this.setTitle(title + " -BeyondGOOD");
          WritableWorksheetGUIView.this.refresh();
          JOptionPane.showMessageDialog(null,
              "Saved " + title + " successfully.",
              "Success", JOptionPane.INFORMATION_MESSAGE);
        }
      }

      private Optional<String> validateFilePath(String path) {

        String out = path;
        if (!path.contains(".")) {

          out += ".gOOD";
        }
        if (!out.endsWith(".gOOD") && !out.endsWith(".txt")) {

          return Optional.empty();
        }

        return Optional.of(out);
      }
    });

    menuBar.addOpenListener(e -> {
      int returnVal = fileChooser.showOpenDialog(WritableWorksheetGUIView.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        if (file == null) {
          JOptionPane.showMessageDialog(null, "No file chosen.", "Error - No File Chosen",
              JOptionPane.ERROR_MESSAGE);
        } else {
          try {
            features.load(file.getAbsolutePath());
            WritableWorksheetGUIView.this.setTitle(file.getName() + " -BeyondGOOD");
            WritableWorksheetGUIView.this.refresh();
            JOptionPane
                .showMessageDialog(null, "Opened " + file.getName() + " successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
          } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, "File not in valid worksheet format.",
                "Error - Invalid File Format", JOptionPane.ERROR_MESSAGE);
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Failed to open file.", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    menuBar.addControlsListener(e -> JOptionPane.showMessageDialog(null,
        "<html>"
            + "<p>Enter on Formula Box - change currently selected cell to contents entered and "
            + "update cell values</p>"
            + "<p>Escape on Formula Box - cancel contents entered.</p>"
            + "<p>Enter on Coord Box - move currently selected cell to entered coord.</p>"
            + "<p>Escape on Coord Box - cancel entered.</p>"
            + "<p>Arrow Keys - move currently selected cell by one in direction pressed.</p>"
            + "<p>Click Cell - move currently selected cell to clicked cell.</p>"
            + "</html>",
        "Controls",
        JOptionPane.INFORMATION_MESSAGE));

    menuBar.addInputsListener(e -> JOptionPane.showMessageDialog(null,
        "<html>"
            + "<p>Accepted Data Types - Words in Quotes (Strings), Numbers (Integers, Doubles), "
            + "Logical Units: true, false (Booleans)</p>"
            + "<p>"
            + "Accepted Formulas"
            + "<ul>"
            + "<li>Values - one of the accepted data types above.</li>"
            + "<li>References - a reference to another cell in the worksheet.</li>"
            + "<li>Function - a function that takes in data types, references, or other functions "
            + "as inputs. Format: =([NAME OF FUNCTION] [ARG1] [ARG2] ...)</li>"
            + "</ul>"
            + "</p>"
            + "</html>",
        "Inputs",
        JOptionPane.INFORMATION_MESSAGE));

  }

  @Override
  public void addReadCapabilities() {

    scrollPane.setHorizontalListener(e -> {
      scrollPane.adjustHorizontal(e.getValue());
      WritableWorksheetGUIView.this.refresh();
    });

    scrollPane.setVerticalListener(e -> {
      scrollPane.adjustVertical(e.getValue());
      WritableWorksheetGUIView.this.refresh();
    });

    scrollPane.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        scrollPane.setCurrentCoord(e.getX(), e.getY());
        WritableWorksheetGUIView.this.resetFocus();
        WritableWorksheetGUIView.this.refresh();
      }

      @Override
      public void mousePressed(MouseEvent e) {

        scrollPane.setDragClick(e.getPoint());
      }
    });

    scrollPane.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {

        scrollPane.drag(e.getPoint());
        WritableWorksheetGUIView.this.resetFocus();
        WritableWorksheetGUIView.this.refresh();
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        // Does nothing for resizing, you must drag
      }
    });

    header.addCoordKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          try {
            Coord inputCrd = WritableWorksheetGUIView.this.header.getCoordFromText();
            WritableWorksheetGUIView.this.scrollPane.moveScrollBarToNewCurrent(inputCrd);
          } catch (IllegalArgumentException ex) {
            // The input Coord was not valid, so do nothing
            return;
          }

          WritableWorksheetGUIView.this.resetFocus();
          WritableWorksheetGUIView.this.refresh();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          WritableWorksheetGUIView.this.resetFocus();
          WritableWorksheetGUIView.this.refresh();
        }
      }
    });

    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int dx = 0;
        int dy = 0;
        switch (e.getKeyCode()) {
          case KeyEvent.VK_RIGHT:
            dx = 1;
            break;
          case KeyEvent.VK_LEFT:
            dx = -1;
            break;
          case KeyEvent.VK_UP:
            dy = -1;
            break;
          case KeyEvent.VK_DOWN:
            dy = 1;
            break;
          default:
            return;
        }
        scrollPane.adjustCurrentCoord(dx, dy);
        WritableWorksheetGUIView.this.refresh();
      }
    });
  }

  @Override
  public void resetFocus() {
    this.setFocusable(true);
    this.requestFocus();
  }

  @Override
  public Map<Integer, Double> getColScales() {
    return scrollPane.getColScales();
  }

  @Override
  public Map<Integer, Double> getRowScales() {
    return scrollPane.getRowScales();
  }

  /**
   * Gives the header panel the currently selected coordinate that is stored in the scroll panel.
   */
  private void relayCurrentCoordPaneToHeader() {
    this.header.setCurrentCell(this.scrollPane.getCurrentCoord());
  }

}
