package io.shiftleft.controller;

import io.shiftleft.exception.AccessDeniedRequestException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Authorization gate for endpoints that look up a record by a client supplied id.
 * The project has no per-user ownership model, so access is limited to the
 * authenticated administrator session established by AdminController.
 */
final class ResourceAccess {

  private ResourceAccess() {
  }

  /**
   * Throws AccessDeniedRequestException (HTTP 403) unless the caller is authorized.
   */
  static void require(HttpServletRequest request) {
    if (!isAuthorized(request)) {
      throw new AccessDeniedRequestException();
    }
  }

  /**
   * @return true only when the current session carries an admin auth token
   */
  static boolean isAuthorized(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return false;
    }
    Object auth = session.getAttribute("auth");
    return auth != null && AdminController.isAdmin(auth.toString());
  }
}
