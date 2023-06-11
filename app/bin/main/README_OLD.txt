Cell structure class diagram:
                                   Cell
                                    ^
                                    |
                                    |
                               FormulaCell
                  _ _ _ _  _ _ ^    ^     ^_ _ _ _ _ _ _ _ _ _ _ _ _ _ _
                 |                  |                   |               |
                 |                  |                   |               |
             ValueCell        ReferenceCell       FunctionCell  RectangularReferenceCell
                ^                                       ^
                |                                       |
          _ _ _ | _ _ _ _                       _ _ _ _ | _ _ _ _ _ _ _
         |      |        |                     |        |        |     |
NumberCell  StringCell  BooleanCell           Sum    Product  LessThan Repeat

For our cell structure we chose to have one single Cell interface. We chose to mandate all cells
should support evaluation (but not necessarily see RectangularReferenceCell) because a Cell should
be able to be evaluated without knowledge of a Worksheet. Additionally we choice to make a
SupportedCell ENUM that enumerates the possible types of Cells. We then added a getType() method in
the interface which allowed use to determine the type of any Cell. Because Cell does most of the
work, the worksheet implementation was somewhat simpler.

We choice to represent a sheet in Worksheet as a Map<Coord, Cell> instead of using something like
a 2D array. This allows the best memory usage as you only have to keep track of the Cells that only
contains values. We choice this because the model shouldn't have to care about Cells that contain
nothing. Using a Map also made representing empty Cells as null very convenient as map.get(Coord)
would always return what we wanted, null if no cell exists at the Coord, or the actual cell there.
Many other methods like update cell are also very simple with a map. We chose to cache values
after they are evaluated in the model and then pass it to the Cell when the evaluation is delegated.
This allows for more efficient evaluation. Additionally we hae one more map that stores what each
Coord is used in. This allows for the necessary data flow model as when one cell is updated all
the cell it is used in are also updated.