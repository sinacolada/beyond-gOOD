package beyondgood.model.formula.function;

import static org.junit.Assert.assertEquals;

import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

/**
 * To test the repeat function.
 */
public class RepeatTest {

  private static final Function<List<Value>, Value> FUNC = new Repeat();

  @Test
  public void testRepeat() {

    List<Value> helloTwice = Arrays.asList(new StringValue("hello"), new DoubleValue(2));
    assertEquals(new StringValue("hellohello"), new Repeat().apply(helloTwice));
  }

  @Test
  public void testEmptyString() {

    List<Value> empty100 = Arrays.asList(new StringValue(""), new DoubleValue(100));
    assertEquals(new StringValue(""), FUNC.apply(empty100));
  }

  @Test
  public void testBadArgNum() {

    assertEquals(ErrorValue.ARG, FUNC.apply(new ArrayList<>()));
    assertEquals(ErrorValue.ARG, FUNC.apply(Collections.singletonList(new StringValue("hello"))));
    assertEquals(ErrorValue.ARG, FUNC.apply(
        Arrays.<Value>asList(new StringValue("hello"), new DoubleValue(1), new DoubleValue(2))));
  }

  @Test
  public void testNegArgNum() {

    assertEquals(ErrorValue.ARG, FUNC.apply(
        Arrays.<Value>asList(new StringValue("hello"), new DoubleValue(-1))));
  }

  @Test
  public void errorPropagatesToArg() {

    List<Value> error1 = new ArrayList<>();
    error1.add(new DoubleValue(1));
    error1.add(ErrorValue.CIRC);

    assertEquals(ErrorValue.ARG, FUNC.apply(error1));
    error1.remove(1);
    error1.add(ErrorValue.ARG);
    assertEquals(ErrorValue.ARG, FUNC.apply(error1));
    error1.remove(1);
    error1.add(ErrorValue.REF);
    assertEquals(ErrorValue.ARG, FUNC.apply(error1));
  }
}
