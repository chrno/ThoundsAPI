package org.thounds.thoundsapi;

import org.json.JSONObject;

/**
 * 
 */
public class HomeWrapper {
	private JSONObject home;

	/**
	 * 
	 * @param home
	 * @throws IllegalThoundsObjectException 
	 */
	public HomeWrapper(JSONObject home) {
		this.home = home;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundsCollectionWrapper getThoundsCollection()
			throws IllegalThoundsObjectException {
		JSONObject obj = home.optJSONObject("thounds-collection");
		if (obj != null)
			return new ThoundsCollectionWrapper(obj);
		else
			return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper getUser() throws IllegalThoundsObjectException {
		JSONObject obj = home.optJSONObject("user");
		if (obj != null)
			return new UserWrapper(obj);
		else
			return null;
	}

}
