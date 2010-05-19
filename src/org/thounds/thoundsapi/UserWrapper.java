package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserWrapper{

	private JSONObject profile;
	private JSONArray thoundsList;

	public UserWrapper(JSONObject profile) {
		this.profile = profile;
	}

	public String getName() {
		try {
			return profile.getString("name");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getSiteUrl() {
		try {
			return profile.getString("site_url");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getMail() {
		try {
			return profile.getString("email");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getCity() {
		try {
			return profile.getString("city");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getCountry() {
		try {
			return profile.getString("country");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getAbout() {
		try {
			return profile.getString("about");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getAvatarUrl() {
		try {
			return profile.getString("avatar");
		} catch (JSONException e) {
			return null;
		}
	}

	public String[] getTagList() {
		JSONArray tags;
		try {
			tags = profile.getJSONArray("tags");
		} catch (JSONException e) {
			return null;
		}
		if (tags.length() > 0) {
			String tagList[] = new String[tags.length()];
			for (int i = 0; i < tags.length(); i++) {
				try {
					tagList[i] = tags.getJSONObject(i).getString("name");
				} catch (JSONException e) {
					tagList[i] = null;
				}
			}
			return tagList;
		}
		return null;
	}

	public ThoundWrapper getDefaultThound() {
		JSONObject thound;
		try {
			thound = profile.getJSONObject("default_thound");
			if (thound != null)
				return new ThoundWrapper(thound);
		} catch (JSONException e) {
			return null;
		}
		return null;
	}
	
	public int getThoundsListLength() throws JSONException {
		if (thoundsList != null) {
			return thoundsList.length();
		}
		return 0;
	}

	public ThoundWrapper getThounds(int index) throws JSONException {
		return new ThoundWrapper(thoundsList.getJSONObject(index));
	}

	public ThoundWrapper[] getThoundsList() throws JSONException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getThoundsListLength()];
		for (int i = 0; i < getThoundsListLength(); i++)
			thoundsList[i] = getThounds(i);
		return thoundsList;
	}
}
