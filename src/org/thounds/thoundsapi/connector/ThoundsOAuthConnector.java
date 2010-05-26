package org.thounds.thoundsapi.connector;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.thounds.thoundsapi.ThoundsConnectionException;

import com.sun.jndi.toolkit.url.Uri;

public class ThoundsOAuthConnector implements ThoundsConnector {
	private static String CONSUMER_KEY = null;
	private static String CONSUMER_SECRET = null;
	private static String REQUEST_TOKEN_ENDPOINT_URL;
	private static String ACCESS_TOKEN_ENDPOINT_URL;
	private static String AUTHORIZE_WEBSITE_URL;
	private static String CALLBACK_URL;

	private CommonsHttpOAuthConsumer consumer;

	private OAuthProvider provider = new DefaultOAuthProvider(
			REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
			AUTHORIZE_WEBSITE_URL);
	HttpClient httpclient = new DefaultHttpClient();

	public ThoundsOAuthConnector(String consumerKey, String consumerSecret,
			String callbackUrl) {
		CONSUMER_KEY = consumerKey;
		CONSUMER_SECRET = consumerSecret;
		consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		CALLBACK_URL = callbackUrl;
	}

	public String getAuthenticationUrl() throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		return provider.retrieveRequestToken(consumer, CALLBACK_URL);
	}

	// verifier parametro oauth_verifier della query
	public void getAccessToken(String verifier)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		provider.retrieveAccessToken(consumer, verifier);
	}

	@Override
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		try {
			
			httpclient = new DefaultHttpClient();
			consumer.sign(request);
			return httpclient.execute(request);
			
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
		// TODO Auto-generated method stub
		return false;
	}

}
