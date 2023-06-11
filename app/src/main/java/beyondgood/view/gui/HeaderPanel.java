package beyondgood.view.gui;

import beyondgood.model.Coord;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Represents a header panel that display the coordinate and the raw contents of the currently
 * selected cell.
 */
public class HeaderPanel extends JPanel {

  private JTextField coordText;
  private JTextField rawContentsText;
  private Map<Coord, String> rawContents;

  /**
   * Constructs a header with nothing in the text boxes.
   */
  public HeaderPanel() {

    this.coordText = new JTextField();
    this.rawContentsText = new JTextField();
    this.rawContents = new HashMap<>();
    GridBagConstraints c = new GridBagConstraints();
    this.setLayout(new GridBagLayout());

    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(5, 5, 5, 5);

    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.04;
    this.add(coordText, c);

    c.gridx = 1;
    c.weightx = 1;
    this.add(rawContentsText, c);
  }

  /**
   * Sets the text for the text box that shows the current selected coordinate.
   *
   * @param coord the String representation of the Coord
   */
  public void setCoordText(String coord) {
    coordText.setText(coord);
  }

  /**
   * Sets the text for the text box that shows the current selected raw contents.
   *
   * @param coord the String representation of the raw contents.
   */
  public void setRawContentsText(String coord) {
    rawContentsText.setText(coord);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(new Color(230, 230, 230));
    g.fillRect(0, 0, getWidth(), getHeight());
  }

  /**
   * Sets the raw contents that this header panel stores.
   *
   * @param contents a map of Coord to a String representation of the the raw contents of the Cells
   */
  public void setRawContents(Map<Coord, String> contents) {
    this.rawContents = contents;
  }

  /**
   * Sets the current coordinate and raw contents at that coord that this header panel stores.
   *
   * @param c the coordinate that should be displayed
   */
  public void setCurrentCell(Coord c) {
    this.coordText.setText(c.toString());
    this.rawContentsText.setText(this.rawContents.get(c));
  }

  /**
   * Adds a key listener to the Coord text box to detect when a valid Coord is inputted.
   *
   * @param l the key listener
   */
  public void addCoordKeyListener(KeyListener l) {
    coordText.addKeyListener(l);
  }

  /**
   * Adds a key listener to the raw contents text box to detect something is inputted / canceled.
   *
   * @param l the key listener
   */
  public void addRawContentsKeyListener(KeyListener l) {
    rawContentsText.addKeyListener(l);
  }

  /**
   * Gets the String that the current raw contents text box has.
   *
   * @return the String that is currently in the raw contents text box
   */
  public String getRawContentsText() {
    return rawContentsText.getText();
  }

  /**
   * Gets the Coord that the current raw contents text box has.
   *
   * @return the String that is currently in the raw contents text box
   * @throws IllegalArgumentException if the text in the Coord text box cannot be converted to a
   *                                  valid coordinate.
   */
  public Coord getCoordFromText() {
    return new Coord(coordText.getText());
  }

  /**
   * Signals to the raw contents text box to clear its text.
   */
  public void cancelFormula() {
    this.rawContentsText.setText(null);
  }

}
