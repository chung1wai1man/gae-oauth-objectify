package com.veersoft.servlet;

import com.veersoft.server.boundary.OAuthService;
import com.veersoft.server.boundary.OAuthType;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.impls.OAuthServiceFactory;
import com.veersoft.server.impls.VsOAuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartOAuthProcessServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private Logger log = Logger.getLogger(getClass().getName());

  /**
   * User: Sarath U
   * Date: 24.02.2014
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    log.info("Calling StartOAuthProcessServlet.doGet()");

    // reading parameter
    String provider = req.getParameter("provider");
    // Default Provider.
    if (provider == null) {
      provider = OAuthType.GOOGLE.getName();
    }

    OAuthType oAuthType = OAuthType.getOAuthTypeByName(provider);
    log.info("Provider: '" + provider + "' and OAuthType: '" + oAuthType.getName() + "'");

    OAuthUser user = new OAuthUser();
    user.setOAuthType(oAuthType);

    // using service
    OAuthService service = new OAuthService();
    try {
      String callbackUrl = req.getScheme() + "://" + req.getHeader("Host") + "/callbackoauth";
      user.setCallbackUrl(callbackUrl);
      user = service.addAuthorizationUrl(user);
    } catch(Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw new ServletException(e);
    }

    VsOAuthService oAuthService = OAuthServiceFactory.getVsOAuthService();
    oAuthService.createTokenEntity(user);

    // put user into session
    /*
     *  This session has been removed because the server is appending the JSessionId to the
     *   authorization Url so that request is not going to the google authorization server.
     */
    // req.getSession().setAttribute("user", user);

    // do redirect
    log.info("Redirect URL: " + user.getOAuthAuthorizationUrl());
    res.sendRedirect(user.getOAuthAuthorizationUrl());
  }
}
