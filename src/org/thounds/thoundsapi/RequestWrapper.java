package org.thounds.thoundsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provide a list of static methods to manage the communication
 * between a Java application and Thounds
 */

public class RequestWrapper {
	// private static String STAGE_HOST_PATH = "http://stage.thounds.com";
	private static String HOST = "http://thounds.com";
	private static String PROFILE_PATH = "/profile";
	private static String HOME_PATH = "/home";
	private static String USERS_PATH = "/users";
	private static String BAND_PATH = "/band";
	private static String FRIENDSHIPS_PATH = "/friendships";
	private static String THOUNDS_PATH = "/thounds";
	private static String LIBRARY_PATH = "/library";
	private static String NOTIFICATIONS_PATH = "/notifications";
	private static DefaultHttpClient httpclient = null;

	protected static String USERNAME = "";
	protected static String PASSWORD = "";
	private static boolean isLogged = false;

	private static HttpResponse executeHttpRequest(HttpUriRequest request,
			boolean useAuthentication) throws ThoundsConnectionException {
		if (httpclient == null)
			httpclient = new DefaultHttpClient();

		HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), false);
		if (useAuthentication) {
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(null, 80, "thounds", "Digest"),
					new UsernamePasswordCredentials(USERNAME, PASSWORD));
		}
		HttpResponse responce = null;
		try {
			responce = httpclient.execute(request);
			// client.getConnectionManager().shutdown();
		} catch (IOException e) {
			throw new ThoundsConnectionException();
		}
		return responce;
	}

	private static JSONObject httpResponseToJSONObject(HttpResponse response)
			throws IllegalThoundsObjectException {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return new JSONObject(result);
		} catch (Exception e) {
			throw new IllegalThoundsObjectException();
		}
	}

	/**
	 * Thounds login method.
	 * 
	 * @param username
	 *            is the mail address of an active Thounds user
	 * @param password
	 *            is the password associated to the user
	 * @return {@code true} for successfull login, {@code false} otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static boolean login(String username, String password)
			throws ThoundsConnectionException {
		USERNAME = username;
		PASSWORD = password;
		JSONObject json;
		try {
			StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH);
			HttpGet httpget = new HttpGet(uriBuilder.toString());
			httpget.addHeader("Accept", "application/json");
			HttpResponse response = executeHttpRequest(httpget, true);
			json = httpResponseToJSONObject(response);

			if ((json == null) || (!json.getString("email").equals(username))) {
				USERNAME = "";
				PASSWORD = "";
				return false;
			} else {
				// Log.d("EMAIL",json.getString("email") );
				// Log.d("CODE",response.getStatusLine().toString());
				isLogged = true;
				return true;
			}
		} catch (JSONException e) {
			System.out.println("LOGIN: Catch JSONException");
		} catch (IllegalThoundsObjectException e) {
			System.out.println("LOGIN: Catch IllegalThoundsObjectException");
		}
		return false;
	}

	/**
	 * Thounds logout method
	 */
	public static void logout() {
		USERNAME = "";
		PASSWORD = "";
		httpclient.getConnectionManager().shutdown();
		isLogged = false;
	}

	/**
	 * Return {@code true} if authentication credentials are set.
	 * 
	 * @return {@code true} if authentication credentials are set, {@code false}
	 *         otherwise
	 */
	public static boolean isLogged() {
		return isLogged;
	}

	/**
	 * Method for retrieve the current user's informations.
	 * 
	 * @return A UserWrapper object that represents the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static UserWrapper loadUserProfile()
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, true);
		return new UserWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method for retrieve a generic user's informations according to the user
	 * code given as parameter. The user to retrieve must be a friend of the
	 * current user.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return A {@link UserWrapper} object that represent the user.{@code null}
	 *         if the code is about a not friend user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static UserWrapper loadGenericUserProfile(int userId)
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = executeHttpRequest(httpget, true);
		return new UserWrapper(httpResponseToJSONObject(response));

	}

	/**
	 * Method for retrieve the current user's library
	 * 
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the current user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundsCollectionWrapper loadUserLibrary()
			throws IllegalThoundsObjectException, ThoundsConnectionException {
		return loadUserLibrary(1, 10);
	}

	/**
	 * Method for retrieve the current user's library
	 * 
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds per page
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundsCollectionWrapper loadUserLibrary(int page, int perPage)
			throws IllegalThoundsObjectException, ThoundsConnectionException {

		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ LIBRARY_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response;
		try {
			response = executeHttpRequest(httpget, true);
			return new ThoundsCollectionWrapper(httpResponseToJSONObject(
					response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}

	/**
	 * Method for retrieve a generic user's library according to the user code
	 * given as parameter. The library to retrieve must be a library of the
	 * friend of the current user.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId)
			throws IllegalThoundsObjectException, ThoundsConnectionException {
		return loadGenericUserLibrary(userId, 1, 10);
	}

	/**
	 * Method for retrieve a generic user's library according to the user code
	 * given as parameter. The library to retrieve must be a library of the
	 * friend of the current user.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds per page
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId,
			int page, int perPage) throws IllegalThoundsObjectException,
			ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH
				+ LIBRARY_PATH + "/" + Integer.toString(userId));
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		try {
			HttpResponse response = executeHttpRequest(httpget, true);
			return new ThoundsCollectionWrapper(httpResponseToJSONObject(
					response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}

	/**
	 * 
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static BandWrapper loadUserBand() throws ThoundsConnectionException,
			IllegalThoundsObjectException {
		return loadUserBand(1, 10);
	}

	/**
	 * 
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds per page
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static BandWrapper loadUserBand(int page, int perPage)
			throws ThoundsConnectionException, IllegalThoundsObjectException {

		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, true);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static BandWrapper loadGenericUserBand(int userId)
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		return loadGenericUserBand(userId, 1, 10);
	}

	/**
	 * 
	 * @param userId
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds per page
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static BandWrapper loadGenericUserBand(int userId, int page,
			int perPage) throws ThoundsConnectionException,
			IllegalThoundsObjectException {

		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, true);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * 
	 * @param name
	 * @param mail
	 * @param country
	 * @param city
	 * @param tags
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static boolean registrateUser(String name, String mail,
			String country, String city, String tags)
			throws ThoundsConnectionException {

		JSONObject userJSON = new JSONObject();
		JSONObject userFieldJSON = new JSONObject();
		try {
			userFieldJSON.put("name", name);
			userFieldJSON.put("email", mail);
			userFieldJSON.put("country", country);
			userFieldJSON.put("city", city);
			userFieldJSON.put("tags", tags);
			userJSON.put("user", userFieldJSON);
		} catch (JSONException e) {
			throw new RuntimeException("user JSONObject creation error");
		}
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH);
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-type", "application/json");
		StringEntity se = null;
		try {
			se = new StringEntity(userJSON.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = executeHttpRequest(httppost, false);
		return (response.getStatusLine().getStatusCode() == 201);
	}

	/**
	 * 
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static HomeWrapper loadHome() throws ThoundsConnectionException,
			IllegalThoundsObjectException {
		return loadHome(1, 10);
	}

	/**
	 * 
	 * @param page
	 * @param perPage
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static HomeWrapper loadHome(int page, int perPage)
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + HOME_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = executeHttpRequest(httpget, true);
		return new HomeWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static boolean friendshipRequest(int userId)
			throws ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + FRIENDSHIPS_PATH);
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httppost, true);
		return (response.getStatusLine().getStatusCode() == 201);
	}

	/**
	 * 
	 * @param friendshipId
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static void acceptFriendship(int friendshipId)
			throws ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(friendshipId)
				+ "?accept=true");
		HttpPut httpput = new HttpPut(uriBuilder.toString());
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		@SuppressWarnings("unused")
		HttpResponse response = executeHttpRequest(httpput, true);
	}

	/**
	 * 
	 * @param friendshipId
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static void refuseFriendship(int friendshipId)
			throws ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(friendshipId));
		HttpPut httpput = new HttpPut(uriBuilder.toString());
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		@SuppressWarnings("unused")
		HttpResponse response = executeHttpRequest(httpput, true);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static boolean removeUserFromBand(int userId)
			throws ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(userId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = executeHttpRequest(httpdelete, true);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * 
	 * @param thoundId
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundWrapper loadThounds(int thoundId)
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, isLogged);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * 
	 * @param thoundHash
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static ThoundWrapper loadThounds(String thoundHash)
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ thoundHash);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, isLogged);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * 
	 * @param thoundId
	 * @return
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 */
	public static boolean removeThound(int thoundId)
			throws ThoundsConnectionException {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = executeHttpRequest(httpdelete, true);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * Method for retrieve the user's notifications
	 * 
	 * @return {@link NotificationsWrapper} object that contains the
	 *         user notifications
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 */
	public static NotificationsWrapper getNotifications()
			throws ThoundsConnectionException, IllegalThoundsObjectException {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH + NOTIFICATIONS_PATH);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = executeHttpRequest(httpget, true);
		return new NotificationsWrapper(httpResponseToJSONObject(response));
	}
}
