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

  @RequestMapping(value = "/search/user", method = RequestMethod.GET)
  @ResponseBody
  public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    // The search term is treated strictly as data (never a view name / expression) and is
    // HTML-encoded before being written into the response body.
    java.lang.Object message = foo;
    return HtmlUtils.htmlEscape(message.toString());
  }
}
