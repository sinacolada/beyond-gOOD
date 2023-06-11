package beyondgood.model.formula;

import beyondgood.model.Coord;
import beyondgood.model.Cell;
import beyondgood.model.formula.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a formula that a {@link Cell} stores.
 */
public interface Formula {

  /**
   * Evaluates this Formula to a {@link Value}.
   *
   * @param refs      the Cells that this Formula refers to
   * @param cache     a cache of what the Cells at the Coords evaluate to (can just be empty map,
   *                  i.e. {@code new HasMap<>()}, but may hinder performance)
   * @param ancestors the Coords of the Cells that are currently being evaluated in a tree of
   *                  evaluation
   * @return what this Formula evaluates to
   */
  Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors);


  /**
   * Evaluates all the parts of this Formula to a {@code List<Value>}.
   *
   * @param refs      the Cells that this Formula refers to
   * @param cache     a cache of what the Cells at the Coords evaluate to (can just be empty map,
   *                  i.e. {@code new HasMap<>()}, but may hinder performance)
   * @param ancestors the Coords of the Cells that are currently being evaluated in a tree of
   *                  evaluation
   * @return what this Formula parts evaluates to
   */
  List<Value> evaluateParts(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors);

}
