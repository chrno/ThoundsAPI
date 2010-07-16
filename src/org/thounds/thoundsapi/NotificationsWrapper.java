package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *
 */
public class NotificationsWrapper implements ThoundsObjectInterface{
	JSONObject notification;
	JSONArray userList;
	JSONArray bannedThoundsList;
	JSONArray newThoundsList;

	/**
	 * 
	 * @param notification
	 */
	public NotificationsWrapper(JSONObject notification){
		this.notification = notification;
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
			thound = bannedThoundsList.getJSONObject(index);
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
			thound = newThoundsList.getJSONObject(index);
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
	public NotificationPair<UserWrapper> getBandRequest(int index)
			throws IllegalThoundsObjectException {
		JSONObject thound;
		int id;
		try {
			thound = userList.getJSONObject(index).getJSONObject("user");
			id = userList.getJSONObject(index).optInt("id");
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
		if (thound != null)
			return new NotificationPair<UserWrapper>(new UserWrapper(thound), id);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	@SuppressWarnings("unchecked")
	public NotificationPair<UserWrapper>[] getBandRequestList()
			throws IllegalThoundsObjectException {
		NotificationPair<?>[] bandRequestList = new NotificationPair<?>[getBandRequestListLength()];
		for (int i = 0; i < getBandRequestListLength(); i++)
			bandRequestList[i] = getBandRequest(i);
		return (NotificationPair<UserWrapper>[])bandRequestList;
	}
}
