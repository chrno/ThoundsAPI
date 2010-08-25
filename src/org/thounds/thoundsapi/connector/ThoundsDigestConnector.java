package org.thounds.thoundsapi.connector;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

/**
 * {@code ThoundsDigestConnector} let you connect with Thounds API using the HTTP Digest authentication protocol.
 */

public class ThoundsDigestConnector implements ThoundsConnector {

	private String USERNAME = "";
	private String PASSWORD = "";
	private boolean isAuthenticated = false;
	private DefaultHttpClient httpclient = new DefaultHttpClient();

	/**
	 * Sets user authentication credentials.
	 * 
	 * @param username the username.
	 * @param password the password.
	 * @return {@code true} if authentication credentials are correct, {@code false} otherwise.
	 * @throws ThoundsConnectionException in case the connection was aborted.
	 */
	public boolean login(String username, String password)
			throws ThoundsConnectionException {

		USERNAME = username;
		PASSWORD = password;

		StringBuilder uriBuilder = new StringBuilder(Thounds.HOST + "/profile");
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		
		try {
			HttpResponse response = executeAuthenticatedHttpRequest(httpget);
			isAuthenticated = response.getStatusLine().getStatusCode() == Thounds.SUCCESS;
		} catch (ThoundsNotAuthenticatedexception e) {
			isAuthenticated = false;
		}
		
		return isAuthenticated;
	}

	/**
	 * Unsets user authentication credentials.
	 */
	public void logout() {
		USERNAME = "";
		PASSWORD = "";
		isAuthenticated = false;
	}

	@Override
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {

		HttpResponse responce = null;
		try {
			
			HttpProtocolParams.setUseExpectContinue(httpclient.getParams(),
					false);
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(null, 80, "thounds", "Digest"),
					new UsernamePasswordCredentials(USERNAME, PASSWORD));
			
			synchronized (httpclient) {
				responce = httpclient.execute(request);
			}

			return responce;
		} catch (ClientProtocolException e) {
			isAuthenticated = false;
			throw new ThoundsNotAuthenticatedexception();
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}

	}

	@Override
	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		HttpResponse responce = null;
		try {
			HttpProtocolParams.setUseExpectContinue(httpclient.getParams(),
					false);
			synchronized (httpclient) {
				responce = httpclient.execute(request);
			}
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
		return responce;
	}

	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

}
