package beyondgood.model.formula.reference;

import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a {@link Formula} that references an arbitrary amount of {@link Cell}s in a Worksheet.
 * That is, refer to an infinite set of references.
 */
public abstract class AbstractInfiniteReference implements Formula {

  @Override
  public Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors) {
    return ErrorValue.REF;
  }

  @Override
  public List<Value> evaluateParts(Map<Coord, Cell> refs, Map<Coord, Value> cache,
      Set<Coord> ancestors) {

    Predicate<Coord> isInRef = this.isInReference();

    List<Value> out = new ArrayList<>();
    if (ancestors.stream().anyMatch(isInRef)) {

      out.add(ErrorValue.CIRC);
      return out;
    }

    for (Entry<Coord, Cell> entry : refs.entrySet()) {

      Coord entryCoord = entry.getKey();

      if (isInRef.test(entryCoord)) {

        if (cache.containsKey(entryCoord)) {

          out.add(cache.get(entryCoord));
        } else {

          Set<Coord> ancestorsCopy = new HashSet<>(ancestors);
          ancestorsCopy.add(entryCoord);
          out.add(entry.getValue().evaluate(refs, cache, ancestorsCopy));
        }
      }
    }

    return out;
  }

  /**
   * A predicate to determine if a given {@link Coord} is contained within this reference.
   *
   * @return a predicate for determine if a coord is in this reference
   */
  protected abstract Predicate<Coord> isInReference();
}
