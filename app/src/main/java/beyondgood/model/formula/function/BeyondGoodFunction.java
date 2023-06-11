package beyondgood.model.formula.function;

import beyondgood.model.Coord;
import beyondgood.model.Cell;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueVisitor;
import beyondgood.model.Worksheet;
import beyondgood.model.formula.value.ErrorValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a {@link Formula} that is a function that operates on {@link Formula}s used in a
 * {@link Worksheet}.
 */
public class BeyondGoodFunction implements Formula {

  private List<Formula> arguments;
  private Function<List<Value>, Value> function;

  /**
   * Constructs a function given the arguments and the function object.
   *
   * @param arguments the arguments for this function
   * @param function  the function object that is called on the evaluated arguments
   */
  public BeyondGoodFunction(List<Formula> arguments, Function<List<Value>, Value> function) {
    this.arguments = arguments;
    this.function = function;
  }

  @Override
  public Value evaluate(Map<Coord, Cell> refs, Map<Coord, Value> cache, Set<Coord> ancestors) {

    return this.evaluateParts(refs, cache, ancestors).get(0);
  }

  @Override
  public List<Value> evaluateParts(Map<Coord, Cell> refs, Map<Coord, Value> cache,
      Set<Coord> ancestors) {

    List<Value> evaluatedArguments = new ArrayList<>();
    for (Formula arg : arguments) {

      Set<Coord> ancestorCopy = new HashSet<>(ancestors);
      List<Value> evalArg = arg.evaluateParts(refs, cache, ancestorCopy);
      for (Value val : evalArg) {

        if (val.accept(new IsError())) {

          return evalArg;
        }
        evaluatedArguments.add(val);
      }
    }

    return Collections.singletonList(function.apply(evaluatedArguments));
  }

  /**
   * A nested {@link ValueVisitor} that determines if a {@link Value} is a {@link ErrorValue}. Used
   * to short circuit {@code evaluate} as soon as an {@link ErrorValue} is seen.
   */
  private static class IsError implements ValueVisitor<Boolean> {

    @Override
    public Boolean visitBlank() {
      return false;
    }

    @Override
    public Boolean visitBoolean(boolean b) {
      return false;
    }

    @Override
    public Boolean visitDouble(double d) {
      return false;
    }

    @Override
    public Boolean visitError(String err) {
      return true;
    }

    @Override
    public Boolean visitString(String val) {
      return false;
    }
  }
}
