package io.shiftleft.controller;

import io.shiftleft.model.AuthToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import io.shiftleft.exception.ForbiddenRequestException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Admin checks login
 */
@Controller
public class AdminController {
  private String fail = "redirect:/";

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  // Signing key: taken from the environment when set, otherwise random per process
  private static final byte[] AUTH_KEY = loadAuthKey();

  private static byte[] loadAuthKey() {
    String configured = System.getenv("AUTH_COOKIE_SECRET");
    if (configured != null && !configured.isEmpty()) {
      return configured.getBytes(StandardCharsets.UTF_8);
    }
    byte[] key = new byte[32];
    new SecureRandom().nextBytes(key);
    return key;
  }

  private static String sign(String payload) throws GeneralSecurityException {
    Mac mac = Mac.getInstance(HMAC_ALGORITHM);
    mac.init(new SecretKeySpec(AUTH_KEY, HMAC_ALGORITHM));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }

  // cookie format: <role>.<hmac(role)>; no native deserialization of client data
  private static String createAuthCookie(AuthToken token) throws GeneralSecurityException {
    String payload = Integer.toString(token.getRole());
    return payload + "." + sign(payload);
  }

  // helper
  private static boolean isAdmin(String auth)
  {
    try {
      String[] parts = auth.split("\\.", -1);
      if (parts.length != 2) {
        return false;
      }
      byte[] expected = sign(parts[0]).getBytes(StandardCharsets.UTF_8);
      byte[] actual = parts[1].getBytes(StandardCharsets.UTF_8);
      if (!MessageDigest.isEqual(expected, actual)) {
        return false;
      }
      return new AuthToken(Integer.parseInt(parts[0])).isAdmin();
    } catch (Exception ex) {
      System.out.println(" cookie cannot be verified: "+ex.getMessage());
      return false;
    }
  }

  /**
   * Authorization check for object-level access: the session must carry a
   * signed auth cookie value that verifies and carries the admin role.
   * Fails closed (ForbiddenRequestException -> HTTP 403) otherwise.
   */
  static void requireAdminSession(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    Object auth = (session == null) ? null : session.getAttribute("auth");
    if (auth == null || !isAdmin(auth.toString())) {
      throw new ForbiddenRequestException();
    }
  }

  //
  @RequestMapping(value = "/admin/printSecrets", method = RequestMethod.POST)
  public String doPostPrintSecrets(HttpServletResponse response, HttpServletRequest request) {
    return fail;
  }


  @RequestMapping(value = "/admin/printSecrets", method = RequestMethod.GET)
  public String doGetPrintSecrets(@CookieValue(value = "auth", defaultValue = "notset") String auth, HttpServletResponse response, HttpServletRequest request) throws Exception {

    if (request.getSession().getAttribute("auth") == null) {
      return fail;
    }

    String authToken = request.getSession().getAttribute("auth").toString();
    if(!isAdmin(authToken)) {
      return fail;
    }

    ClassPathResource cpr = new ClassPathResource("static/calculations.csv");
    try {
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      response.getOutputStream().println(new String(bdata, StandardCharsets.UTF_8));
      return null;
    } catch (IOException ex) {
      ex.printStackTrace();
      // redirect to /
      return fail;
    }
  }

  /**
   * Handle login attempt
   * @param auth signed cookie value (role.hmac)
   * @param password hardcoded value
   * @param response -
   * @param request -
   * @return redirect to company numbers
   * @throws Exception
   */
  @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
  public String doPostLogin(@CookieValue(value = "auth", defaultValue = "notset") String auth, @RequestBody String password, HttpServletResponse response, HttpServletRequest request) throws Exception {
    String succ = "redirect:/admin/printSecrets";

    try {
      // no cookie no fun
      if (!auth.equals("notset")) {
        if(isAdmin(auth)) {
          request.getSession().setAttribute("auth",auth);
          return succ;
        }
      }

      // split password=value
      String[] pass = password.split("=");
      if(pass.length!=2) {
        return fail;
      }
      // compare pass
      if(pass[1] != null && pass[1].length()>0 && pass[1].equals("shiftleftsecret"))
      {
        AuthToken authToken = new AuthToken(AuthToken.ADMIN);
        String cookieValue = createAuthCookie(authToken);
        response.addCookie(new Cookie("auth", cookieValue ));

        // cookie is lost after redirection
        request.getSession().setAttribute("auth",cookieValue);

        return succ;
      }
      return fail;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      // no succ == fail
      return fail;
    }
  }

  /**
   * Same as POST but just a redirect
   * @param response
   * @param request
   * @return redirect
   */
  @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
  public String doGetLogin(HttpServletResponse response, HttpServletRequest request) {
    return "redirect:/";
  }
}
