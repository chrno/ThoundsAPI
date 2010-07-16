package org.thounds.thoundsapi.connector;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.thounds.thoundsapi.ThoundsConnectionException;

public class ThoundsDigestConnector implements ThoundsConnector {

	protected String USERNAME = "";
	protected String PASSWORD = "";
	private boolean isAuthenticated = false;
	private DefaultHttpClient httpclient = null;

	public boolean login(String username, String password)
			throws ThoundsConnectionException {

		USERNAME = username;
		PASSWORD = password;
		
		StringBuilder uriBuilder = new StringBuilder(
				"http://thounds.com/profile");
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeAuthenticatedHttpRequest(httpget);
		isAuthenticated = response.getStatusLine().getStatusCode()==200;
		return isAuthenticated;
	}

	public void logout() {
		USERNAME = "";
		PASSWORD = "";
		isAuthenticated = false;
		httpclient.getConnectionManager().shutdown();
	}

	@Override
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {

		
		HttpResponse responce = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), false);
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(null, 80, "thounds", "Digest"),
					new UsernamePasswordCredentials(USERNAME, PASSWORD));
			responce = httpclient.execute(request);
			return responce;
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
	}

	@Override
	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException {
		HttpResponse responce = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), false);
			responce = httpclient.execute(request);
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
