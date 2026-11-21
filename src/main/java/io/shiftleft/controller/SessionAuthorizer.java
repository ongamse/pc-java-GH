package io.shiftleft.controller;

import io.shiftleft.exception.ForbiddenRequestException;
import io.shiftleft.model.AuthToken;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Authorization gate for endpoints that look up or modify records by a
 * client-supplied id. The only authenticated principal this application has is
 * the admin session established by AdminController (session attribute "auth"),
 * and records have no per-user owner, so access to a record by id requires that
 * admin session. Anything else is denied (403).
 */
final class SessionAuthorizer {

  private static final int MAX_BASE64_INPUT_LENGTH = 8192;
  private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
  private static final Pattern TOKEN_PATTERN = Pattern.compile("^role=([0-9]{1,9})$");

  private SessionAuthorizer() {
  }

  static void requireAdminSession(HttpServletRequest request) {
    if (!isAdminSession(request)) {
      throw new ForbiddenRequestException();
    }
  }

  private static boolean isAdminSession(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return false;
    }
    Object auth = session.getAttribute("auth");
    if (auth == null) {
      return false;
    }
    String token = auth.toString();
    if (token.length() > MAX_BASE64_INPUT_LENGTH || !BASE64_PATTERN.matcher(token).matches()) {
      return false;
    }
    try {
      String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
      Matcher m = TOKEN_PATTERN.matcher(decoded);
      return m.matches() && new AuthToken(Integer.parseInt(m.group(1))).isAdmin();
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }
}
