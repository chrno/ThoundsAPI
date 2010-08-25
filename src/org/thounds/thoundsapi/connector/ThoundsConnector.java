package org.thounds.thoundsapi.connector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

/**
 * {@code ThoundsConnector} interface describes methods to connect with Thounds API.
 */
public interface ThoundsConnector {

	/**
	 * Performs an unauthenticated request.
	 * 
	 * @param request the request.
	 * @return the response.
	 * @throws ThoundsConnectionException in case the connection was aborted.
	 */
	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException;

	/**
	 * Performs an authenticated request.
	 * 
	 * @param request the request.
	 * @return the response.
	 * @throws ThoundsConnectionException in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedException in case the request was not authenticated.
	 */
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception;
	
	/**
	 * Returns {@code true} if authentication credentials are set.
	 * 
	 * @return {@code true} if authentication credentials are set, {@code false} otherwise.
	 */
	public boolean isAuthenticated();

}
