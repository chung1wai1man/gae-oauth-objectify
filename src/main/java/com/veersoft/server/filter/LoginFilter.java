package com.veersoft.server.filter;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import com.veersoft.server.shared.RequestTokenEntity;
import com.veersoft.server.utlities.ServerConstants;
import com.veersoft.server.utlities.Utility;

public class LoginFilter implements Filter {
  private Logger log = Logger.getLogger(getClass().getName());

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse,
      FilterChain paramFilterChain) throws IOException, ServletException {
    HttpServletResponse resp = (HttpServletResponse) paramServletResponse;
    HttpServletRequest req = (HttpServletRequest) paramServletRequest;
    Map<String, String> map = Utility.getCookieElements(req);
    String userName = (String) map.get(ServerConstants.USER_NAME);
    if (userName == null || userName.isEmpty()) {
      log.info("userName is null for the login filer");
      String serviceProvider = (String) map.get(ServerConstants.OAUTH_TYPE_KEY);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("/startoauth");
      if (serviceProvider != null) {
        stringBuilder.append("?provider=" + serviceProvider);
      }
      resp.sendRedirect(stringBuilder.toString());
    } else {
      log.info("userName: " + userName);
      String serviceProvider = (String) map.get(ServerConstants.OAUTH_TYPE_KEY);
      log.info("serviceProvider: " + serviceProvider);
      req.getRequestDispatcher("/admin/success").forward(req, resp);
    }
  }

  @Override
  public void init(FilterConfig paramFilterConfig) throws ServletException {
    ObjectifyService.register(RequestTokenEntity.class);
  }

}
