package beyondgood.model.formula.function;

import static org.junit.Assert.assertEquals;

import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * To test the Product function.
 */
public class ProductTest {

  private static final Function<List<Value>, Value> FUNC = new Product();

  @Test
  public void testProduct() {

    List<Value> six = Stream.of(2, 3).map(DoubleValue::new).collect(Collectors.toList());
    assertEquals(new DoubleValue(6), FUNC.apply(six));
  }

  @Test
  public void testNoArgs() {

    assertEquals(new DoubleValue(0), FUNC.apply(new ArrayList<>()));
  }

  @Test
  public void testNonNumberIgnored() {

    List<Value> args = new ArrayList<>();
    args.add(new DoubleValue(2));
    args.add(new StringValue("hello"));
    args.add(new BooleanValue(true));
    assertEquals(new DoubleValue(2), FUNC.apply(args));
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
