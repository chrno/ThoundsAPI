package org.thounds.thoundsapi;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeWrapper {
	private JSONObject home;

	/**
	 * 
	 * @param home
	 */
	public HomeWrapper(JSONObject home) {
		this.home = home;
	}

	/**
	 * 
	 * @return
	 */
	public ThoundsCollectionWrapper getThoundsCollection() {
		try {
			return new ThoundsCollectionWrapper(home
					.getJSONObject("thounds-collection"));
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public UserWrapper getUser() {
		try {
			return new UserWrapper(home.getJSONObject("user"));
		} catch (JSONException e) {
			return null;
		}
	}

}
