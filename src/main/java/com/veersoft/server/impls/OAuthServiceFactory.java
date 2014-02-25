package com.veersoft.server.impls;


/**
 * User: Sarath U
 * Date: 24.02.2014
 */
public class OAuthServiceFactory {
  private static VsOAuthService vsOAuthService = new VsOAuthServiceImpl();

  public static VsOAuthService getVsOAuthService() {
    return vsOAuthService;
  }
}
