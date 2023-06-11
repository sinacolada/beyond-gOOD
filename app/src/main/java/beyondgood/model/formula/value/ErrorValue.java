package beyondgood.model.formula.value;

import java.util.Objects;

/**
 * A {@link Value} that holds a String error message.
 */
public final class ErrorValue extends AbstractValue {

  public static final ErrorValue CIRC = new ErrorValue("#CIRC!");
  public static final ErrorValue REF = new ErrorValue("#REF!");
  public static final ErrorValue ARG = new ErrorValue("#ARG!");
  public static final ErrorValue NAME = new ErrorValue("#NAME?");
  public static final ErrorValue DIV0 = new ErrorValue("#DIV/0!");
  public static final ErrorValue NUM = new ErrorValue("#NUM!");

  private String err;

  private ErrorValue(String err) {
    super();
    this.err = err;
  }

  @Override
  public <R> R accept(ValueVisitor<R> visitor) {
    return visitor.visitError(err);
  }

  @Override
  public String toString() {
    return this.accept(new ValueToStringVisitor());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorValue that = (ErrorValue) o;
    return this.err.equals(that.err);
  }

  @Override
  public int hashCode() {
    return Objects.hash(err);
  }
}
