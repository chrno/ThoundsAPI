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
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

/**
 * Classe che consente di comunicare con Thounds usando l'autenticazione tramite digest
 * 
 */
public class ThoundsDigestConnector implements ThoundsConnector {

	private String USERNAME = "";
	private String PASSWORD = "";
	private boolean isAuthenticated = false;
	private DefaultHttpClient httpclient = new DefaultHttpClient();

	/**
	 * Metodo di login con il quale si settano le credenziali che poi il connettore
	 * userà per comunicare con Thounds
	 * 
	 * @param username username dell'utente
	 * @param password password dell'utente
	 * @return {@code true} se le credenziali fornite sono corrette, {@code false} altrimenti
	 * @throws ThoundsConnectionException eccezione sollevata nel caso si verifichino errori di connessione
	 */
	public boolean login(String username, String password)
			throws ThoundsConnectionException {

		USERNAME = username;
		PASSWORD = password;

		StringBuilder uriBuilder = new StringBuilder(
				"http://thounds.com/profile");
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		
		try {
			HttpResponse response = executeAuthenticatedHttpRequest(httpget);
			isAuthenticated = response.getStatusLine().getStatusCode() == 200;
		} catch (ThoundsNotAuthenticatedexception e) {
			isAuthenticated = false;
		}
		
		return isAuthenticated;
	}

	/**
	 * Metodo che cancella le credenziali di accesso impostate tramite il metodo di login.
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
