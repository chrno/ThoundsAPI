package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 */
public class ThoundsCollectionWrapper {
	JSONObject collection;
	JSONArray thoundsList;
	private static String[] fieldList = { "thounds", "page", "pages", "total" };

	/**
	 * 
	 * @param collection
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundsCollectionWrapper(JSONObject collection)
			throws IllegalThoundsObjectException {
		this.collection = collection;
		for (int i = 0; i < fieldList.length; i++)
			if (!collection.has(fieldList[i]))
				throw new IllegalThoundsObjectException();
		thoundsList = collection.optJSONArray("thounds");
	}

	/**
	 * 
	 * @return
	 */
	public int getPageTotalNumber() {
		return collection.optInt("pages");
	}

	/**
	 * 
	 * @return
	 */
	public int getThoundsTotalNumber() {
		return collection.optInt("total");
	}

	/**
	 * 
	 * @return
	 */
	public int getCurrentPageNumber() {
		return collection.optInt("page");
	}

	/**
	 * 
	 * @return
	 */
	public int getThoundsListLength() {
		if (thoundsList != null) {
			return thoundsList.length();
		}
		return 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper getThounds(int index)
			throws IllegalThoundsObjectException {
		JSONObject thound;
		thound = thoundsList.optJSONObject(index);
		if (thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper[] getThoundsList()
			throws IllegalThoundsObjectException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getThoundsListLength()];
		for (int i = 0; i < getThoundsListLength(); i++)
			thoundsList[i] = getThounds(i);
		return thoundsList;
	}
}
