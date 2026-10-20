package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Search login
 */
@Controller
public class SearchController {

  private static final java.util.regex.Pattern SAFE_SPEL_PATTERN =
      java.util.regex.Pattern.compile("^[a-zA-Z0-9_.,' \\-+*/()]+$");

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    if (foo == null || foo.isEmpty() || foo.length() > 200 || !SAFE_SPEL_PATTERN.matcher(foo).matches()) {
      return "Invalid search expression";
    }
    java.lang.Object message = new Object();
    try {
      String validatedInput = foo;
      ExpressionParser parser = new SpelExpressionParser();
      Expression exp = parser.parseExpression(validatedInput);
      EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
      message = (Object) exp.getValue(context);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return message.toString();
  }
}
