package beyondgood.model.formula.value;

/**
 * A abstract {@link ValueVisitor} that gets the inner contents of a {@link Value}.
 *
 * @param <R> the type of inner content being requested
 */
public abstract class ValueGetterVisitor<R> implements ValueVisitor<R> {

  @Override
  public abstract R visitBlank();

  @Override
  public R visitBoolean(boolean b) {

    throw new IllegalArgumentException("Unexpected boolean");
  }

  @Override
  public R visitDouble(double d) {
    throw new IllegalArgumentException("Unexpected double");
  }

  @Override
  public R visitError(String err) {
    throw new UnsupportedOperationException("Cannot get value of Error");
  }

  @Override
  public R visitString(String val) {
    throw new IllegalArgumentException("Unexpected String");
  }

  /**
   * A nested visitor to get the double in a {@link DoubleValue}.
   */
  public static class GetDouble extends ValueGetterVisitor<Double> {

    @Override
    public Double visitBlank() {
      return (double) 0;
    }

    @Override
    public Double visitDouble(double d) {
      return d;
    }

  }

  /**
   * A nested visitor to get the boolean in a {@link BooleanValue}.
   */
  public static class GetBoolean extends ValueGetterVisitor<Boolean> {

    @Override
    public Boolean visitBlank() {
      return false;
    }

    @Override
    public Boolean visitBoolean(boolean b) {
      return b;
    }
  }


  /**
   * A nested visitor to get the String in a {@link StringValue}.
   */
  public static class GetString extends ValueGetterVisitor<String> {

    @Override
    public String visitBlank() {
      return "";
    }

    @Override
    public String visitString(String val) {
      return val;
    }
  }

}

