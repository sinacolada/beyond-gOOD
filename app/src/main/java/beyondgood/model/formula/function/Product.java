package beyondgood.model.formula.function;

import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueGetterVisitor.GetDouble;

import java.util.List;
import java.util.function.Function;

/**
 * A function object that takes in a variable amount of arguments and multiplies all the number
 * arguments together. Non-number inputs that are not errors are ignored.
 */
public class Product implements Function<List<Value>, Value> {

  @Override
  public Value apply(List<Value> arguments) {

    if (arguments.size() == 0) {

      return new DoubleValue(0);
    }

    double prod = 1;
    for (Value arg : arguments) {

      try {
        prod *= arg.accept(new GetDouble());
      } catch (IllegalArgumentException e) {
        // PRODUCT ignores bad values
      } catch (UnsupportedOperationException e) {

        return ErrorValue.ARG;
      }
    }
    return new DoubleValue(prod);
  }
}
