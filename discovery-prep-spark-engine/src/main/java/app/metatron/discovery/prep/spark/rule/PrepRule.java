package app.metatron.discovery.prep.spark.rule;

import app.metatron.discovery.prep.parser.preparation.rule.expr.Constant.ArrayExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr.BinaryNumericOpExprBase;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr.FunctionArrayExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr.FunctionExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr.UnaryMinusExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expr.UnaryNotExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Expression;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Identifier;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Identifier.IdentifierArrayExpr;
import app.metatron.discovery.prep.parser.preparation.rule.expr.Identifier.IdentifierExpr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrepRule {

  List<String> relatedColNames;

  public PrepRule() {
    relatedColNames = new ArrayList();
  }

  public List<String> getIdentifierList(Expression expr) {
    List<String> arr = new ArrayList();

    if (expr instanceof IdentifierExpr) {
      String colName = ((IdentifierExpr) expr).getValue();
      arr.add(colName);
      relatedColNames.add(colName);
    } else {
      for (String colName : ((IdentifierArrayExpr) expr).getValue()) {
        arr.add(colName);
        relatedColNames.add(colName);
      }
    }
    return arr;
  }

  public static String asSparkExpr(String expr) {
    return expr.replace("==", "=").replace("||", "OR").replace("&&", "AND");
  }

  public class StrExpResult {

    public String str;
    public List<String> arrStr;

    public StrExpResult(String str, List<String> arrStr) {
      this.str = str;
      this.arrStr = arrStr;
    }

    public StrExpResult(String str) {
      this(str, Arrays.asList(str));
    }

    public int getArrSize() {
      return arrStr.size();
    }

    public String toColList() {
      if (arrStr.size() >= 3) {
        return arrStr.size() + " columns";
      }

      return str;
    }
  }

  private String wrapIdentifier(String identifier) {
    if (!identifier.matches("[_a-zA-Z\u0080-\uFFFF]+[_a-zA-Z0-9\u0080-\uFFFF]*")) {  // if has odd characters
      return "`" + identifier + "`";
    }
    return identifier;
  }

  private StrExpResult wrapIdentifier(StrExpResult strExpResult) {
    for (int i = 0; i < strExpResult.arrStr.size(); i++) {
      strExpResult.arrStr.set(i, wrapIdentifier(strExpResult.arrStr.get(i)));
    }
    strExpResult.str = joinWithComma(strExpResult.arrStr);
    return strExpResult;
  }

  private String stringifyFuncExpr(FunctionExpr funcExpr) {
    List<Expr> args = funcExpr.getArgs();
    String str = funcExpr.getName() + "(";

    for (Expr arg : args) {
      str += stringifyExpr(arg).str + ", ";
    }
    return str.substring(0, str.length() - 2) + ")";
  }

  private String joinWithComma(List<String> strs) {
    String resultStr = "";

    for (String str : strs) {
      resultStr += str + ", ";
    }
    return resultStr.substring(0, resultStr.length() - 2);
  }

  protected StrExpResult stringifyExpr(Expression expr) {
    if (expr == null) {
      return null;
    }

    if (expr instanceof IdentifierArrayExpr) {    // This should come first because this is the sub-class of Identifier
      IdentifierArrayExpr arrExpr = (IdentifierArrayExpr) expr;
      List<String> wrappedIdentifiers = new ArrayList();

      for (String colName : arrExpr.getValue()) {
        wrappedIdentifiers.add(wrapIdentifier(colName));
        relatedColNames.add(colName);
      }

      return new StrExpResult(joinWithComma(wrappedIdentifiers), wrappedIdentifiers);
    } else if (expr instanceof Identifier) {
      String colName = expr.toString();
      relatedColNames.add(colName);
      return new StrExpResult(wrapIdentifier(colName));
    } else if (expr instanceof FunctionExpr) {
      return new StrExpResult(stringifyFuncExpr((FunctionExpr) expr));
    } else if (expr instanceof FunctionArrayExpr) {
      FunctionArrayExpr funcArrExpr = (FunctionArrayExpr) expr;
      List<String> funcStrExprs = new ArrayList();

      for (FunctionExpr funcExpr : funcArrExpr.getFunctions()) {
        funcStrExprs.add(stringifyFuncExpr(funcExpr));
      }
      return new StrExpResult(joinWithComma(funcStrExprs), funcStrExprs);
    } else if (expr instanceof BinaryNumericOpExprBase) {
      BinaryNumericOpExprBase binExpr = (BinaryNumericOpExprBase) expr;
      return new StrExpResult(
          stringifyExpr(binExpr.getLeft()).str + " " + binExpr.getOp() + " " + stringifyExpr(
              binExpr.getRight()).str);
    } else if (expr instanceof UnaryNotExpr) {
      UnaryNotExpr notExpr = (UnaryNotExpr) expr;
      return new StrExpResult("!" + stringifyExpr(notExpr.getChild()));
    } else if (expr instanceof UnaryMinusExpr) {
      UnaryMinusExpr minusExpr = (UnaryMinusExpr) expr;
      return new StrExpResult(minusExpr.toString());
    } else if (expr instanceof ArrayExpr) {
      List<String> arrStr = ((ArrayExpr) expr).getValue();
      return new StrExpResult(joinWithComma(arrStr), arrStr);
    }

    return new StrExpResult(expr.toString());
  }
}
