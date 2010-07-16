package org.thounds.thoundsapi.connector;

import java.io.IOException;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.thounds.thoundsapi.ThoundsConnectionException;

/**
 * 
 *
 */
public class ThoundsOAuthConnector implements ThoundsConnector {
	private static String REQUEST_TOKEN_ENDPOINT_URL="http://stage.thounds.com/oauth/request_token";
	private static String ACCESS_TOKEN_ENDPOINT_URL="http://stage.thounds.com/oauth/access_token";
	private static String AUTHORIZE_WEBSITE_URL = "http://stage.thounds.com/oauth/authorize";
	
	private String CONSUMER_KEY = null;
	private String CONSUMER_SECRET = null;
	private String CALLBACK_URL=null;
	private boolean isAuthenticated = false;
	private CommonsHttpOAuthConsumer consumer;

	private OAuthProvider provider = new DefaultOAuthProvider(
			REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
			AUTHORIZE_WEBSITE_URL);
	HttpClient httpclient = new DefaultHttpClient();

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 */
	public ThoundsOAuthConnector(String consumerKey, String consumerSecret,
			String callbackUrl) {
		CONSUMER_KEY = consumerKey;
		CONSUMER_SECRET = consumerSecret;
		consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		CALLBACK_URL = callbackUrl;
	}

	/**
	 * 
	 * @return
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public String getAuthenticationUrl() throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		return provider.retrieveRequestToken(consumer, CALLBACK_URL);
	}

	/**
	 * 
	 * @param verifier
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	// verifier parametro oauth_verifier della query
	public void retrieveAccessToken(String verifier)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		provider.retrieveAccessToken(consumer, verifier);
		isAuthenticated = true;
	}
	
	/**
	 * 
	 * @param token
	 * @param tokenSecret
	 */
	public void setAccessToken(String token, String tokenSecret){
		consumer.setTokenWithSecret(token, tokenSecret);
		isAuthenticated = true;
	}
	
	/**
	 * 
	 */
	public void logout() {
		isAuthenticated = false;
		consumer.setTokenWithSecret(null, null);
		httpclient.getConnectionManager().shutdown();
	}

	@Override
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		try {
			
			httpclient = new DefaultHttpClient();
			consumer.sign(request);
			HttpResponse response = httpclient.execute(request);
			
			return response;
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
		
		return null;
	}
	
	@Override
	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		httpclient = new DefaultHttpClient();
		try {
			return httpclient.execute(request);
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
	}

	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public String getToken(){
		return consumer.getToken();
	}
	
	public String getTokenSecret(){
		return consumer.getTokenSecret();
	}
}
