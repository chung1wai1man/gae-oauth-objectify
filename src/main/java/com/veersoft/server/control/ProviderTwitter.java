package com.veersoft.server.control;

import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;

import com.veersoft.server.api.TwitterApi;
import com.veersoft.server.boundary.OAuthUser;
import com.veersoft.server.boundary.exception.OAuthProviderException;

/**
 * User: hal
 * Date: 18.06.2011
 * Time: 17:39:29
 */
public class ProviderTwitter extends AbstractProvider {

  private Logger log = Logger.getLogger(getClass().getName());
  public void buildRedirectUrl(OAuthUser user) {
    OAuthService service = new ServiceBuilder()
    .provider(TwitterApi.class)
    .apiKey("ZWl3zuQBqnpIz8VtOAWw")
    .apiSecret("jMfKy6pFaVA7RP3T5Oukrv5CQGEPkZwA9V2FcARA")
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
    .provider(TwitterApi.class)
    .apiKey("ZWl3zuQBqnpIz8VtOAWw")
    .apiSecret("jMfKy6pFaVA7RP3T5Oukrv5CQGEPkZwA9V2FcARA")
    .callback("https://oauthtestapplication.appspot.com/callbackoauth")
    .build();

    Verifier verifier = new Verifier(user.getOAuthVerifier());
    Token token = new Token(user.getOAuthToken(), user.getOAuthSecret(), user.getOAuthRawResponse());
    Token accessToken = service.getAccessToken(token, verifier);

    // get GUID
    OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
    service.signRequest(accessToken, request);

    Response response = request.send();

    checkResponseCode(response.getCode());

    String uglyJson = response.getBody();
    log.info("uglyJson response: " + uglyJson);
    String json = prettyPrintJsonString(uglyJson);
    log.info("json response: " + json);
    JSONParser jsonParser = new JSONParser();
    Object object = null;
    try {
      object = jsonParser.parse(json);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    JSONObject jsonObj = (JSONObject) object;
    Long long1 = (Long) jsonObj.get("id");
    user.setProviderUserId(long1.toString());
    log.info("Found id: " + user.getProviderUserId());
    user.setName((String) jsonObj.get("name"));
    user.setNickname((String) jsonObj.get("screen_name"));
  }

  private String prettyPrintJsonString(String uglyJson) {
    try {
      JsonRootNode jsonRootNode = new JdomParser().parse(uglyJson);
      JsonFormatter jsonFormatter = new PrettyJsonFormatter();
      String prettyJson = jsonFormatter.format(jsonRootNode);
      return prettyJson;
    }
    catch (Exception e) {
      return uglyJson;
    }
  }
}
