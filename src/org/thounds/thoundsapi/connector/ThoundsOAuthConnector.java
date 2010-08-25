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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;

/**
 * {@code ThoundsOAuthConnector} let you connect with Thounds API using the OAuth protocol.
 */

public class ThoundsOAuthConnector implements ThoundsConnector {
	private static String REQUEST_TOKEN_ENDPOINT_URL = Thounds.HOST + "/oauth/request_token";
	private static String ACCESS_TOKEN_ENDPOINT_URL = Thounds.HOST + "/oauth/access_token";
	private static String AUTHORIZE_WEBSITE_URL = Thounds.HOST + "/oauth/authorize";
	
	private String CONSUMER_KEY = null;
	private String CONSUMER_SECRET = null;
	private String CALLBACK_URL = null;
	private boolean isAuthenticated = false;
	private CommonsHttpOAuthConsumer consumer;

	private OAuthProvider provider = new DefaultOAuthProvider(
			REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
			AUTHORIZE_WEBSITE_URL);
	HttpClient httpclient;

	/**
	 * Creates a {@code ThoundsOAuthConnector} object.
	 * 
	 * @param consumerKey the consumer application key.
	 * @param consumerSecret the consumer application secret.
	 * @param callbackUrl the consumer application callback URL.
	 */
	public ThoundsOAuthConnector(String consumerKey, String consumerSecret,
			String callbackUrl) {
		CONSUMER_KEY = consumerKey;
		CONSUMER_SECRET = consumerSecret;
		consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		CALLBACK_URL = callbackUrl;
		httpclient = new DefaultHttpClient();
	}

	/**
	 * Returns the authorization URL used to get a request token.
	 * 
	 * @return the authorization URL. 
	 * @throws ThoundsConnectionException in case the connection was aborted.
	 * @throws ThoundsOAuthParameterExcepion in case of bad consumer application key or secret.
	 */
	public String getAuthorizeUrl() throws ThoundsConnectionException, ThoundsOAuthParameterExcepion {
		try {
			return provider.retrieveRequestToken(consumer, CALLBACK_URL);
		} catch (OAuthMessageSignerException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthNotAuthorizedException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthExpectationFailedException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthCommunicationException e) {
			throw new ThoundsConnectionException();
		}
	}

	/**
	 * Returns an access token to the service provider.
	 * 
	 * @param verifier the verifier code returned by the authorization process.
	 * @throws ThoundsConnectionException in case the connection was aborted.
	 * @throws ThoundsOAuthParameterExcepion in case of bad OAuth request parameters.
	 */
	// verifier parametro oauth_verifier della query
	public void retrieveAccessToken(String verifier) throws ThoundsOAuthParameterExcepion, ThoundsConnectionException {
		try {
			provider.retrieveAccessToken(consumer, verifier);
		} catch (OAuthMessageSignerException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthNotAuthorizedException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthExpectationFailedException e) {
			throw new ThoundsOAuthParameterExcepion();
		} catch (OAuthCommunicationException e) {
			throw new ThoundsConnectionException();
		}
		isAuthenticated = true;
	}
	
	/**
	 * Sets access token and secret.
	 * 
	 * @param token the access token.
	 * @param tokenSecret the access token secret.
	 */
	public void setAccessToken(String token, String tokenSecret){
		consumer.setTokenWithSecret(token, tokenSecret);
		isAuthenticated = true;
	}
	
	/**
	 * Unsets access token and secret. 
	 */
	public void logout() {
		isAuthenticated = false;
		consumer.setTokenWithSecret(null, null);
	}

	@Override
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		try {
			consumer.sign(request);
			HttpResponse response;
			synchronized (httpclient) {
				response = httpclient.execute(request);
			}		
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
		try {
			HttpResponse response;
			synchronized (httpclient) {
				response = httpclient.execute(request);
			}		
			return response;
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
	}

	@Override
	public boolean isAuthenticated() {
		StringBuilder uriBuilder = new StringBuilder(Thounds.HOST + "/profile");
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response;
		try {
			response = executeAuthenticatedHttpRequest(httpget);
			return response.getStatusLine().getStatusCode()==200;
		} catch (ThoundsConnectionException e) {
			return false;
		}
	}

	/**
	 * Returns the access token.
	 * 
	 * @return the access token.
	 */
	public String getToken(){
		return consumer.getToken();
	}
	
	/**
	 * Returns the access token secret.
	 * 
	 * @return the access token secret.
	 */
	public String getTokenSecret(){
		return consumer.getTokenSecret();
	}
}
