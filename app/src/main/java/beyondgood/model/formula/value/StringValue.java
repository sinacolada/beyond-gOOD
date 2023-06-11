package beyondgood.model.formula.value;

import java.util.Objects;

/**
 * A {@link Value} that holds a String.
 */
public class StringValue extends AbstractValue {

  private String s;

  /**
   * Constructs a StringValue that holds the given String.
   *
   * @param s the String this value holds
   */
  public StringValue(String s) {

    super();
    this.s = s;
  }

  @Override
  public <R> R accept(ValueVisitor<R> visitor) {
    return visitor.visitString(s);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StringValue that = (StringValue) o;
    return this.s.equals(that.s);
  }

  @Override
  public int hashCode() {
    return Objects.hash(s);
  }
}
