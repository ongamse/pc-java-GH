package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    return HtmlUtils.htmlEscape(String.valueOf(message));
  }

  /**
   * Evaluation context for attacker-controlled expressions: no type references (T(...)),
   * no constructors, no method calls, no property/bean access. Only literals and operators
   * can be evaluated.
   */
  private static StandardEvaluationContext newRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setTypeLocator(typeName -> {
      throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    });
    context.setMethodResolvers(new ArrayList<MethodResolver>());
    context.setConstructorResolvers(new ArrayList<ConstructorResolver>());
    context.setPropertyAccessors(new ArrayList<PropertyAccessor>());
    context.setBeanResolver(null);
    return context;
  }
}
