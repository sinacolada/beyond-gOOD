package beyondgood.model.formula.value;

import beyondgood.model.formula.Formula;

/**
 * Represents a {@link Formula} that holds some kind of data.
 */
public interface Value extends Formula {

  <R> R accept(ValueVisitor<R> visitor);
}
