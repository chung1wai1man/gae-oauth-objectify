package com.veersoft.server.shared;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.veersoft.server.boundary.OAuthType;

@Entity
public class RequestTokenEntity {
  @Id
  private String id;
  private OAuthType oAuthType;
  private String secret;
  private String rawResponse;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getRawResponse() {
    return rawResponse;
  }

  public void setRawResponse(String rawResponse) {
    this.rawResponse = rawResponse;
  }

  public OAuthType getOAuthType() {
    return oAuthType;
  }

  public void setOAuthType(OAuthType oAuthType) {
    this.oAuthType = oAuthType;
  }
}
