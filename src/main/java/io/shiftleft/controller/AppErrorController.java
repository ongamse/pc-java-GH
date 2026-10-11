package io.shiftleft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {
  private final ErrorAttributes errorAttributes;

  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public ResponseEntity<String> errorHtml(HttpServletRequest request) {
    HttpStatus status = getStatus(request);
    String message = HtmlUtils.htmlEscape(status.getReasonPhrase());
    String body = "<!doctype html><html><head><title>" + status.value() + " " + message
        + "</title></head><body><h1>" + status.value() + " " + message + "</h1></body></html>";
    return ResponseEntity.status(status).contentType(MediaType.TEXT_HTML).body(body);
  }

  @RequestMapping("/error")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
    Map<String, Object> body = errorAttributes.getErrorAttributes(
        new ServletWebRequest(request), ErrorAttributeOptions.defaults());
    return ResponseEntity.status(getStatus(request)).body(body);
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (statusCode instanceof Integer code) {
      HttpStatus status = HttpStatus.resolve(code);
      if (status != null) {
        return status;
      }
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
