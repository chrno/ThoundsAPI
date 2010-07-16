package org.thounds.thoundsapi.connector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.thounds.thoundsapi.ThoundsConnectionException;

public interface ThoundsConnector {

	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException;

	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException;
	
	public boolean isAuthenticated();

}
