package beyondgood.model.formula.value;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * To test the {@link ValueToStringVisitor}.
 */
public class ToStringVisitorTest {

  private static final ValueVisitor<String> TO_STRING = new ValueToStringVisitor();

  @Test
  public void testBlank() {

    assertEquals(new BlankValue().accept(TO_STRING), "");
  }

  @Test
  public void testDouble() {

    assertEquals(new DoubleValue(2).accept(TO_STRING), "2.000000");
    assertEquals(new DoubleValue(-2).accept(TO_STRING), "-2.000000");
    assertEquals(new DoubleValue(2.45).accept(TO_STRING), "2.450000");
  }

  @Test
  public void testString() {

    assertEquals(new StringValue("hello").accept(TO_STRING), "\"hello\"");
    assertEquals(new StringValue("").accept(TO_STRING), "\"\"");
    assertEquals(new StringValue("\"hello\"").accept(TO_STRING), "\"\"hello\"\"");
  }

  @Test
  public void testBoolean() {

    assertEquals(new BooleanValue(true).accept(TO_STRING), "TRUE");
    assertEquals(new BooleanValue(false).accept(TO_STRING), "FALSE");
  }

  @Test
  public void testError() {

    assertEquals(ErrorValue.ARG.accept(TO_STRING), "#ARG!");
    assertEquals(ErrorValue.NAME.accept(TO_STRING), "#NAME?");
    assertEquals(ErrorValue.CIRC.accept(TO_STRING), "#CIRC!");
    assertEquals(ErrorValue.DIV0.accept(TO_STRING), "#DIV/0!");
    assertEquals(ErrorValue.NUM.accept(TO_STRING), "#NUM!");
    assertEquals(ErrorValue.REF.accept(TO_STRING), "#REF!");
  }
}
