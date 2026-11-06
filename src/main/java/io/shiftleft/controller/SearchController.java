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
      message = (Object) exp.getValue(buildRestrictedContext());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return message.toString();
  }

  /**
   * Evaluation context for untrusted expressions: no type references (T(..), new ..), no method
   * calls, no property access and no bean references, so only literals and operators evaluate.
   */
  private static StandardEvaluationContext buildRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setTypeLocator(typeName -> {
      throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    });
    context.setMethodResolvers(Collections.<MethodResolver>emptyList());
    context.setConstructorResolvers(Collections.<ConstructorResolver>emptyList());
    context.setPropertyAccessors(Collections.<PropertyAccessor>emptyList());
    context.setBeanResolver(null);
    return context;
  }
}
