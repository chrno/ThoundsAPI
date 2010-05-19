package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThoundsCollectionWrapper {
	JSONObject collection;
	JSONArray thoundsList;
	public ThoundsCollectionWrapper(JSONObject collection) {
		this.collection = collection;
		try {
			thoundsList = collection.getJSONArray("thounds");
		} catch (JSONException e) {
			thoundsList = null;
		}
	}
	
	public int getPageTotalNumber() {
		try {
			return collection.getInt("pages");
		} catch (JSONException e) {
			return 0;
		}
	}

	public int getThoundsTotalNumber(){
		try {
			return collection.getInt("total");
		} catch (JSONException e) {
			return 0;
		}
	}

	public int getCurrentPageNumber() {
		try {
			return collection.getInt("page");
		} catch (JSONException e) {
			return 0;
		}
	}
	
	public int getThoundsListLength() throws JSONException {
		if (thoundsList != null) {
			return thoundsList.length();
		}
		return 0;
	}
	
	public ThoundWrapper getThounds(int index){
		JSONObject thound;
		try {
			thound = thoundsList.getJSONObject(index);
		} catch (JSONException e) {
			thound = null;
		}
		if(thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	public ThoundWrapper[] getThoundsList() throws JSONException {
		ThoundWrapper[] thoundsList = new ThoundWrapper[getThoundsListLength()];
		for (int i = 0; i < getThoundsListLength(); i++)
			thoundsList[i] = getThounds(i);
		return thoundsList;
	}
}
