package io.shiftleft.controller;

import java.util.Collections;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.util.HtmlUtils;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Search login
 */
@Controller
public class SearchController {

  // Allowlist: only simple literals — digits, letters, spaces, and basic arithmetic.
  // Blocks parentheses, dots, hashes, and brackets to prevent method calls,
  // type references T(), bean references #, and property traversal.
  private static final Pattern SAFE_EXPRESSION = Pattern.compile("^[a-zA-Z0-9_\\s+\\-*/]+$");

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    java.lang.Object message = new Object();
    if (foo == null || foo.isEmpty() || !SAFE_EXPRESSION.matcher(foo).matches()) {
      return "Invalid search expression";
    }
    try {
      ExpressionParser parser = new SpelExpressionParser();
      // Use a locked-down evaluation context: disable type resolution, method
      // resolution, and constructor resolution to prevent code injection / RCE.
      StandardEvaluationContext context = new StandardEvaluationContext();
      context.setTypeLocator(typeName -> {
        throw new IllegalArgumentException("Type references are not allowed in search expressions");
      });
      context.setMethodResolvers(Collections.emptyList());
      context.setConstructorResolvers(Collections.emptyList());
      Expression exp = parser.parseExpression(foo);
      message = (Object) exp.getValue(context);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return HtmlUtils.htmlEscape(message.toString());
  }
}
