package beyondgood.model.formula.function;

import static org.junit.Assert.assertEquals;

import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * Test the less than function.
 */
public class LessThanTest {

  private static final Function<List<Value>, Value> LESS_THAN = new LessThan();
  private static final Value TRUE = new BooleanValue(true);
  private static final Value FALSE = new BooleanValue(false);

  @Test
  public void testTrue() {

    List<Value> trueCase = Stream.of(2, 3).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(TRUE, LESS_THAN.apply(trueCase));
  }

  @Test
  public void testFalse() {

    List<Value> falseCase1 = Stream.of(3, 2).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(FALSE, LESS_THAN.apply(falseCase1));

    List<Value> falseCase2 = Stream.of(3, 3).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(FALSE, LESS_THAN.apply(falseCase2));
  }

  @Test
  public void badArgNum() {

    List<Value> tooMany = Stream.of(2, 3, 4).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(tooMany));

    List<Value> tooFew = Stream.of(2).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(tooFew));
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(new ArrayList<>()));
  }

  @Test
  public void badArgType() {

    Value one = new DoubleValue(1);
    Value hello = new StringValue("hello");
    List<Value> bad2Arg = Arrays.asList(one, hello);
    List<Value> bad1Arg = Arrays.asList(hello, one);
    List<Value> badBothArg = Arrays.asList(hello, hello);
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(bad2Arg));
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(bad1Arg));
    assertEquals(ErrorValue.ARG, LESS_THAN.apply(badBothArg));
  }
}
