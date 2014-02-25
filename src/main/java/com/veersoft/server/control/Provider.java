package com.veersoft.server.control;

import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.boundary.exception.OAuthProviderException;

/**
 * User: hal
 * Date: 13.06.2011
 * Time: 20:23:35
 */
public interface Provider {
  public void buildRedirectUrl(OAuthUser user);

  public void readUserData(OAuthUser user)
      throws OAuthProviderException;

}
