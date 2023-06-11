package beyondgood.model.formula.value;

import beyondgood.model.Cell;
import beyondgood.model.Coord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract {@link Value} to make creating {@link Value}s easier.
 */
abstract class AbstractValue implements Value {

  @Override
  public abstract <R> R accept(ValueVisitor<R> visitor);

  @Override
  public Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors) {

    return this;
  }

  @Override
  public String toString() {
    return this.accept(new ValueToStringVisitor());
  }

  @Override
  public List<Value> evaluateParts(Map<Coord, Cell> refs, Map<Coord, Value> cache,
      Set<Coord> ancestors) {
    return new ArrayList<>(Collections.singletonList(this));
  }
}
