package org.thounds.thoundsapi.connector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

/**
 * Interfaccia che descrive i metodi necessari per implementare una classe che
 * permetta di comunicare con Thounds in modo autenticato che non.
 *
 */
public interface ThoundsConnector {

	/**
	 * Metodo per effettuare richieste senza autenticazione
	 * 
	 * @param request richiesta da inviare al server
	 * @return responso della richiesta effettuata
	 * @throws ThoundsConnectionException eccezione sollevata nel caso si verifichino errori di connessione
	 */
	public HttpResponse executeHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException;

	/**
	 * Metodo per effettuare richieste con autenticazione
	 * 
	 * @param request richiesta da inviare al server
	 * @return responso della richiesta effettuata
	 * @throws ThoundsConnectionException eccezione sollevata nel caso si verifichino errori di connessione
	 * @throws ThoundsNotAuthenticatedexception eccezione sollevata nel caso le credenziali di accesso siano errate
	 */
	public HttpResponse executeAuthenticatedHttpRequest(HttpUriRequest request)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception;
	
	/**
	 * Metodo per verificare se le credenziali di autenticazione sono state impostate
	 * 
	 * @return {@code true} se le credenziali sono definite, {@code false} altrimenti
	 */
	public boolean isAuthenticated();

}
