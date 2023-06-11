package beyondgood.model.formula.value;

import java.util.Objects;

/**
 * A {@link Value} that holds a double.
 */
public class DoubleValue extends AbstractValue {

  private double d;

  public DoubleValue(double d) {
    super();
    this.d = d;
  }

  @Override
  public <R> R accept(ValueVisitor<R> visitor) {
    return visitor.visitDouble(d);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DoubleValue that = (DoubleValue) o;
    return Double.compare(that.d, d) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(d);
  }
}
