package com.veersoft.server.utlities;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Utility {

  public static Map<String, String> getCookieElements(HttpServletRequest req) {
    Map<String, String> map = new HashMap<String, String>();
    Cookie[] cookie = req.getCookies();
    if (cookie != null) {
      for (int i = 0; i < cookie.length; i++) {
        Cookie cookie2 = cookie[i];
        map.put(cookie2.getName(), cookie2.getValue());
      }
    }
    return map;
  }

}
