package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @ResponseBody
  @RequestMapping(value = "/search/user", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    // The search term is treated as a literal; it is never evaluated as an expression.
    return HtmlUtils.htmlEscape(foo);
  }
}
