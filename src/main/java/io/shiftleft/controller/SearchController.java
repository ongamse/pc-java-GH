package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
import org.springframework.web.util.HtmlUtils;


/**
 * Search login
 */
@Controller
public class SearchController {

  private static final int MAX_EXPRESSION_LENGTH = 256;

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    java.lang.Object message = new Object();
    try {
      ExpressionParser parser = new SpelExpressionParser();
      if (foo.length() > MAX_EXPRESSION_LENGTH) {
        throw new IllegalArgumentException("Search expression too long");
      }
      Expression exp = parser.parseExpression(foo);
      message = (Object) exp.getValue(buildRestrictedContext());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return HtmlUtils.htmlEscape(String.valueOf(message));
  }

  /**
   * Builds an evaluation context that only permits literals and operators. Type references
   * (T(...)), constructors, method calls, property access and bean references are all disabled so
   * attacker-controlled expressions cannot reach arbitrary classes or code.
   */
  private static StandardEvaluationContext buildRestrictedContext() {
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
