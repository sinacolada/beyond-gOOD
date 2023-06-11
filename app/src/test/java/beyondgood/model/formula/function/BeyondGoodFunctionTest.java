package beyondgood.model.formula.function;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import beyondgood.model.Coord;
import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * To test the {@link BeyondGoodFunction} class.
 */
public class BeyondGoodFunctionTest {

  @Test
  public void testSumEvaluate() {

    List<Formula> args = Stream.of(2, 3, 4).map(DoubleValue::new).collect(Collectors.toList());
    Formula func1 = new BeyondGoodFunction(args, new Sum());

    assertEquals(new DoubleValue(9),
        func1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()));

    assertArrayEquals(Collections.singletonList(new DoubleValue(9)).toArray(),
        func1.evaluateParts(new HashMap<>(), new HashMap<>(), new HashSet<>()).toArray());
  }

  @Test
  public void testMultiSumRef() {

    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(1, 1), new DoubleValue(1));
    cache.put(new Coord(1, 2), new DoubleValue(2));

    Formula multiRef = new Reference(new Coord(1, 1), new Coord(1, 2));
    Formula sumFunc = new BeyondGoodFunction(Collections.singletonList(multiRef), new Sum());
    assertEquals(new DoubleValue(3), sumFunc.evaluate(new HashMap<>(), cache, new HashSet<>()));
  }

  @Test
  public void testCircular() {

    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(1, 1), new DoubleValue(1));
    cache.put(new Coord(1, 2), new DoubleValue(2));

    Formula multiRef = new Reference(new Coord(1, 1), new Coord(1, 2));
    Formula sumFunc = new BeyondGoodFunction(Collections.singletonList(multiRef), new Sum());
    Set<Coord> ancestors = new HashSet<>();
    ancestors.add(new Coord(1, 1));
    assertEquals(ErrorValue.CIRC, sumFunc.evaluate(new HashMap<>(), cache, ancestors));
  }

  @Test
  public void testErrorPropagates() {

    List<Formula> args = Stream.of(2, 3, 4).map(DoubleValue::new).collect(Collectors.toList());
    args.add(ErrorValue.ARG);
    Formula func1 = new BeyondGoodFunction(args, new Sum());
    assertEquals(ErrorValue.ARG, func1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()));

  }

}
