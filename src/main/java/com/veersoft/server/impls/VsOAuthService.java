package com.veersoft.server.impls;

import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.shared.RequestTokenEntity;

/**
 * User: Sarath U
 * Date: 24.02.2014
 */
public interface VsOAuthService {
  public RequestTokenEntity createTokenEntity(OAuthUser user);
  public RequestTokenEntity getTokenEntity(String id);
}
