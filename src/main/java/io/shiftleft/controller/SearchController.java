package io.shiftleft.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;


/**
 * Search login
 */
@Controller
public class SearchController {

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  @ResponseBody
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    java.lang.Object message = new Object();
    try {
      ExpressionParser parser = new SpelExpressionParser();
      Expression exp = parser.parseExpression(foo);
      message = (Object) exp.getValue(createRestrictedContext());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    // Evaluation result derives from attacker input: write it as an HTML-encoded response body
    return HtmlUtils.htmlEscape(String.valueOf(message));
  }

  /**
   * Evaluation context with no type lookup, constructors, method calls, property access or bean
   * references, so attacker-supplied expressions can only evaluate literals and operators.
   */
  private static StandardEvaluationContext createRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setTypeLocator(typeName -> {
      throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    });
    context.setConstructorResolvers(Collections.<ConstructorResolver>emptyList());
    context.setMethodResolvers(Collections.<MethodResolver>emptyList());
    context.setPropertyAccessors(Collections.<PropertyAccessor>emptyList());
    context.setBeanResolver(null);
    return context;
  }
}
