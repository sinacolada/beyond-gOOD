package beyondgood.model.formula.function;

import static org.junit.Assert.assertEquals;

import beyondgood.model.Coord;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.Test;

/**
 * To test the Sum function.
 */
public class SumTest {

  public static final Function<List<Value>, Value> FUNC = new Sum();

  @Test
  public void testSum() {

    List<Value> sumTwoThree = Arrays.asList(new DoubleValue(2), new DoubleValue(3));
    assertEquals(new DoubleValue(5), FUNC.apply(sumTwoThree));
  }

  @Test
  public void testNoArgs() {

    assertEquals(new DoubleValue(0), FUNC.apply(new ArrayList<>()));
  }

  @Test
  public void testBlankValue() {

    assertEquals(new DoubleValue(2),
        FUNC.apply(Arrays.<Value>asList(new DoubleValue(2), new BlankValue())));
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

  @Test
  public void sameArgTwice() {

    Formula ref1 = new Reference(new Coord(1, 1), new Coord(1, 1));
    Formula ref2 = new Reference(new Coord(1, 1), new Coord(1, 1));

    List<Formula> args = Arrays.asList(ref1, ref2);
    Formula sumFunc = new BeyondGoodFunction(args, new Sum());
    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(1, 1), new DoubleValue(2));
    assertEquals(new DoubleValue(4), sumFunc.evaluate(new HashMap<>(), cache, new HashSet<>()));
  }

  @Test
  public void rectRef() {

    Formula ref1 = new Reference(new Coord(1, 1), new Coord(3, 3));

    List<Formula> args = Collections.singletonList(ref1);
    Formula sumFunc = new BeyondGoodFunction(args, new Sum());
    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(1, 1), new DoubleValue(2));
    cache.put(new Coord(2, 2), new DoubleValue(4));
    cache.put(new Coord(1, 2), new DoubleValue(4));
    assertEquals(new DoubleValue(10), sumFunc.evaluate(new HashMap<>(), cache, new HashSet<>()));
  }


}
