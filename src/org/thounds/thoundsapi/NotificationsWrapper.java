package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *
 */
public class NotificationsWrapper {
	JSONObject notification;
	JSONArray userList;
	JSONArray bannedThoundsList;
	JSONArray newThoundsList;

	private static String[] fieldList = { "band_requests", "banned_thounds",
			"new_thounds" };

	/**
	 * 
	 * @param notification
	 * @throws IllegalThoundsObjectException
	 */
	public NotificationsWrapper(JSONObject notification)
			throws IllegalThoundsObjectException {
		this.notification = notification;
		for (int i = 0; i < fieldList.length; i++)
			if (!notification.has(fieldList[i]))
				throw new IllegalThoundsObjectException();
		userList = notification.optJSONArray("band_requests");
		bannedThoundsList = notification.optJSONArray("banned_thounds");
		newThoundsList = notification.optJSONArray("new_thounds");
	}

	/**
	 * 
	 * @return
	 */
	public int getBannedThoundsListLength() {
		if (bannedThoundsList != null) {
			return bannedThoundsList.length();
		}
		return 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper getBannedThounds(int index)
			throws IllegalThoundsObjectException {
		JSONObject thound;
		try {
			thound = bannedThoundsList.getJSONObject(index).getJSONObject(
					"thound");
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
		if (thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper[] getBannedThoundsList()
			throws IllegalThoundsObjectException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getBannedThoundsListLength()];
		for (int i = 0; i < getBannedThoundsListLength(); i++)
			thoundsList[i] = getBannedThounds(i);
		return thoundsList;
	}

	/**
	 * 
	 * @return
	 */
	public int getNewThoundsListLength() {
		if (newThoundsList != null) {
			return newThoundsList.length();
		}
		return 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper getNewThounds(int index)
			throws IllegalThoundsObjectException {
		JSONObject thound;
		try {
			thound = newThoundsList.getJSONObject(index)
					.getJSONObject("thound");
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
		if (thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper[] getNewThoundsList()
			throws IllegalThoundsObjectException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getNewThoundsListLength()];
		for (int i = 0; i < getNewThoundsListLength(); i++)
			thoundsList[i] = getNewThounds(i);
		return thoundsList;
	}

	/**
	 * 
	 * @return
	 */
	public int getBandRequestListLength() {
		if (userList != null) {
			return userList.length();
		}
		return 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper getBandRequest(int index)
			throws IllegalThoundsObjectException {
		JSONObject thound;
		try {
			thound = userList.getJSONObject(index).getJSONObject("user");
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
		if (thound != null)
			return new UserWrapper(thound);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper[] getBandRequestList()
			throws IllegalThoundsObjectException {
		UserWrapper[] bandRequestList = new UserWrapper[getBandRequestListLength()];
		for (int i = 0; i < getBandRequestListLength(); i++)
			bandRequestList[i] = getBandRequest(i);
		return bandRequestList;
	}
}
