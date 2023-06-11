package beyondgood.model.formula.value;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

/**
 * To test common methods among all value formulas.
 */
public abstract class ValueTest {

  protected abstract Value getValue();

  @Test
  public void testEvaluate() {

    Value expected = getValue();

    Value actual = expected.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>());

    assertSame(expected, actual);
  }

  @Test
  public void testEvaluateParts() {

    Value val = getValue();
    List<Value> expected = new ArrayList<>(Collections.singletonList(val));
    List<Value> actual = val.evaluateParts(new HashMap<>(), new HashMap<>(), new HashSet<>());

    assertArrayEquals(expected.toArray(), actual.toArray());
  }

  /**
   * To test methods unique to a BlankValue.
   */
  public static class BlankValueTest extends ValueTest {


    @Override
    protected Value getValue() {
      return new BlankValue();
    }
  }

  /**
   * To test methods unique to a BlankValue.
   */
  public static class BooleanValueTest extends ValueTest {


    @Override
    protected Value getValue() {
      return new BooleanValue(true);
    }
  }

  /**
   * To test methods unique to a DoubleValue.
   */
  public static class DoubleValueTest extends ValueTest {


    @Override
    protected Value getValue() {
      return new DoubleValue(42.0);
    }
  }

  /**
   * To test methods unique to a ErrorValue.
   */
  public static class ErrorValueTest extends ValueTest {


    @Override
    protected Value getValue() {
      return ErrorValue.ARG;
    }
  }

  /**
   * To test methods unique to a StringValue.
   */
  public static class StringValueTest extends ValueTest {


    @Override
    protected Value getValue() {
      return new StringValue("hello world");
    }
  }
}
