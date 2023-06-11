package beyondgood.view.gui;

import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * A custom menu bar used to open files and get helpful information in a {@link
 * WritableWorksheetGUIView}.
 */
public class CustomMenuBar extends JMenuBar {

  private JMenuItem save;
  private JMenuItem open;
  private JMenuItem controls;
  private JMenuItem inputs;

  /**
   * Constructs a menu bar with a "File" and "Help" options.
   */
  public CustomMenuBar() {
    JMenu file = new JMenu("File");
    Image srcSaveImg = new ImageIcon("resources\\view_images\\save_icon.png").getImage();
    Image resizeSaveImg = srcSaveImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    this.save = new JMenuItem("Save", new ImageIcon(resizeSaveImg));
    Image srcOpenImg = new ImageIcon("resources\\view_images\\open_icon.png").getImage();
    Image resizeOpenImg = srcOpenImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    this.open = new JMenuItem("Open", new ImageIcon(resizeOpenImg));
    file.add(this.save);
    file.add(this.open);
    this.add(file);
    JMenu help = new JMenu("Help");
    Image srcControlsImg = new ImageIcon("resources\\view_images\\controls_icon.png").getImage();
    Image resizeControlsImg = srcControlsImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    this.controls = new JMenuItem("Controls", new ImageIcon(resizeControlsImg));
    Image srcInputsImg = new ImageIcon("resources\\view_images\\inputs_icon.png").getImage();
    Image resizeInputsImg = srcInputsImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    this.inputs = new JMenuItem("Inputs", new ImageIcon(resizeInputsImg));
    help.add(this.controls);
    help.add(this.inputs);
    this.add(help);
  }

  /**
   * Adds an action listener to the save option under file.
   *
   * @param l the action listener
   */
  public void addSaveListener(ActionListener l) {
    save.addActionListener(l);
  }

  /**
   * Adds an action listener to the open option under file.
   *
   * @param l the action listener
   */
  public void addOpenListener(ActionListener l) {
    open.addActionListener(l);
  }

  /**
   * Adds an action listener to the controls option under help.
   *
   * @param l the action listener
   */
  public void addControlsListener(ActionListener l) {
    controls.addActionListener(l);
  }

  /**
   * Adds an action listener to the inputs option under help.
   *
   * @param l the action listener
   */
  public void addInputsListener(ActionListener l) {
    inputs.addActionListener(l);
  }
}
