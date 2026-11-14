package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    java.lang.Object message = new Object();
    try {
      ExpressionParser parser = new SpelExpressionParser();
      Expression exp = parser.parseExpression(foo);
      message = (Object) exp.getValue(newRestrictedContext());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    // the evaluated result derives from request input: encode it before it is rendered as HTML
    return HtmlUtils.htmlEscape(message.toString());
  }

  /**
   * Evaluation context that only permits literals and operators: no type references
   * (T(..) / new), no method calls, no property or bean access.
   */
  private static StandardEvaluationContext newRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setTypeLocator(typeName -> {
      throw new EvaluationException("Type references are not allowed: " + typeName);
    });
    context.setMethodResolvers(Collections.<MethodResolver>emptyList());
    context.setConstructorResolvers(Collections.<ConstructorResolver>emptyList());
    context.setPropertyAccessors(Collections.<PropertyAccessor>emptyList());
    context.setBeanResolver(null);
    return context;
  }
}
