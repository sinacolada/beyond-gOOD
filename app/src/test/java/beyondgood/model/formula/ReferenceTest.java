package beyondgood.model.formula;

import static org.junit.Assert.assertEquals;

import beyondgood.model.Cell;
import beyondgood.model.Coord;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

/**
 * To test a Reference.
 */
public class ReferenceTest {

  @Test
  public void testReference() {

    Formula ref1 = new Reference(new Coord(1, 1), new Coord(1, 1));
    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(1, 1), new DoubleValue(2));

    assertEquals(ref1.evaluate(new HashMap<>(), cache, new HashSet<>()),
        new DoubleValue(2));
  }

  @Test
  public void testSelfRef() {

    Formula ref1 = new Reference(new Coord(1, 1), new Coord(1, 1));
    Map<Coord, Value> cache = new HashMap<>();
    cache.put(new Coord(2, 2), new DoubleValue(2));
    Set<Coord> prevRefs = new HashSet<>();
    prevRefs.add(new Coord(1, 1));
    Map<Coord, Cell> sheet = new HashMap<>();
    sheet.put(new Coord(1, 1), new Cell("=A1", ref1));

    assertEquals(ref1.evaluate(sheet, cache, prevRefs), ErrorValue.CIRC);
    assertEquals(ref1.evaluate(new HashMap<>(), cache, new HashSet<>()), new DoubleValue(0));
  }

  @Test
  public void testIndirectCircRef() {

    Formula ref1 = new Reference(new Coord(2, 2), new Coord(2, 2));
    Map<Coord, Value> cache = new HashMap<>();
    Set<Coord> prevRefs = new HashSet<>();
    prevRefs.add(new Coord(1, 1));
    prevRefs.add(new Coord(2, 2));
    Map<Coord, Cell> sheet = new HashMap<>();
    sheet.put(new Coord(1, 1), new Cell("=A1", ref1));

    assertEquals(ref1.evaluate(sheet, cache, prevRefs), ErrorValue.CIRC);
  }

  @Test
  public void testRectRefEval() {

    Formula ref1 = new Reference(new Coord(2, 2), new Coord(2, 5));
    assertEquals(ErrorValue.REF, ref1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()));

  }

}
