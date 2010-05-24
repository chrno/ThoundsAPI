package org.thounds.thoundsapi;

import org.json.JSONObject;

/**
 * 
 */
public class HomeWrapper {
	private JSONObject home;
	private static String[] fieldList = { "thounds-collection", "user"};

	/**
	 * 
	 * @param home
	 * @throws IllegalThoundsObjectException 
	 */
	public HomeWrapper(JSONObject home) throws IllegalThoundsObjectException {
		this.home = home;
		for (int i = 0; i < fieldList.length; i++)
			if (!home.has(fieldList[i]))
				throw new IllegalThoundsObjectException();
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
