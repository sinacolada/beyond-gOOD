package beyondgood.model.formula.function;

import beyondgood.model.formula.value.Value;
import beyondgood.model.Worksheet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents all the supported {@link BeyondGoodFunction}s in a {@link Worksheet}. To add a new
 * function, simply create a new enum constant and provide the String representation of the function
 * as well as the appropriate function object.
 */
public enum SupportedFunctions {

  SUM("SUM", new Sum()), PRODUCT("PRODUCT", new Product()), LESS_THAN("<", new LessThan()), REPEAT(
      "REPEAT", new Repeat());

  /**
   * Constructs a {@link SupportedFunctions} provided a String representation of a function and the
   * appropriate function object.
   *
   * @param str  the String representation of the function name
   * @param func the function object
   */
  SupportedFunctions(String str, Function<List<Value>, Value> func) {

    this.str = str;
    this.func = func;

  }

  private final String str;
  private final Function<List<Value>, Value> func;

  /**
   * Gets a Map of String representation of functions to the actual function object they represent.
   *
   * @return the map of String representation to function object
   */
  public static Map<String, Function<List<Value>, Value>> getFuncs() {

    return Arrays.stream(SupportedFunctions.values())
        .collect(Collectors.toMap(supFunc -> supFunc.str, supFunc -> supFunc.func));
  }

}
