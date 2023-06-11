package beyondgood.model;

import static org.junit.Assert.assertEquals;

import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.function.BeyondGoodFunction;
import beyondgood.model.formula.function.Sum;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

/**
 * Tests the Cell. Cell is a wrapper class, so all evaluation edges cases are in the formula
 * package. Raw contents are mostly tested here.
 */
public class CellTest {

  @Test
  public void testCell() {

    Formula form1 = new DoubleValue(1);
    Cell cell1 = new Cell("1.000000", form1);
    assertEquals(cell1.getRawCellContents(), "1.000000");
    assertEquals(cell1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()),
        new DoubleValue(1));
  }

  @Test
  public void testSelfRefCell() {

    Formula ref = new Reference(new Coord(1, 1), new Coord(1, 1));
    Cell cell1 = new Cell("=A1", ref);
    Set<Coord> prevRefs = new HashSet<>();
    prevRefs.add(new Coord(1, 1));
    assertEquals("=A1", cell1.getRawCellContents());
    assertEquals(ErrorValue.CIRC, cell1.evaluate(new HashMap<>(), new HashMap<>(), prevRefs));
  }

  @Test
  public void testGetRawContents() {

    Cell cell1 = new Cell("2", new DoubleValue(2));
    assertEquals("2", cell1.getRawCellContents());
    Cell cell2 = new Cell("This is a bad name", ErrorValue.ARG);
    assertEquals("This is a bad name", cell2.getRawCellContents());
  }

  @Test
  public void testEvalFunction() {

    Formula func = new BeyondGoodFunction(Arrays.asList(new DoubleValue(1), new DoubleValue(2)),
        new Sum());
    Cell cell1 = new Cell("=(SUM 1 2)", func);
    assertEquals(new DoubleValue(3),
        cell1.evaluate(new HashMap<>(), new HashMap<>(), new HashSet<>()));
  }

  @Test
  public void testIndirectInfiniteRef() {

    Formula sumCol1 = SexpConverter.convertString("=(SUM A:A)");
    Formula sumRow3 = SexpConverter.convertString("=(SUM 3:3");

    Cell cell1 = new Cell("=(SUM A:A)", sumCol1);
    Cell cell2 = new Cell("=(SUM 3:3)", sumRow3);

    Map<Coord, Cell> refs = new HashMap<>();
    refs.put(new Coord(3, 3), cell1);
    refs.put(new Coord(2, 2), cell2);

    Set<Coord> ancestors = new HashSet<>();
    ancestors.add(new Coord(2, 2));

    assertEquals(ErrorValue.ARG, cell2.evaluate(refs, new HashMap<>(), ancestors));
  }
}
