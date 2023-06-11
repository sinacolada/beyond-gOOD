Minor Changes from Last Assignment + Design Choices for Writable View + Controller

Issues Resolved from Previous Model:

- Updating a cell was only removing the updated cell from the cache, but it must remove every cell
  the updated cell is used in from the cache as well so they are re-evaluated.
- We achieved this by initially just resetting the cache and re-evaluating everything,
  but have optimized it with a DFS-based function to remove deep cell references of the updated cell
  from the cache only, and leave non-referenced cells evaluated in the cache,
  thus speeding up evaluation time greatly.
- Fixed arrow keys not working because our text field was the focus by using a resetFocus method
  (explained in controller section).

Issues Resolved from Previous Readable View:
- Fixed a very annoying bug where selected cell border was being drawn. There were if statements to
  check that the clicked coordinates weren't on a non-selectable border or header, but these were
  checking pixel coordinates, not logical coordinates, which made it impossible to select a cell
  if you scrolled far enough.
- We achieved this by using logical coordinates and optimizing the if statements so the selected
  cell border would draw properly no matter how far we scrolled.

Design of Writable View:
- Added a header panel, which contains 2 text fields.
- A text field which displays the current cell coordinate.
- A text field which shows the "raw contents" (string formula) of the current cell.
- View action where entering a valid coordinate in the coordinate text field and pressing enter
  shifts view to that coordinate being the top left coordinate on the screen (does nothing if
  invalid).
- Infinite scrolling (we implemented this in the last assignment).
- Selecting a cell (we implemented the visual of this in the last assignment).
- FROM ASSIGNMENT 6 README:
  --Here is some extra things you can do in the view:
    - The current selected cell is outlined with a green rectangle much like beyondgood.
    - The row / column headers of the selected cell have fancier graphics, again much like beyondgood
    - You can infinite scroll with the scroll bars
    - You can click anywhere on the cells and the selected cell will move to where the mouse is
    - The current selected cell is displayed in the top left and the raw contents of the selected
      cell are displayed in the top center of the screen, again much like beyondgood

Design of Controller:
- Callbacks were made using a features interface which our controller implemented.
- Our Writable view had an addFeatures(Features features) method which added all of these features
  to their respective listeners.
- These events were created using lambdas -> Adapters to override needed functionality
  in the mouse and key listener classes.
- The controller implements features class would the set the view and add itself as the view's
  features.
- It overrides all features to make necessary model adjustments to the view given the parameters
  required passed from the view events (callback). Therefore, our controller does NOT need to know
  anything about Swing to work successfully (thus it doesn't import or subclass any swing classes
  or methods).
- A resetFocus method was also used to set the focus of the frame to the respective component which
  had the EventListener for any of the frame events.
- Features: - delete key to reset a cell
            - enter key after formula is entered to update the sheet and re-evaluate formulas
              where updated cell was used
            - saving and loading a file from within our gui view (implementing right now)