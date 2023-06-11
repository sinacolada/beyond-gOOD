package beyondgood.model.formula.function;

import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueGetterVisitor.GetDouble;

import java.util.List;
import java.util.function.Function;

/**
 * A function object takes in two number arguments and determines if the first arg is less than the
 * second.
 */
public class LessThan implements Function<List<Value>, Value> {

  @Override
  public Value apply(List<Value> arguments) {

    if (arguments.size() != 2) {

      return ErrorValue.ARG;
    }

    double num1;
    double num2;

    try {

      num1 = arguments.get(0).accept(new GetDouble());
      num2 = arguments.get(1).accept(new GetDouble());
    } catch (IllegalArgumentException | UnsupportedOperationException e) {

      return ErrorValue.ARG;
    }

    return new BooleanValue(num1 < num2);
  }
}
