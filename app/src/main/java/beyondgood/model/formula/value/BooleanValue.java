package beyondgood.model.formula.value;

import java.util.Objects;

/**
 * A {@link Value} that holds a boolean.
 */
public class BooleanValue extends AbstractValue {

  private boolean b;

  /**
   * Constructs a boolean value given a boolean.
   * @param b the boolean for this \value.
   */
  public BooleanValue(boolean b) {
    super();
    this.b = b;
  }

  @Override
  public <R> R accept(ValueVisitor<R> visitor) {
    return visitor.visitBoolean(b);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BooleanValue that = (BooleanValue) o;
    return b == that.b;
  }

  @Override
  public int hashCode() {
    return Objects.hash(b);
  }
}
