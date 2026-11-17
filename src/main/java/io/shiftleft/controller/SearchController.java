package io.shiftleft.controller;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
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
      message = (Object) exp.getValue(newRestrictedContext());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    // the evaluated value derives from the attacker-supplied expression; HTML-encode it
    return HtmlUtils.htmlEscape(message.toString());
  }

  /**
   * Evaluation context for attacker-supplied expressions: no type references (T(..)),
   * no constructors, no method invocation, no property/reflective access and no bean
   * references. Only literals and operators can be evaluated.
   */
  private static EvaluationContext newRestrictedContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setTypeLocator(new TypeLocator() {
      @Override
      public Class<?> findType(String typeName) throws EvaluationException {
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
      }
    });
    context.setConstructorResolvers(new ArrayList<ConstructorResolver>());
    context.setMethodResolvers(new ArrayList<MethodResolver>());
    context.setPropertyAccessors(new ArrayList<PropertyAccessor>());
    context.setBeanResolver(null);
    return context;
  }
}
