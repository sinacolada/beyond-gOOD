package beyondgood.model.formula.function;

import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;
import beyondgood.model.formula.value.ValueGetterVisitor.GetDouble;
import beyondgood.model.formula.value.ValueGetterVisitor.GetString;

import java.util.List;
import java.util.function.Function;

/**
 * A function object that repeats the first String argument the floor of the second number argument
 * times.
 */
public class Repeat implements Function<List<Value>, Value> {

  @Override
  public Value apply(List<Value> arguments) {

    if (arguments.size() != 2) {
      return ErrorValue.ARG;
    }

    String str;
    double repeatNum;

    try {

      str = arguments.get(0).accept(new GetString());
      repeatNum = arguments.get(1).accept(new GetDouble());
    } catch (IllegalArgumentException | UnsupportedOperationException e) {
      return ErrorValue.ARG;
    }

    if (repeatNum < 0) {
      return ErrorValue.ARG;
    }

    StringBuilder out = new StringBuilder();
    for (int i = 0; i < (int) repeatNum; i++) {
      out.append(str);
    }

    return new StringValue(out.toString());
  }
}
