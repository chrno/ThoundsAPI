package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsWrapper {
	JSONObject notification;
	JSONArray userList;
	JSONArray bannedThoundsList;
	JSONArray newThoundsList;
	public NotificationsWrapper(JSONObject notification) {
		this.notification = notification;
		try {
			userList = notification.getJSONArray("band_requests");
		} catch (JSONException e) {
			userList = null;
		}
		try {
			bannedThoundsList = notification.getJSONArray("banned_thounds");
		} catch (JSONException e) {
			userList = null;
		}
		try {
			newThoundsList = notification.getJSONArray("new_thounds");
		} catch (JSONException e) {
			userList = null;
		}
	}
	
	public int getBannedThoundsListLength() throws JSONException {
		if (bannedThoundsList != null) {
			return bannedThoundsList.length();
		}
		return 0;
	}
	
	public ThoundWrapper getBannedThounds(int index){
		JSONObject thound;
		try {
			thound = bannedThoundsList.getJSONObject(index);
		} catch (JSONException e) {
			thound = null;
		}
		if(thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	public ThoundWrapper[] getBannedThoundsList() throws JSONException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getBannedThoundsListLength()];
		for (int i = 0; i < getBannedThoundsListLength(); i++)
			thoundsList[i] = getBannedThounds(i);
		return thoundsList;
	}
	
	public int getNewThoundsListLength() throws JSONException {
		if (newThoundsList != null) {
			return newThoundsList.length();
		}
		return 0;
	}
	
	public ThoundWrapper getNewThounds(int index){
		JSONObject thound;
		try {
			thound = newThoundsList.getJSONObject(index);
		} catch (JSONException e) {
			thound = null;
		}
		if(thound != null)
			return new ThoundWrapper(thound);
		return null;
	}
	

	public ThoundWrapper[] getNewThoundsList() throws JSONException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getNewThoundsListLength()];
		for (int i = 0; i < getNewThoundsListLength(); i++)
			thoundsList[i] = getNewThounds(i);
		return thoundsList;
	}

	
	public int getBandRequestListLength() throws JSONException {
		if (userList != null) {
			return userList.length();
		}
		return 0;
	}
	
	public UserWrapper getBandRequest(int index){
		JSONObject thound;
		try {
			thound = userList.getJSONObject(index);
		} catch (JSONException e) {
			thound = null;
		}
		if(thound != null)
			return new UserWrapper(thound);
		return null;
	}

	public UserWrapper[] getBandRequestList() throws JSONException {
		UserWrapper[] bandRequestList = new UserWrapper[getBandRequestListLength()];
		for (int i = 0; i < getBandRequestListLength(); i++)
			bandRequestList[i] = getBandRequest(i);
		return bandRequestList;
	}
}
