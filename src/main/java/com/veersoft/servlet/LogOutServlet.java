package com.veersoft.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogOutServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private Logger log = Logger.getLogger(getClass().getName());

  /**
   * User: Sarath U
   * Date: 24.02.2014
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Cookie[] cookie = req.getCookies();
    if (cookie != null) {
      for (int i = 0; i < cookie.length; i++) {
        Cookie cookie2 = cookie[i];
        cookie2.setMaxAge(0);
        resp.addCookie(cookie2);
        log.info("age: " + cookie2.getMaxAge());
      }
    }
    resp.sendRedirect("/");
  }
}
