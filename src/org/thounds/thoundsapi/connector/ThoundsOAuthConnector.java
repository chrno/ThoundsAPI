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
import org.thounds.thoundsapi.ThoundsConnectionException;

/**
 * Classe che consente di comunicare con Thounds usando l'autenticazione tramite OAuth
 *
 */
public class ThoundsOAuthConnector implements ThoundsConnector {
	private static String REQUEST_TOKEN_ENDPOINT_URL="http://thounds.com/oauth/request_token";
	private static String ACCESS_TOKEN_ENDPOINT_URL="http://thounds.com/oauth/access_token";
	private static String AUTHORIZE_WEBSITE_URL = "http://thounds.com/oauth/authorize";
	
	private String CONSUMER_KEY = null;
	private String CONSUMER_SECRET = null;
	private String CALLBACK_URL=null;
	private boolean isAuthenticated = false;
	private CommonsHttpOAuthConsumer consumer;

	private OAuthProvider provider = new DefaultOAuthProvider(
			REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
			AUTHORIZE_WEBSITE_URL);
	HttpClient httpclient;

	/**
	 * Costruttore
	 * 
	 * @param consumerKey consumer key ottenuto da Thounds
	 * @param consumerSecret consumer secret key ottenuto da Thounds
	 * @param callbackUrl callback url dell'applicazione
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
	 * Metodo per ottenere l'url per autorizzare l'applicazione
	 * 
	 * @return url per autorizzare l'applicazione 
	 * @throws ThoundsConnectionException se si verificano problemi di comunicazione con il server
	 * @throws ThoundsOAuthParameterExcepion se i parametri di configurazione dai al costruttore sono errati 
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
	 * Metodo per ottenere i token di accesso da Thounds
	 * 
	 * @param verifier codice di verifica ricevuto tramite callback da Thounds
	 * @throws ThoundsConnectionException se si verificano problemi di comunicazione con il server
	 * @throws ThoundsOAuthParameterExcepion se il parametro verifier è errato
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
	 * Metodo per impostare i token di accesso
	 * 
	 * @param token access token ottenuto da Thounds
	 * @param tokenSecret access token secret ottenuto da Thounds
	 */
	public void setAccessToken(String token, String tokenSecret){
		consumer.setTokenWithSecret(token, tokenSecret);
		isAuthenticated = true;
	}
	
	/**
	 * Metodo di logout che cancella i token di accesso ottenuti da Thounds 
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
		StringBuilder uriBuilder = new StringBuilder("http://thounds.com/profile");
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
	 * Metodo per ottenere l'access token
	 * @return access token
	 */
	public String getToken(){
		return consumer.getToken();
	}
	
	/**
	 * Metodo per ottenere l'access token secret
	 * @return access token secret
	 */
	public String getTokenSecret(){
		return consumer.getTokenSecret();
	}
}
