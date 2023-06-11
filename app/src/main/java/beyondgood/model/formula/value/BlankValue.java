package beyondgood.model.formula.value;

import java.util.Objects;

/**
 * A {@link Value} that holds nothing.
 */
public class BlankValue extends AbstractValue {

  @Override
  public <R> R accept(ValueVisitor<R> visitor) {
    return visitor.visitBlank();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
