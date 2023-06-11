So we did a complete redesign of our model, here are the changes. Refers to the README_OLD.txt to
see more specific about our previous design:

First these were the issues of the previous model
  - The existence of getType() for value is inherently bad
  - If a reference Cell holds another Cell then updating Cells is difficult to achieve
  - A Formula shouldn't be a Cell, there is a logical contention with this.
  - You shouldn't needs to have a RectangularReferenceCell, it should be possible to describe both
    single and multiple references with one class
  - Evaluation takes too much time without a cache
  - We made the assumption that a file has to be read in topological order rather than any order.

Most of these issues are for Cells, but not the Worksheet. Specifically our designing of a reference
was the bottleneck for our poor design. We felt our Worksheet was well designed, so there were
minimal to no changes for it. Here are the solution for these issues with our new model.
  - getType() is not necessary with a ValueVisitor
  - A reference is a formula that holds a List<Coord> of the Coords of the Cells it refers to. This
    makes making a reference very easy. And evaluation does not need to update any Cells.
  - A Cell now has a Formula which lets us store the raw contents inside a Cell instead of a Formula
  - Only one class for a Reference, can refer to any arbitrary size / length of references
  - Evaluation now takes in a cache of previous evaluations, but is not necessary. The cache is
    stored in the model, and Cells have access to an alias of it during evaluation
  - Since a Reference just has a Coord, you can now read the file in any order

To do evaluation we now pass an unmodifiable version of our HashMap<Coord, Cell> representing the
entire spreadsheet, an aliased HashMap<Coord, Value> of cached evaluations, and a HasSet<Coord> of
the Coords of that are the ancestors of this evaluation in a tree of evaluation. This is needed for
cycle detection.

Here is the new class diagram for Cells:

  A Cell has a Formula. A Value, BeyondGoodFunction and Reference is a Formula. Values are either
  StringValue, BooleanValue, DoubleValue, or BlankValue (for now). A BeyondGoodFunction has a
  Function<List<Value>, Value>. Some function objects we made are Sum, Product, LessThan and Repeat.
  A Reference has a List<Coord> of all of its references.

  Diamonds are "has a" and arrows are "is a"

  Cell<>---------------Formula
                          ^
                          |
            _ _ _ _ _ _ _ | _ _ _ _ _ _ _
            |             |              |
    AbstractValue      Reference        BeyondGoodFunction <>--------- Function<List<Value>, Value>
         ^                ^                                                       ^
         |                v                                                       |
         |                |                                                       |
         |             List<Coord>                                  _ _ _ _ _ _ _ | _ _ _ _ _ _
         |                                                          |      |           |        |
   _ _ _ | _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _                        Sum     Product   LessThan  Repeat
  |         |  |         |    |          |
DoubleValue | BlankValue | BlankValue  ErrorValue
        StringValue    BooleanValue


Here is our design for the gui view:
  - We said the only thing any view should need to fully draw a spreadsheet is a Map<Coord, Value>
    of the values of each Cell and a Map<Coord, String> of the raw contents of the Cells. Naturally
    these are observers within the model.
  - We made our scroll pane for the gui view and this is how the structure is set up.
    A WorksheetGuiView is a JFrame, WorksheetView and has a
        - BeyondGoodScrollPane is a JPanel and has
          - WorksheetPanel is a JPanel and has a
              - HeaderPanel is a JPanel and has
                  - JTextField for the Coord of the current selected Cell
                  - JTextField for the raw contents of the current selected Cell
          - JScrollBar for vertical scrolling
          - JScrollbar for horizontal scrolling

All the drawing of cells is done within the paintComponent of WorksheetPanel with the casted
Graphics2D object.

Here is some extra things you can do in the view:
  - The current selected cell is outlined with a green rectangle much like beyondgood.
  - The row / column headers of the selected cell have fancier graphics, again much like beyondgood
  - You can infinite scroll with the scroll bars
  - You can click anywhere on the cells and the selected cell will move to where the mouse is
  - The current selected cell is displayed in the top left and the raw contents of the selected
    cell are displayed in the top center of the screen, again much like beyondgood