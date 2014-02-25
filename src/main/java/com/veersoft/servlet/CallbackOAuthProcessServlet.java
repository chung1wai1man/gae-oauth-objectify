package com.veersoft.servlet;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.veersoft.server.boundary.OAuthService;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.boundary.exception.OAuthProviderException;
import com.veersoft.server.impls.OAuthServiceFactory;
import com.veersoft.server.impls.VsOAuthService;
import com.veersoft.server.shared.RequestTokenEntity;
import com.veersoft.server.utlities.ServerConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallbackOAuthProcessServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private Logger log = Logger.getLogger(getClass().getName());

  /**
   * User: Sarath U
   * Date: 24.02.2014
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    log.info("Calling CallbackOAuthProcessServlet.doGet()");

    log.info("URL: " + fullRequestUrl(req));

    String oAuthToken = req.getParameter("oauth_token");
    log.info("oAuthToken: " + oAuthToken);
    Objectify ofy = ObjectifyService.ofy();
    VsOAuthService oAuthService = OAuthServiceFactory.getVsOAuthService();
    RequestTokenEntity requestTokenEntity = oAuthService.getTokenEntity(oAuthToken);
    // OAuthUser user = (OAuthUser) req.getSession().getAttribute("user");
    OAuthUser user = new OAuthUser();
    if (requestTokenEntity != null) {
      // The providers using OAuth1.0
      user.setOAuthType(requestTokenEntity.getOAuthType());
      user.setOAuthSecret(requestTokenEntity.getSecret());
      user.setOAuthToken(oAuthToken);
      user.setOAuthRawResponse(requestTokenEntity.getRawResponse());
      String oAuthVerifier = req.getParameter("oauth_verifier");
      user.setOAuthVerifier(oAuthVerifier);
      log.info("oAuthVerifier: " + oAuthVerifier);
      String callbackUrl = req.getScheme() + "://" + req.getHeader("Host") + "/callbackoauth";
      user.setCallbackUrl(callbackUrl);

      // calling service
      OAuthService service = new OAuthService();
      try {
        user = service.readingUserData(user);
      } catch (OAuthProviderException e) {
        log.log(Level.SEVERE, e.getMessage(), e);
        throw new ServletException(e);
      }

      // Logging
      log.info("User: providerUserId => " + user.getProviderUserId());
      log.info("User: nickname => " + user.getNickname());
      log.info("User: name => " + user.getName());
      log.info("User: eMail => " + user.getEMail());

      createAndAddCookies(res, requestTokenEntity.getOAuthType().getName(), user);
      ofy.delete().entity(requestTokenEntity).now();
      // put it to session
      //      req.getSession().setAttribute("user", user);
    } else {
      log.log(Level.SEVERE, "There is no tokenEntity");
    }
    res.sendRedirect("/admin/success");
  }

  private void createAndAddCookies(HttpServletResponse res, String oAuthType, OAuthUser user) {
    Cookie cookie = new Cookie(ServerConstants.USER_NAME, user.getName());
    cookie.setMaxAge(30 * 60);
    res.addCookie(cookie);
    cookie = new Cookie(ServerConstants.OAUTH_TYPE_KEY, oAuthType);
    cookie.setMaxAge(30 * 60);
    res.addCookie(cookie);
  }

  private String fullRequestUrl(HttpServletRequest request) {
    String reqUrl = request.getRequestURL().toString();
    String queryString = request.getQueryString();
    if (queryString != null) {
      reqUrl += "?"+queryString;
    }
    return reqUrl;
  }
}
