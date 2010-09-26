package org.thounds.thoundsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.thounds.thoundsapi.connector.ThoundsConnector;
import org.thounds.thoundsapi.utils.Base64Encoder;

/**
 * {@code Thound} class provide a set of static methods to manage the communication
 * between Java applications and Thounds API.
 */

public class Thounds {
	// DEBUG HOST
	//public static String HOST = "http://stage.thounds.com";
	public static String HOST = "http://thounds.com";
	
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
	
	private static ThoundsConnector connector = null;
	
	private static int PAGE = 1;
	private static int PER_PAGE = 20;
	
	public static int SUCCESS = 200;
	public static int CREATED = 201;
	
	public static String PRIVATE = "private";
	public static String CONTACTS = "contacts";
	public static String PUBLIC = "public";

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
	 * Sets the connector used to communicate with Thounds API.
	 * 
	 * @param con a class implementing the {@link ThoundsConnector} interface.
	 */
	public static void setConnector(ThoundsConnector con) {
		connector = con;
	}

	/**
	 * Retrieves the connector.
	 * 
	 * @return the connector.
	 */
	public static ThoundsConnector getConnector() {
		return connector;
	}

	/**
	 * Returns {@code true} if authentication credentials are set.
	 * 
	 * @return {@code true} if authentication credentials are set, {@code false} otherwise.
	 */
	public static boolean isLogged() {
		return connector.isAuthenticated();
	}


	/**
	 * Retrieves the current user's informations. This method requires login.
	 * 
	 * @return a {@link UserWrapper} object representing the user.
	 * @throws ThoundsConnectionException
	 *             	in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *             	in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
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
	 * Retrieves a generic user's informations according to the user
	 * code given as parameter. This method requires login.
	 * 
	 * @param userId
	 *				requested user identification code.
	 * @return a {@link UserWrapper} object representing the user.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
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
	 * Retrieves the current user's library. This method requires login.
	 * 
	 * A library represents a collection of {@link ThoundsWrapper} belonging
	 * to the current user.
	 * 
	 * @return a {@link ThoundsCollectionWrapper} object representing the
	 *				library of the current user.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static ThoundsCollectionWrapper loadUserLibrary()
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		return loadUserLibrary(PAGE, PER_PAGE);
	}

	/**
	 * Retrieves the current user's library. This method requires login.
	 * 
	 * A library represents a collection of {@link ThoundsWrapper} belonging
	 * to the current user.
	 * 
	 * @param page
	 *				page number.
	 * @param perPage
	 *				number of thounds per page.
	 * @return a {@link ThoundsCollectionWrapper} object representing the
	 *				library of the current user.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
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
	 * Retrieves a generic user's library according to the given user code.
	 * This method requires login.
	 * 
	 * A library represents a collection of {@link ThoundsWrapper} belonging
	 * to the selected user.
	 * 
	 * @param userId
	 *				user identification code. 
	 * @return a {@link ThoundsCollectionWrapper} object representing the
	 *				library of the current user.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId)
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		return loadGenericUserLibrary(userId, PAGE, PER_PAGE);
	}

	/**
	 * Retrieves a generic user's library according to the given user code.
	 * This method requires login.
	 * 
	 * A library represents a collection of {@link ThoundsWrapper} belonging
	 * to the selected user.
	 * 
	 * @param userId
	 *				user identification code.
	 * @param page
	 *				page number.
	 * @param perPage
	 *				number of thounds per page.
	 * @return a {@link ThoundsCollectionWrapper} object representing the
	 *				library of the current user.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static ThoundsCollectionWrapper loadGenericUserLibrary(int userId,
			int page, int perPage)
	throws IllegalThoundsObjectException, ThoundsConnectionException, ThoundsNotAuthenticatedexception {
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
	 * Retrieves current user friends list (band). This method requires login.
	 * 
	 * @return a {@link BandWrapper} object representing current user band.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static BandWrapper loadUserBand()
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadUserBand(PAGE, PER_PAGE);
	}

	/**
	 * Retrieves current user friends list (band). This method requires login.
	 * 
	 * @param page
	 *				page number.
	 * @param perPage
	 *				number of friends to load at time.
	 * @return a {@link BandWrapper} object representing current user band.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static BandWrapper loadUserBand(int page, int perPage)
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH + BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Retrieves selected user friends list (band). This method requires login.
	 * 
	 * @param userId
	 *            user identification code.
	 * @return a {@link BandWrapper} object representing selected user band.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static BandWrapper loadGenericUserBand(int userId)
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadGenericUserBand(userId, PAGE, PER_PAGE);
	}

	/**
	 * Retrieves selected user friends list (band). This method requires login.
	 * 
	 * @param userId
	 *				user identification code.
	 * @param page
	 *				page number.
	 * @param perPage
	 *				friends per page.
	 * @return a {@link BandWrapper} object representing selected user band.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static BandWrapper loadGenericUserBand(int userId, int page, int perPage)
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/" + Integer.toString(userId) + BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	/**
	 * Retrieves your home thounds stream. This method requires login.
	 * 
	 * @return a {@link HomeWrapper} object representing your home stream.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static HomeWrapper loadHome()
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		return loadHome(PAGE, PER_PAGE);
	}

	/**
	 * Retrieves your home thounds stream. This method requires login.
	 * 
	 * @param page
	 *				page number.
	 * @param perPage
	 *				thounds per page.
	 * @return a {@link HomeWrapper} object representing your home stream.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
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
	 * Performs a friendship request. This method requires login.
	 * 
	 * @param userId
	 *				user identification code.
	 * @return {@code true} if friendship request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static boolean friendshipRequest(int userId)
	throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/" + Integer.toString(userId) + FRIENDSHIPS_PATH);
		HttpPost httppost = new HttpPost(uriBuilder.toString());
		httppost.addHeader("Accept", "application/json");
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == CREATED);
	}

	/**
	 * Accepts a friendship request. This method requires login.
	 * 
	 * @param friendshipId
	 *				friendship request identification code.
	 * @return {@code true} if request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
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
		return (response.getStatusLine().getStatusCode() == SUCCESS);
	}

	/**
	 * Refuses a friendship request. This method requires login.
	 * 
	 * @param friendshipId
	 *            	friendship request identification code.
	 * @return {@code true} if request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static boolean refuseFriendship(int friendshipId)
	throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(friendshipId));
		HttpPut httpput = new HttpPut(uriBuilder.toString());
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpput);
		return (response.getStatusLine().getStatusCode() == SUCCESS);
	}

	/**
	 * Removes a friend from the current user band. This method requires login.
	 * 
	 * @param userId
	 *            	user identification code.
	 * @return {@code true} if remove request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static boolean removeUserFromBand(int userId)
	throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH
				+ FRIENDSHIPS_PATH + "/" + Integer.toString(userId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == SUCCESS);
	}

	/**
	 * Retrieves informations about a thound. This method requires login only if
	 * requesting {@code private} (must be thound owner) or {@code contacts} (must
	 * be friend with thound owner) thounds.
	 * 
	 * @param thoundId
	 *				thound hash code ({@code String}) or thound identifier ({@code int}).
	 * @return a {@link ThoundWrapper} object containing informations about selected thound.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated. 
	 */
	public static ThoundWrapper loadThounds(Object thoundId)
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception {
		if (thoundId instanceof String)
			thoundId = (String)thoundId;
		else
			thoundId = Integer.toString((Integer)thoundId);
		
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/" + thoundId);
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
	 * Removes a thound. This method requires login.
	 * 
	 * @param thoundId
	 * 				thound identifier.
	 * @return {@code true} if remove request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static boolean removeThound(int thoundId)
	throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == SUCCESS);
	}

	/**
	 * Retrieves user's notifications. This method requires login.
	 * 
	 * @return a {@link NotificationsWrapper} object containing user notifications.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
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
	 * Performs user registration to Thounds.
	 * 
	 * @param name
	 *            	user full name.
	 * @param mail
	 *            	user email (used to login).
	 * @param country
	 *            	user country.
	 * @param city
	 *            	user city.
	 * @param tags
	 *            	user tags (instruments, genres, etc.).
	 * @return {@code true} if user registration ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static boolean registrateUser(String name, String mail, String country, String city, String tags)
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
			throw new RuntimeException("JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == CREATED);
	}

	/**
	 * Creates a new thound.
	 * 
	 * @param title
	 * 				thound title.
	 * @param tags
	 * 				list of tags associated to the thound (instrument, genre, etc.).
	 * @param delay
	 * 				audio file start delay in milliseconds (used to sync tracks).
	 * @param offset
	 * 				audio file start offset in milliseconds (used to trim tracks).
	 * @param duration
	 * 				audio file duration in milliseconds (used to trim tracks).
	 * @param privacy
	 * 				privacy level (one of private, contacts or public).
	 * @param bpm
	 * 				beats per minute.
	 * @param lat
	 * 				latitude in degrees.
	 * @param lng
	 * 				longitude in degrees.
	 * @param thoundPath
	 * 				thound's file path.
	 * @param format
	 * 				thound's file format (example: {@code "3gp"}).
	 * @param coverPath
	 * 				thound's cover file path. 
	 * @return {@code true} if request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static boolean createThound(String title, String tags, int delay,
			int offset, int duration, String privacy, Integer bpm, Double lat,
			Double lng, String thoundPath, String format, String coverPath)
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
			trackFieldJSON.put("format", format);
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
			throw new RuntimeException("JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == CREATED);
	}


	/**
	 * Adds a new track.
	 * 
	 * @param thound_id
	 * 				thound identifier. The new track (line) will be added on it.
	 * @param title
	 *				thound title.
	 * @param tags
	 *				list of tags associated to the thound (instrument, genre, etc.).
	 * @param delay
	 *				audio file start delay in milliseconds (used to sync tracks).
	 * @param offset
	 * 				audio file start offset in milliseconds (used to trim tracks).
	 * @param duration
	 * 				audio file duration in milliseconds (used to trim tracks).
	 * @param privacy
	 * 				privacy level ({@code private}, {@code contacts} or {@code public}).
	 * @param bpm
	 * 				beats per minute.
	 * @param lat
	 * 				latitude in degrees. Can be {@code null}. 
	 * @param lng
	 * 				longitude in degrees. Can be {@code null}.
	 * @param thoundPath
	 * 				thound's file path.
	 * @param coverPath
	 * 				thound's cover file path. Can be {@code null}.
	 * @return {@code true} if request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
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
			throw new RuntimeException("JSONObject to StringEntity conversion error");
		}
		httppost.setEntity(se);
		HttpResponse response = connector.executeAuthenticatedHttpRequest(httppost);
		return (response.getStatusLine().getStatusCode() == CREATED);
	}

	/**
	 * Removes a track notification.
	 * 
	 * @param thoundId
	 * 				thound identifier.
	 * @return {@code true} if request ends successfully, {@code false} otherwise.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static boolean removeTrackNotification(int thoundId)
	throws ThoundsConnectionException, ThoundsNotAuthenticatedexception {
		StringBuilder uriBuilder = new StringBuilder(HOST
				+ TRACK_NOTIFICATIONS_PATH + "/" + Integer.toString(thoundId));
		HttpDelete httpdelete = new HttpDelete(uriBuilder.toString());
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = connector.executeAuthenticatedHttpRequest(httpdelete);
		return (response.getStatusLine().getStatusCode() == SUCCESS);
	}
	
	/**
	 * Searches for users.
	 * 
	 * Using this method you could search for thounds users by name, country or tags.
	 * 
	 * @param query
	 *				search query.
	 * @param page
	 * 				page number.
	 * @param perPage
	 * 				users per page.
	 * @return a {@link UsersCollectionWrapper} containing search results.
	 * @throws ThoundsConnectionException
	 *				in case the connection was aborted.
	 * @throws IllegalThoundsObjectException
	 *				in case the retrieved object is broken.
	 * @throws ThoundsNotAuthenticatedexception
	 * 				in case the request was not authenticated.
	 */
	public static UsersCollectionWrapper search(String[] query, int page, int perPage)
	throws ThoundsConnectionException, IllegalThoundsObjectException, ThoundsNotAuthenticatedexception{
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
			return new UsersCollectionWrapper(httpResponseToJSONObject(response).getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
	}
}
