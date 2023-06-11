package beyondgood.model.formula.reference;

import beyondgood.model.Coord;
import beyondgood.model.Cell;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import beyondgood.model.Worksheet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a {@link Formula} that refers to {@link Cell} within a {@link Worksheet}.
 */
public class Reference implements Formula {

  private List<Coord> references;
  // Referencing a cell not in a formula that has yet to be defined defaults to being 0
  private static final Value DEFAULT_REF_EVAL = new DoubleValue(0);

  /**
   * Constructs a Reference given the the two corners of a rectangular region of reference.
   *
   * @param topLeft  the coordinate of the top left reference
   * @param botRight the coordinate of the bottom right reference
   */
  public Reference(Coord topLeft, Coord botRight) {
    // check for order, beyondgood always stores topLeft then bottomRight
    this.references = new ArrayList<>();
    for (int i = topLeft.col; i <= botRight.col; i++) {
      for (int j = topLeft.row; j <= botRight.row; j++) {

        references.add(new Coord(i, j));
      }
    }
  }

  @Override
  public List<Value> evaluateParts(Map<Coord, Cell> refs, Map<Coord, Value> cache,
      Set<Coord> ancestors) {

    List<Value> out = new ArrayList<>();
    if (!Collections.disjoint(references, ancestors)) {

      out.add(ErrorValue.CIRC);
      return out;
    }

    for (Coord coord : references) {
      if (cache.containsKey(coord)) {

        out.add(cache.get(coord));
      } else {

        Set<Coord> ancestorsCopy = new HashSet<>(ancestors);
        ancestorsCopy.add(coord);
        Cell cell = refs.get(coord);
        if (cell == null) {

          out.add(new BlankValue());
        } else {
          Value val = cell.evaluate(refs, cache, ancestorsCopy);
          cache.put(coord, val);
          out.add(val);
        }
      }
    }

    return out;
  }


  @Override
  public Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors) {

    if (references.size() != 1) {
      return ErrorValue.REF;
    }

    if (!Collections.disjoint(references, ancestors)) {
      return ErrorValue.CIRC;
    }

    Coord oneRef = references.get(0);
    if (cache.containsKey(oneRef)) {
      return cache.get(oneRef);
    }

    Set<Coord> ancestorsCopy = new HashSet<>(ancestors);
    ancestorsCopy.add(oneRef);
    Cell cell = refs.get(oneRef);
    if (cell == null) {
      return DEFAULT_REF_EVAL;
    }
    Value val = cell.evaluate(refs, cache, ancestorsCopy);
    cache.put(oneRef, val);
    return val;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Reference reference = (Reference) o;
    return Arrays.equals(references.toArray(), reference.references.toArray());
  }

  @Override
  public int hashCode() {
    return Objects.hash(references);
  }
}
