package beyondgood.model.formula.function;

import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueGetterVisitor.GetDouble;

import java.util.List;
import java.util.function.Function;

/**
 * A function object that takes in a variable amount of arguments and sums all the number
 * arguments together. Non-number inputs that are not errors are ignored.
 */
public class Sum implements Function<List<Value>, Value> {

  @Override
  public Value apply(List<Value> arguments) {

    double sum = 0;
    for (Value arg : arguments) {

      try {
        sum += arg.accept(new GetDouble());
      } catch (IllegalArgumentException e) {

        // SUM ignores bad inputs
      } catch (UnsupportedOperationException e) {

        return ErrorValue.ARG;
      }
    }
    return new DoubleValue(sum);
  }
}
