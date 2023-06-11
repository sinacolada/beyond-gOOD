package beyondgood.view.gui;

import beyondgood.controller.Features;
import beyondgood.model.Coord;
import beyondgood.model.formula.value.Value;
import beyondgood.view.WorksheetView;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JFrame;

/**
 * A Graphics User Interface for the {@link beyondgood.model.WorksheetModel}. It
 * displays the evaluated values of the cells in a 2D grid fashion. You can scroll infinitely in
 * either direction to view the rest of the sheet.
 */
public class ReadableWorksheetGUIView extends JFrame implements WorksheetView {

  private BeyondGoodScrollPane scrollPane;

  /**
   * Constructs a new empty read-only gui view named "EmptySheet".
   */
  public ReadableWorksheetGUIView() {

    this("EmptySheet");
  }

  /**
   * Constructs a new empty read-only gui view named title.
   *
   * @param title the title of the window
   */
  public ReadableWorksheetGUIView(String title) {

    super();
    this.scrollPane = new BeyondGoodScrollPane();
    this.setTitle(title + " -BeyondGOOD (Final)");
    this.add(scrollPane);
    this.addReadCapabilities();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  @Override
  public void refresh() {

    this.repaint();
  }

  @Override
  public void makeVisible() {
    this.pack();
    this.setVisible(true);
  }

  @Override
  public void setCells(Map<Coord, Value> values, Map<Coord, String> rawContents) {
    scrollPane.setValues(values);
  }

  @Override
  public void setScales(Map<Integer, Double> colScales, Map<Integer, Double> rowScales) {

    scrollPane.setScales(colScales, rowScales);
  }

  @Override
  public void addFeatures(Features features) {
    // Readable does not have any editable features, so does nothing.

  }

  @Override
  public void addReadCapabilities() {

    scrollPane.setHorizontalListener(e -> {
      scrollPane.adjustHorizontal(e.getValue());
      ReadableWorksheetGUIView.this.refresh();
    });

    scrollPane.setVerticalListener(e -> {
      scrollPane.adjustVertical(e.getValue());
      ReadableWorksheetGUIView.this.refresh();
    });

    scrollPane.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        scrollPane.setCurrentCoord(e.getX(), e.getY());
        ReadableWorksheetGUIView.this.refresh();
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
        ReadableWorksheetGUIView.this.refresh();
      }
    });
  }

  @Override
  public void resetFocus() {
    // do nothing - focus is already on the one component within this frame
  }

  @Override
  public Map<Integer, Double> getColScales() {
    return scrollPane.getColScales();
  }

  @Override
  public Map<Integer, Double> getRowScales() {
    return scrollPane.getRowScales();
  }
}
