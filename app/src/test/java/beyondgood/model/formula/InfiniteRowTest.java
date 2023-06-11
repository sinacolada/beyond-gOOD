package beyondgood.model.formula;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.formula.function.BeyondGoodFunction;
import beyondgood.model.formula.function.Sum;
import beyondgood.model.formula.reference.RowReference;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * To test the {@link RowReference} class.
 */
public class InfiniteRowTest {

  @Test
  public void testEvaluate() {

    Formula row1 = new RowReference(1, 1);
    assertEquals(ErrorValue.REF, row1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()));
  }

  @Test
  public void testEvaluateParts() {

    Formula row1 = new RowReference(1, 1);
    Formula row12 = new RowReference(1, 2);
    Map<Coord, Cell> refs = new LinkedHashMap<>();
    refs.put(new Coord(1, 1), new Cell("2", new DoubleValue(2)));
    refs.put(new Coord(1, 2), new Cell("3", new DoubleValue(3)));
    refs.put(new Coord(143, 1), new Cell("4", new DoubleValue(4)));

    List<Value> evalPartsActual = row1.evaluateParts(refs, new HashMap<>(), new HashSet<>());
    List<Value> evalPartsActual2 = row12.evaluateParts(refs, new HashMap<>(), new HashSet<>());
    assertArrayEquals(Stream.of(2, 4).map(DoubleValue::new).toArray(), evalPartsActual.toArray());

    assertArrayEquals(Stream.of(2, 3, 4).map(DoubleValue::new).toArray(),
        evalPartsActual2.toArray());
  }

  @Test
  public void testSelfCirc() {

    List<Formula> rowArgs = Collections.singletonList(new RowReference(1, 1));
    Formula sumRow1 = new BeyondGoodFunction(rowArgs, new Sum());
    Set<Coord> ancestors = new HashSet<>();
    ancestors.add(new Coord(32, 1));
    assertEquals(ErrorValue.CIRC, sumRow1.evaluate(new HashMap<>(), new HashMap<>(), ancestors));
  }

  @Test
  public void testIndirectCirc() {

    List<Formula> rowArgs = Collections.singletonList(new RowReference(1, 1));
    List<Formula> rowArgs2 = Collections.singletonList(new RowReference(2, 2));

    Formula sumRow1 = new BeyondGoodFunction(rowArgs, new Sum());
    Formula sumRow2 = new BeyondGoodFunction(rowArgs2, new Sum());

    Set<Coord> ancestors = new HashSet<>();
    Set<Coord> ancestors2 = new HashSet<>();
    ancestors.add(new Coord(1, 1));
    ancestors2.add(new Coord(2, 2));

    assertEquals(ErrorValue.CIRC, sumRow1.evaluate(new HashMap<>(), new HashMap<>(), ancestors));
    assertEquals(ErrorValue.CIRC, sumRow2.evaluate(new HashMap<>(), new HashMap<>(), ancestors2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadValues() {

    new RowReference(-1, 2);
  }

  @Test
  public void testIsInReference() {

    Formula rowRef = new RowReference(1, 3);
    Formula two = new DoubleValue(2);
    Formula three = new DoubleValue(3);
    Cell twoCell = new Cell("2", two);
    Cell threeCell = new Cell("3", three);
    Map<Coord, Cell> refs = new HashMap<>();
    refs.put(new Coord(1, 1), twoCell);
    refs.put(new Coord(45, 3), threeCell);

    Formula sum = new BeyondGoodFunction(Collections.singletonList(rowRef), new Sum());
    assertEquals(new DoubleValue(5), sum.evaluate(refs, new HashMap<>(), new HashSet<>()));
  }


}
