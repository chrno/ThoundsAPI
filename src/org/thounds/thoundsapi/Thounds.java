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
import org.thounds.thoundsapi.connector.ThoundsConnector;
import org.thounds.thoundsapi.utils.Base64Encoder;

/**
 * This class provide a list of static methods to manage the communication
 * between a Java application and Thounds
 */

public class Thounds {
	private static String HOST = "http://thounds.com";
	private static String PROFILE_PATH = "/profile";
	private static String HOME_PATH = "/home";
	private static String USERS_PATH = "/users";
	private static String BAND_PATH = "/band";
	private static String FRIENDSHIPS_PATH = "/friendships";
	private static String THOUNDS_PATH = "/thounds";
	private static String TRACK_PATH = "/tracks";
	private static String LIBRARY_PATH = "/library";
	private static String NOTIFICATIONS_PATH = "/notifications";
	private static String TRACK_NOTIFICATIONS_PATH = "/tracks_notifications";
	public static String PRIVATE = "private";
	public static String CONTACTS = "contacts";
	public static String PUBLIC = "public";
	private static ThoundsConnector connector = null;

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
	 * Method for set the connector to use to comunicate with Thounds
	 * @param con a class that implement the {@code ThoundsConnector} interface
	 */
	public static void setConnector(ThoundsConnector con) {
		connector = con;
	}

	/**
	 * Method to retrieve the connector
	 * @return the connector
	 */
	public static ThoundsConnector getConnector() {
		return connector;
	}

	/**
	 * Return {@code true} if authentication credentials are set.
	 * 
	 * @return {@code true} if authentication credentials are set, {@code false}
	 *         otherwise
	 */
	public static boolean isLogged() {
		return connector.isAuthenticated();
	}


	/**
	 * Method for retrieve the current user's informations. Require login.
	 * 
	 * @return A UserWrapper object that represents the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static UserWrapper loadUserProfile()
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new UserWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method for retrieve a generic user's informations according to the user
	 * code given as parameter. The user to retrieve must be a friend of the
	 * current user. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return A {@link UserWrapper} object that represent the user.{@code null}
	 *         if the code is about a not friend user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static UserWrapper loadGenericUserProfile(int userId)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new UserWrapper(httpResponseToJSONObject(response));

	}

	/**
	 * Method for retrieve the current user's library. Require login.
	 * 
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the current user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundsCollectionWrapper loadUserLibrary()
			throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		return loadUserLibrary(1, 10);
	}

	/**
	 * Method for retrieve the current user's library. Require login.
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
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundsCollectionWrapper loadUserLibrary(int page, int perPage)
			throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {

		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ LIBRARY_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response;
		try {
			response = connector.executeAuthenticatedHttpRequest(httpget);
			return new ThoundsCollectionWrapper(httpResponseToJSONObject(
					response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}

	/**
	 * Method for retrieve a generic user's library according to the user code
	 * given as parameter. The library to retrieve must be a library of the
	 * friend of the current user. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId)
			throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		return loadGenericUserLibrary(userId, 1, 10);
	}

	/**
	 * Method for retrieve a generic user's library according to the user code
	 * given as parameter. The library to retrieve must be a library of the
	 * friend of the current user. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds to load at time
	 * @return A {@link ThoundsCollectionWrapper} object that represent the
	 *         library of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId,
			int page, int perPage) throws IllegalThoundsObjectException,
			ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + LIBRARY_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		try {
			HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
			return new ThoundsCollectionWrapper(httpResponseToJSONObject(
					response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}

	/**
	 * Method for retrieve the friends list (band) of the current user. Require
	 * login.
	 * 
	 * @return A {@link BandWrapper} object that represent the band of the
	 *         current user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static BandWrapper loadUserBand() throws ThoundsConnectionException,
			IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadUserBand(1, 10);
	}

	/**
	 * Method for retrieve the friends list (band) of the current user. Require
	 * login.
	 * 
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of friends to load at time
	 * @return A {@link BandWrapper} object that represent the band of the
	 *         current user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static BandWrapper loadUserBand(int page, int perPage)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {

		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method for retrieve the friends list (band) of the user. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return A {@link BandWrapper} object that represent the band of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static BandWrapper loadGenericUserBand(int userId)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadGenericUserBand(userId, 1, 10);
	}

	/**
	 * Method for retrieve the friends list (band) of the user. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of friends to load at time
	 * @return A {@link BandWrapper} object that represent the band of the user
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static BandWrapper loadGenericUserBand(int userId, int page,
			int perPage) throws ThoundsConnectionException,
			IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {

		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method for retrieve the Thounds home informations. Require login.
	 * 
	 * @return A {@link HomeWrapper} object that contain the informations about
	 *         Thounds home
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static HomeWrapper loadHome() throws ThoundsConnectionException,
			IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadHome(1, 10);
	}

	/**
	 * Method for retrieve the Thounds home informations. Require login.
	 * 
	 * @param page
	 *            page number
	 * @param perPage
	 *            number of thounds to load at time
	 * @return A {@link HomeWrapper} object that contain the informations about
	 *         Thounds home
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static HomeWrapper loadHome(int page, int perPage)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + HOME_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new HomeWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method to perform a friendship request. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return {@code true} if friendship request ends successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean friendshipRequest(int userId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + FRIENDSHIPS_PATH);
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == 201);
	}

	/**
	 * Method to accept a friendship request. Require login.
	 * 
	 * @param friendshipId
	 *            Identification code of the friendship request
	 * @return {@code true} if friendship request ends successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean acceptFriendship(int friendshipId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(friendshipId)
				+ "?accept=true");
		HttpPut httpput = new HttpPut(uriBuilder.toString());
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpput);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * Method to refuse a friendship request. Require login.
	 * 
	 * @param friendshipId
	 *            Identification code of the friendship request
	 * @return {@code true} if friendship request ends successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean refuseFriendship(int friendshipId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(friendshipId));
		HttpPut httpput = new HttpPut(uriBuilder.toString());
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpput);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * Method to remove a friend from the current user band. Require login.
	 * 
	 * @param userId
	 *            Identification code of the user
	 * @return {@code true} if remove request ends successfully, {@code false}
	 *         otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean removeUserFromBand(int userId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(userId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * Method for retrieve informations about a thound. Requires login only if
	 * requesting private (must be thound owner) or contacts (must be friend of
	 * thound owner) thounds.
	 * 
	 * @param thoundId
	 *            Identification code of the thound
	 * @return A {@link ThoundWrapper} object that contain the informations
	 *         about the selected thound
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundWrapper loadThounds(int thoundId)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId));
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response;
		if (connector.isAuthenticated())
			response = connector.executeAuthenticatedHttpRequest(httpget);
		else
			response = connector.executeHttpRequest(httpget);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method for retrieve informations about a thound. Requires login only if
	 * requesting private (must be thound owner) or contacts (must be friend of
	 * thound owner) thounds.
	 * 
	 * @param thoundHash
	 *            Hash code of the thound
	 * @return A {@link ThoundWrapper} object that contain the informations
	 *         about the selected thound
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static ThoundWrapper loadThounds(String thoundHash)
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ thoundHash);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response; 
		if (connector.isAuthenticated())
			response = connector.executeAuthenticatedHttpRequest(httpget);
		else
			response = connector.executeHttpRequest(httpget);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method to remove a thound. Require login.
	 * 
	 * @param thoundId
	 * @return {@code true} if remove request ends successfully, {@code false}
	 *         otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean removeThound(int thoundId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	/**
	 * Method for retrieve the user's notifications. Require login.
	 * 
	 * @return {@link NotificationsWrapper} object that contains the user
	 *         notifications
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws IllegalThoundsObjectException
	 *             in case the retrieved object is broken
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static NotificationsWrapper loadNotifications()
			throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ NOTIFICATIONS_PATH);
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new NotificationsWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Method to perform registration to Thounds.
	 * 
	 * @param name
	 *            user full name
	 * @param mail
	 *            user email (used to login)
	 * @param country
	 *            user country
	 * @param city
	 *            user city
	 * @param tags
	 *            tags associated (instruments, genres, etc.)
	 * @return {@code true} if user registration ends successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 *             in case the connection was aborted
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean registrateUser(String name, String mail,
			String country, String city, String tags)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {

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
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == 201);
	}

	/**
	 * Method to create a new thound.
	 * 
	 * @param title Thound's title
	 * @param tags list of tags associated to the thound (instrument, genre, etc.)
	 * @param delay audio file start delay in milliseconds (used to sync tracks)
	 * @param offset audio file start offset in milliseconds (used to trim tracks)
	 * @param duration audio file duration in milliseconds (used to trim tracks)
	 * @param privacy privacy level (one of private, contacts or public)
	 * @param bpm beats per minute
	 * @param lat latitude in degrees
	 * @param lng longitude in degrees
	 * @param thoundPath thound's file path
	 * @param coverPath thound's cover file path 
	 * @return {@code true} if request end successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean createThound(String title, String tags, int delay,
			int offset, int duration, String privacy, Integer bpm, Double lat,
			Double lng, String thoundPath, String coverPath)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {

		JSONObject thoundJSON = new JSONObject();
		JSONObject trackFieldJSON = new JSONObject();
		JSONObject thounds_AttributeJSON = new JSONObject();
		try {

			trackFieldJSON.put("title", title);
			trackFieldJSON.put("tag_list", tags);
			trackFieldJSON.put("delay", delay);
			trackFieldJSON.put("offset", offset);
			trackFieldJSON.put("duration", duration);
			trackFieldJSON.put("privacy", privacy);
			if (bpm != null) {
				thounds_AttributeJSON.put("bpm", bpm);
				trackFieldJSON.put("thound_attributes", thounds_AttributeJSON);
			}
			if (lat != null)
				trackFieldJSON.put("lat", lat);
			if (lng != null)
				trackFieldJSON.put("lng", lng);
			String encodedThound = null;
			String encodedCover = null;
			if (thoundPath != null && !thoundPath.equals("")){
				encodedThound = Base64Encoder.Encode(thoundPath);
				System.out.println("encodedthound: " + encodedThound);
				trackFieldJSON.put("thoundfile", encodedThound);
			}
			if (coverPath != null && !coverPath.equals("")){
				encodedCover = Base64Encoder.Encode(coverPath);
			trackFieldJSON.put("coverfile", encodedCover);
			}
			thoundJSON.put("track", trackFieldJSON);
			
		} catch (JSONException e) {
			throw new RuntimeException("user JSONObject creation error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder uriBuilder = new StringBuilder(HOST + TRACK_PATH);
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-type", "application/json");
		StringEntity se = null;
		try {
			se = new StringEntity(thoundJSON.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == 201);
	}


	/**
	 * Method to add new track on an existing thound.
	 * 
	 * @param thound_id id to add a new track (a new line) on an existing thound
	 * @param title Thound's title
	 * @param tags list of tags associated to the thound (instrument, genre, etc.)
	 * @param delay audio file start delay in milliseconds (used to sync tracks)
	 * @param offset audio file start offset in milliseconds (used to trim tracks)
	 * @param duration audio file duration in milliseconds (used to trim tracks)
	 * @param privacy privacy level (one of private, contacts or public)
	 * @param bpm beats per minute.
	 * @param lat latitude in degrees. Can be null. 
	 * @param lng longitude in degrees. Can be null.
	 * @param thoundPath thound's file path
	 * @param coverPath thound's cover file path. Can be null; 
	 * @return {@code true} if request end successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean createTrack(int thound_id, String title, String tags,
			int delay, int offset, int duration, String privacy, Integer bpm,
			Double lat, Double lng, String thoundPath, String coverPath)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {

		JSONObject thoundJSON = new JSONObject();
		JSONObject trackFieldJSON = new JSONObject();
		JSONObject thounds_AttributeJSON = new JSONObject();
		try {

			trackFieldJSON.put("title", title);
			trackFieldJSON.put("tag_list", tags);
			trackFieldJSON.put("delay", delay);
			trackFieldJSON.put("offset", offset);
			trackFieldJSON.put("duration", duration);
			trackFieldJSON.put("privacy", privacy);
			if (bpm != null) {
				thounds_AttributeJSON.put("bpm", bpm);
				trackFieldJSON.put("thound_attributes", thounds_AttributeJSON);
			}
			if (lat != null)
				trackFieldJSON.put("lat", lat);
			if (lng != null)
				trackFieldJSON.put("lng", lng);
			String encodedThound = null;
			String encodedCover = null;
			if (thoundPath != null && !thoundPath.equals("")){
				encodedThound = Base64Encoder.Encode(thoundPath);
				System.out.println("encodedthound: " + encodedThound);
				trackFieldJSON.put("thoundfile", encodedThound);
			}
			if (coverPath != null && !coverPath.equals("")){
				encodedCover = Base64Encoder.Encode(coverPath);
				trackFieldJSON.put("coverfile", encodedCover);
			}
			thoundJSON.put("track", trackFieldJSON);
		} catch (JSONException e) {
			throw new RuntimeException("user JSONObject creation error");
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuilder uriBuilder = new StringBuilder(HOST + TRACK_PATH
				+ "?thound_id=" + Integer.toString(thound_id));
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-type", "application/json");
		StringEntity se = null;
		try {
			se = new StringEntity(thoundJSON.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == 201);
	}

	/**
	 * Method to remove a track notification
	 * 
	 * @param thoundId thound identifier
	 * @return {@code true} if request end successfully, {@code
	 *         false} otherwise
	 * @throws ThoundsConnectionException
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static boolean removeTrackNotification(int thoundId)
			throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST
				+ TRACK_NOTIFICATIONS_PATH + "/" + Integer.toString(thoundId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == 200);
	}
	
	/**
	 * Method to search users
	 * 
	 * @param query search query
	 * @param page page number 
	 * @param perPage number of user to load at time
	 * @return a UsersCollectionWrapper that contain the search result
	 * @throws ThoundsConnectionException
	 * @throws IllegalThoundsObjectException
	 * @throws ThoundsNotAuthenticatedexception 
	 */
	public static UsersCollectionWrapper search(String[] query, int page, int perPage) throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception{
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		if(query!=null && query.length > 0){
			uriBuilder.append("&query=" + query[0]);
			for(int i=1; i < query.length; i++)
				uriBuilder.append("+" + query[i]);
		}
		
		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response;
		try {
			response = connector.executeAuthenticatedHttpRequest(httpget);
			return new UsersCollectionWrapper(httpResponseToJSONObject(
					response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}
}
