package beyondgood.model.formula.value;

/**
 * A {@link ValueVisitor} that gets the String representation of a {@link Value}.
 */
public class ValueToStringVisitor implements ValueVisitor<String> {

  @Override
  public String visitBlank() {
    return "";
  }

  @Override
  public String visitBoolean(boolean b) {
    return Boolean.toString(b).toUpperCase();
  }

  @Override
  public String visitDouble(double d) {
    return String.format("%f", d);
  }

  @Override
  public String visitError(String err) {
    return err;
  }

  @Override
  public String visitString(String val) {
    return "\"" + val + "\"";
  }
}
