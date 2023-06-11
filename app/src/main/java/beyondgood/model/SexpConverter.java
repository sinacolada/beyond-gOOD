package beyondgood.model;

import beyondgood.model.formula.Formula;
import beyondgood.model.formula.function.BeyondGoodFunction;
import beyondgood.model.formula.function.SupportedFunctions;
import beyondgood.model.formula.reference.ColumnReference;
import beyondgood.model.formula.reference.Reference;
import beyondgood.model.formula.reference.RowReference;
import beyondgood.model.formula.value.BlankValue;
import beyondgood.model.formula.value.BooleanValue;
import beyondgood.model.formula.value.DoubleValue;
import beyondgood.model.formula.value.ErrorValue;
import beyondgood.model.formula.value.StringValue;
import beyondgood.model.formula.value.Value;
import beyondgood.sexp.Parser;
import beyondgood.sexp.SList;
import beyondgood.sexp.SSymbol;
import beyondgood.sexp.Sexp;
import beyondgood.sexp.SexpVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link SexpVisitor} to convert a {@link Sexp} to a {@link Formula}. All Sexps can be
 * represented by some {@link Formula}.
 */
public class SexpConverter implements SexpVisitor<Formula> {

  private static final Map<String, Function<List<Value>, Value>> VALID_FUNCS = SupportedFunctions
      .getFuncs();

  private static final Pattern ONE_REF = Pattern.compile("([A-Za-z]+)([1-9][0-9]*)");
  private static final Pattern RECT_REF = Pattern
      .compile("([A-Za-z]+[1-9][0-9]*):([A-Za-z]+[1-9][0-9]*)");
  private static final Pattern COL_REF = Pattern.compile("([A-Za-z]+):([A-Za-z]+)");
  private static final Pattern ROW_REF = Pattern.compile("([1-9][0-9]*):([1-9][0-9]*)");

  @Override
  public Formula visitBoolean(boolean b) {
    return new BooleanValue(b);
  }

  @Override
  public Formula visitNumber(double d) {
    return new DoubleValue(d);
  }

  @Override
  public Formula visitSList(List<Sexp> l) {

    // A SList is a function, so it must have at least a name. Empty Lists are not supported.
    if (l.size() < 1) {

      return ErrorValue.NAME;
    }

    // A SList must start with a SSymbol to be a function
    String funcName;
    try {

      funcName = l.get(0).accept(new GetSymbol());
    } catch (IllegalArgumentException e) {

      return ErrorValue.NAME;
    }

    // Only make the function if it is supported
    if (VALID_FUNCS.containsKey(funcName)) {

      // Make all the formulas for the this functions arguments
      List<Formula> funcArgs = new ArrayList<>();
      for (int i = 1; i < l.size(); i++) {

        funcArgs.add(l.get(i).accept(this));
      }
      return new BeyondGoodFunction(funcArgs, VALID_FUNCS.get(funcName));
    } else {

      return ErrorValue.NAME;
    }
  }

  @Override
  public Formula visitSymbol(String s) {

    Matcher oneRefMatcher = ONE_REF.matcher(s);
    Matcher rectRefMatcher = RECT_REF.matcher(s);
    Matcher colRefMatcher = COL_REF.matcher(s);
    Matcher rowRefMatcher = ROW_REF.matcher(s);

    if (oneRefMatcher.matches()) {

      Coord coord = new Coord(s);
      return new Reference(coord, coord);
    }
    if (rectRefMatcher.matches()) {

      Coord topLeft = new Coord(rectRefMatcher.group(1));
      Coord botRight = new Coord(rectRefMatcher.group(2));
      return new Reference(topLeft, botRight);
    }
    if (colRefMatcher.matches()) {

      int colRef1 = Coord.colNameToIndex(colRefMatcher.group(1));
      int colRef2 = Coord.colNameToIndex(colRefMatcher.group(2));
      return new ColumnReference(colRef1, colRef2);
    }
    if (rowRefMatcher.matches()) {

      int rowRef1 = Integer.parseInt(rowRefMatcher.group(1));
      int rowRef2 = Integer.parseInt(rowRefMatcher.group(2));
      return new RowReference(rowRef1, rowRef2);
    }

    return ErrorValue.NAME;
  }


  @Override
  public Formula visitString(String s) {
    return new StringValue(s);
  }

  /**
   * Converts the given String into a {@link Formula} by first attempting to Parse it into an {@link
   * Sexp} and then using {@link SexpConverter} to convert it into a formula.
   *
   * @param contents the given String
   * @return the formula that the String can be converted to. All (non-null) Strings are converted.
   * @throws IllegalArgumentException if the given String is null
   */
  public static Formula convertString(String contents) {

    if (contents == null) {
      throw new IllegalArgumentException("Contents cannot be null");
    }
    // Empty Strings should be treated as blank values, not converted into Sexps
    if (contents.length() == 0) {

      return new BlankValue();
    }
    int startIndex = 0;
    if (contents.startsWith("=")) {

      startIndex = 1;
    }
    Formula formula;
    try {
      Sexp parsedString = Parser.parse(contents.substring(startIndex));
      formula = parsedString.accept(new SexpConverter());
      // Could use a visitor but this is honestly cleaner / more clear
      if (startIndex == 0 && (parsedString instanceof SList || parsedString instanceof SSymbol)) {
        // functions/refs must start with "="
        formula = ErrorValue.ARG;
      }
    } catch (IllegalArgumentException e) {
      // Something went wrong parsing the String to a Sexp
      formula = ErrorValue.ARG;
    }

    return formula;
  }

  /**
   * A nexted {@link SexpVisitor} to get the String within a {@link SSymbol}.
   */
  private static class GetSymbol implements SexpVisitor<String> {

    @Override
    public String visitBoolean(boolean b) {
      throw new IllegalArgumentException();
    }

    @Override
    public String visitNumber(double d) {
      throw new IllegalArgumentException();
    }

    @Override
    public String visitSList(List<Sexp> l) {
      throw new IllegalArgumentException();
    }

    @Override
    public String visitSymbol(String s) {
      return s;
    }

    @Override
    public String visitString(String s) {
      throw new IllegalArgumentException();
    }
  }
}