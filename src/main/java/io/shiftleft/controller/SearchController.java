package io.shiftleft.controller;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9 _.'\"\\-]+$");

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    if (foo == null || !SAFE_INPUT_PATTERN.matcher(foo).matches()) {
      return "Invalid search input";
    }
    java.lang.Object message = new Object();
    try {
      ExpressionParser parser = new SpelExpressionParser();
      SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
      Expression exp = parser.parseExpression("'" + foo.replace("'", "''") + "'");
      message = (Object) exp.getValue(context);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return message.toString();
  }
}
