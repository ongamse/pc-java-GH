package io.shiftleft.controller;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;


/**
 * Search login
 */
@Controller
public class SearchController {

  private static final int MAX_EXPRESSION_LENGTH = 100;
  private static final Pattern SAFE_ARITHMETIC = Pattern.compile("[0-9+\\-*/%().\\s]+");

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    java.lang.Object message = new Object();
    try {
      // Only plain arithmetic is evaluated; anything else (type references, method calls,
      // bean references, constructors, property access) is rejected before SpEL sees it.
      if (foo == null || foo.length() > MAX_EXPRESSION_LENGTH || !SAFE_ARITHMETIC.matcher(foo).matches()) {
        throw new IllegalArgumentException("Unsupported search expression");
      }
      ExpressionParser parser = new SpelExpressionParser();
      Expression exp = parser.parseExpression(foo);
      message = (Object) exp.getValue();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return HtmlUtils.htmlEscape(message.toString());
  }
}
