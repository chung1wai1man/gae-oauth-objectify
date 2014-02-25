package com.veersoft.server.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.boundary.exception.OAuthProviderException;

/**
 * User: hal
 * Date: 13.06.2011
 * Time: 20:23:47
 */
public class ProviderGoogle extends AbstractProvider {
  private Logger log = Logger.getLogger(getClass().getName());
  public void buildRedirectUrl(OAuthUser user) {
    OAuthService service = new ServiceBuilder()
    .provider(GoogleApi.class)
    .apiKey("anonymous")
    .apiSecret("anonymous")
    .scope("http://www.google.com/m8/feeds/")
    .callback(user.getCallbackUrl())
    .build();
    Token token = service.getRequestToken();
    log.info("requestToken: " + token);

    String redirect = service.getAuthorizationUrl(token);
    log.info("Redirect URL: " + redirect);

    user.setOAuthToken(token.getToken());
    user.setOAuthSecret(token.getSecret());
    user.setOAuthRawResponse(token.getRawResponse());

    user.setOAuthAuthorizationUrl(redirect);
  }

  public void readUserData(OAuthUser user)
      throws OAuthProviderException {

    OAuthService service = new ServiceBuilder()
    .provider(GoogleApi.class)
    .apiKey("anonymous")
    .apiSecret("anonymous")
    .scope("http://www.google.com/m8/feeds/")
    .callback(user.getCallbackUrl())
    .build();

    Verifier verifier = new Verifier(user.getOAuthVerifier());
    Token token = new Token(user.getOAuthToken(), user.getOAuthSecret(), user.getOAuthRawResponse());
    Token accessToken = service.getAccessToken(token, verifier);


    OAuthRequest request = new OAuthRequest(Verb.GET, "http://www.google.com/m8/feeds/contacts/default/full");
    service.signRequest(accessToken, request);
    request.addHeader("GData-Version", "3.0");
    Response response = request.send();

    checkResponseCode(response.getCode());

    String xml = response.getBody();
    log.info("XML response: " + xml);

    String xPathExpressionName = "/feed/author/name/text()";
    try {
      String authorName = getValueOverXPath(xml, xPathExpressionName);
      user.setName(authorName);
    } catch(Exception e) {
      log.log(Level.SEVERE, "Can't find expression: " + xPathExpressionName + " => XML: " + xml, e);
      throw new OAuthProviderException("Can't find expression: " + xPathExpressionName);
    }

    String xPathExpressionEMail = "/feed/author/email/text()";
    try {
      String authorEMail = getValueOverXPath(xml, xPathExpressionEMail);
      user.setEMail(authorEMail);
    } catch(Exception e) {
      log.log(Level.SEVERE, "Can't find expression: " + xPathExpressionEMail + " => XML: " + xml, e);
      throw new OAuthProviderException("Can't find expression: " + xPathExpressionEMail);
    }
  }
}
