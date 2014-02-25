package com.veersoft.server.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.boundary.exception.OAuthProviderException;

/**
 * User: hal
 * Date: 13.06.2011
 * Time: 21:40:37
 */
public class ProviderYahoo extends AbstractProvider {
  private Logger log = Logger.getLogger(getClass().getName());

  public void buildRedirectUrl(OAuthUser user) {
    OAuthService service = new ServiceBuilder()
    .provider(YahooApi.class)
    .apiKey("dj0yJmk9SHdWaUFocEF6SXBUJmQ9WVdrOWRWbFRWamRITmpRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03Yg--")
    .apiSecret("ccb61fd3555375f7af9c68f6ed80eed830f63ae8")
    .callback("https://oauthtestapplication.appspot.com/callbackoauth")
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
    .provider(YahooApi.class)
    .apiKey("dj0yJmk9SHdWaUFocEF6SXBUJmQ9WVdrOWRWbFRWamRITmpRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03Yg--")
    .apiSecret("ccb61fd3555375f7af9c68f6ed80eed830f63ae8")
    .callback("https://oauthtestapplication.appspot.com/callbackoauth")
    .build();
    Verifier verifier = new Verifier(user.getOAuthVerifier());
    Token token = new Token(user.getOAuthToken(), user.getOAuthSecret(), user.getOAuthRawResponse());
    Token accessToken = service.getAccessToken(token, verifier);

    // get GUID
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/me/guid");
    service.signRequest(accessToken, request);

    Response response = request.send();

    checkResponseCode(response.getCode());

    String xml = response.getBody();
    log.info("XML response: " + xml);

    String guid = null;

    String xPathExpressionGuid = "/guid/value/text()";
    try {
      guid = getValueOverXPath(xml, xPathExpressionGuid);
      user.setProviderUserId(guid);
      log.info("Found guid: " + guid);
    } catch(Exception e) {
      log.log(Level.SEVERE, "Can't find expression: " + xPathExpressionGuid + " => XML: " + xml, e);
      throw new OAuthProviderException("Can't find expression: " + xPathExpressionGuid);
    }


    // get User data
    //        request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/user/" + guid + "/profile/status");
    //        request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/user/" + guid + "/profile/usercard");
    request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/user/" + guid + "/profile");
    //        request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/user/" + guid + "/contacts");
    service.signRequest(accessToken, request);

    response = request.send();

    checkResponseCode(response.getCode());

    xml = response.getBody();
    log.info("XML response: " + xml);

    String xPathExpressionName = "/profile/nickname/text()";
    try {
      String nickname = getValueOverXPath(xml, xPathExpressionName);
      user.setName(nickname);
    } catch(Exception e) {
      log.log(Level.SEVERE, "Can't find expression: " + xPathExpressionName + " => XML: " + xml, e);
      throw new OAuthProviderException("Can't find expression: " + xPathExpressionName);
    }
    String xPathExpressionName1 = "/profile/email/text()";
    try {
      String nickname = getValueOverXPath(xml, xPathExpressionName1);
      user.setEMail(nickname);
    } catch(Exception e) {
      log.log(Level.SEVERE, "Can't find expression: " + xPathExpressionName1 + " => XML: " + xml, e);
      throw new OAuthProviderException("Can't find expression: " + xPathExpressionName1);
    }
    // get GUID
    request = new OAuthRequest(Verb.GET, "http://social.yahooapis.com/v1/me/guid?format=json");
    service.signRequest(accessToken, request);

    response = request.send();

    checkResponseCode(response.getCode());

    xml = response.getBody();
    log.info("XML response: " + xml);
  }
}
