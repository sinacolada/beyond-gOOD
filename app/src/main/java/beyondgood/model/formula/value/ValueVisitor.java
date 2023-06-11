package beyondgood.model.formula.value;

/**
 * An abstracted function object for processing any {@link Value}.
 *
 * @param <R> The return type of this function
 */
public interface ValueVisitor<R> {

  /**
   * Process a {@link BlankValue}.
   *
   * @return what should be returned for blank values.
   */
  R visitBlank();

  /**
   * Process a {@link BooleanValue} given the boolean it stores.
   *
   * @param b the boolean the BooleanValue stores
   * @return what should be returned for boolean values
   */
  R visitBoolean(boolean b);

  /**
   * Process a {@link DoubleValue} given the double it stores.
   *
   * @param d the double the DoubleValue stores
   * @return what should be returned for double values
   */
  R visitDouble(double d);

  /**
   * Process a {@link ErrorValue} given the String error message it stores.
   *
   * @param err the String error message the ErrorValue stores
   * @return what should be returned for error values
   */
  R visitError(String err);

  /**
   * Process a {@link StringValue} given the String it stores.
   *
   * @param val the String the StringValue stores
   * @return what should be returned for String values
   */
  R visitString(String val);

}
