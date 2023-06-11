package beyondgood.model.formula.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import beyondgood.model.formula.value.ValueGetterVisitor.GetBoolean;
import beyondgood.model.formula.value.ValueGetterVisitor.GetDouble;
import beyondgood.model.formula.value.ValueGetterVisitor.GetString;

import org.junit.Test;

/**
 * To test the {@link ValueGetterVisitor}.
 */
public class ValueGetterVisitorTest {

  @Test
  public void testBlank() {

    assertEquals(new BlankValue().accept(new GetBoolean()), false);
    assertEquals(new BlankValue().accept(new GetString()), "");
    assertEquals(new BlankValue().accept(new GetDouble()), (Double) 0.0);
  }

  @Test
  public void testDouble() {

    assertEquals(new DoubleValue(42.2).accept(new GetDouble()), (Double) 42.2);
  }

  @Test
  public void testString() {

    assertEquals(new StringValue("hello").accept(new GetString()), "hello");
  }

  @Test
  public void testBoolean() {

    assertEquals(new BooleanValue(true).accept(new GetBoolean()), true);
    assertEquals(new BooleanValue(false).accept(new GetBoolean()), false);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testErrorBool() {

    ErrorValue.ARG.accept(new GetBoolean());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testErrorBoolStr() {

    ErrorValue.ARG.accept(new GetString());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testErrorBoolNum() {

    ErrorValue.ARG.accept(new GetDouble());
  }

  @Test
  public void testBadGetters() {

    try {
      new BooleanValue(true).accept(new GetString());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected boolean", e.getMessage());
    }

    try {
      new BooleanValue(true).accept(new GetDouble());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected boolean", e.getMessage());
    }

    try {
      new DoubleValue(2.0).accept(new GetString());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected double", e.getMessage());
    }

    try {
      new DoubleValue(2.0).accept(new GetBoolean());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected double", e.getMessage());
    }

    try {
      new StringValue("").accept(new GetBoolean());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected String", e.getMessage());
    }

    try {
      new StringValue("").accept(new GetDouble());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {

      assertEquals("Unexpected String", e.getMessage());
    }
  }

}
