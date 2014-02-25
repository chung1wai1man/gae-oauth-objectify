package com.veersoft.server.impls;

import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.shared.RequestTokenEntity;

/**
 * User: Sarath U
 * Date: 24.02.2014
 * Time: 18:02:06
 */
public class VsOAuthServiceImpl implements VsOAuthService {
  private Logger log = Logger.getLogger(getClass().getName());

  @Override
  public RequestTokenEntity createTokenEntity(OAuthUser user) {
    log.info("creating the Token Entity");
    Objectify ofy = ObjectifyService.ofy();
    RequestTokenEntity tokenEntity = new RequestTokenEntity();
    tokenEntity.setId(user.getOAuthToken());
    tokenEntity.setRawResponse(user.getOAuthRawResponse());
    tokenEntity.setSecret(user.getOAuthSecret());
    tokenEntity.setOAuthType(user.getOAuthType());
    ofy.save().entity(tokenEntity).now();
    log.info("Token Entity has been created");
    return tokenEntity;
  }

  @Override
  public RequestTokenEntity getTokenEntity(String id) {
    RequestTokenEntity tokenEntity = null;
    if (id != null && !id.isEmpty()) {
      Objectify ofy = ObjectifyService.ofy();
      // The providers using OAuth1.0
      Key<RequestTokenEntity> tokenEntityKey = Key.create(RequestTokenEntity.class, id);
      tokenEntity = ofy.load().key(tokenEntityKey).safe();
    }
    return tokenEntity;
  }
}
