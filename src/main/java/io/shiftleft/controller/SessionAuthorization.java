package io.shiftleft.controller;

import io.shiftleft.exception.ForbiddenRequestException;
import io.shiftleft.model.AuthToken;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * Authorization check for endpoints that look up records by a caller supplied id.
 * The application only has the server-side "auth" session attribute established by
 * AdminController's login, so access to arbitrary records by id is restricted to
 * sessions holding an admin AuthToken. Anything else is denied (fail closed).
 */
@Component
public class SessionAuthorization {

  /**
   * ObjectInputStream that only resolves the AuthToken class.
   */
  private static final class AuthTokenInputStream extends ObjectInputStream {
    AuthTokenInputStream(InputStream in) throws IOException {
      super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      if (!AuthToken.class.getName().equals(desc.getName())) {
        throw new InvalidClassException(desc.getName(), "Unauthorized deserialization attempt");
      }
      return super.resolveClass(desc);
    }
  }

  /**
   * @throws ForbiddenRequestException when the request has no authenticated admin session
   */
  public void requireAdmin(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      throw new ForbiddenRequestException();
    }
    Object auth = session.getAttribute("auth");
    if (auth == null) {
      throw new ForbiddenRequestException();
    }
    try (ObjectInputStream in = new AuthTokenInputStream(
        new ByteArrayInputStream(Base64.getDecoder().decode(auth.toString())))) {
      Object token = in.readObject();
      if (token instanceof AuthToken && ((AuthToken) token).isAdmin()) {
        return;
      }
    } catch (Exception ex) {
      // fall through to deny
    }
    throw new ForbiddenRequestException();
  }
}
