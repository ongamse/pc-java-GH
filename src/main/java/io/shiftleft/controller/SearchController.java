package io.shiftleft.controller;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeLocator;
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
   * Evaluation context that cannot reach arbitrary types, methods, constructors, properties or
   * beans, so attacker-supplied expressions are limited to literals and operators.
   * (SimpleEvaluationContext is unavailable in the Spring version used by this project.)
   */
  private static StandardEvaluationContext buildRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setPropertyAccessors(new ArrayList<PropertyAccessor>());
    context.setMethodResolvers(new ArrayList<MethodResolver>());
    context.setConstructorResolvers(new ArrayList<ConstructorResolver>());
    context.setTypeLocator(new TypeLocator() {
      @Override
      public Class<?> findType(String typeName) throws EvaluationException {
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
      }
    });
    return context;
  }
}
