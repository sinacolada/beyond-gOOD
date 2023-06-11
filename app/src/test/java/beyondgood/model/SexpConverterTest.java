package beyondgood.model;

import static org.junit.Assert.assertEquals;

import beyondgood.model.formula.Formula;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.sexp.SBoolean;
import beyondgood.sexp.SNumber;
import beyondgood.sexp.SString;
import beyondgood.sexp.SSymbol;
import beyondgood.sexp.Sexp;
import beyondgood.sexp.SexpVisitor;

import org.junit.Test;

/**
 * To test the {@link SexpConverter}.
 */
public class SexpConverterTest {

  private static final SexpVisitor<Formula> CONVERTER = new SexpConverter();

  @Test
  public void testConvertString() {

    Sexp hello = new SString("hello");
    Formula expectedHello = new StringValue("hello");

    Sexp empty = new SString("");
    Formula expectedEmpty = new StringValue("");

    Sexp escape = new SString("\"hello\"");
    Formula expectedEscape = new StringValue("\"hello\"");

    assertEquals(expectedHello, hello.accept(CONVERTER));
    assertEquals(expectedEmpty, empty.accept(CONVERTER));
    assertEquals(expectedEscape, escape.accept(CONVERTER));
  }

  @Test
  public void testConvertBooleans() {

    Sexp sTrue = new SBoolean(true);
    Sexp sFalse = new SBoolean(false);
    Formula expecTrue = new BooleanValue(true);
    Formula expecFalse = new BooleanValue(false);

    assertEquals(expecTrue, sTrue.accept(CONVERTER));
    assertEquals(expecFalse, sFalse.accept(CONVERTER));
  }

  @Test
  public void testConvertNumbers() {

    Sexp zero = new SNumber(0);
    Formula expecZero = new DoubleValue(0);

    Sexp pos = new SNumber(42.12321421);
    Formula expecPos = new DoubleValue(42.12321421);

    Sexp neg = new SNumber(-42.12321421);
    Formula expecNeg = new DoubleValue(-42.12321421);

    assertEquals(expecZero, zero.accept(CONVERTER));
    assertEquals(expecPos, pos.accept(CONVERTER));
    assertEquals(expecNeg, neg.accept(CONVERTER));
  }

  @Test
  public void testConvertBadSymbol() {

    Sexp badRef = new SSymbol("A:4");
    Sexp justSum = new SSymbol("SUM");

    Formula expecBadRef = ErrorValue.NAME;

    assertEquals(expecBadRef, badRef.accept(CONVERTER));
    assertEquals(expecBadRef, justSum.accept(CONVERTER));
  }

  @Test
  public void testConvertOneCoord() {

    Sexp oneRef = new SSymbol("A2");
    Coord oneCoord = new Coord("A2");
    Formula expeOneRef = new Reference(oneCoord, oneCoord);

    assertEquals(expeOneRef, oneRef.accept(CONVERTER));
  }

  @Test
  public void testConvertMultiCoord() {

    Sexp multiCoord = new SSymbol("A2:B4");
    Coord topLeft = new Coord("A2");
    Coord botRight = new Coord("B4");
    Formula expeOneRef = new Reference(topLeft, botRight);

    assertEquals(expeOneRef, multiCoord.accept(CONVERTER));
  }

  @Test
  public void testFlippedMultiCoord() {

    Sexp multiCoord = new SSymbol("B4:A2");
    Coord topLeft = new Coord("A2");
    Coord botRight = new Coord("B4");
    Formula expeOneRef = new Reference(botRight, topLeft);

    assertEquals(expeOneRef, multiCoord.accept(CONVERTER));
  }
}
