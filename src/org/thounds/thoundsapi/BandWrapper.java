package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 */
public class BandWrapper{
	private JSONObject band;
	private JSONArray friendList;

	/**
	 * 
	 * @param band
	 */
	public BandWrapper(JSONObject band){
		this.band = band;
		friendList = band.optJSONObject("friends-collection").optJSONArray("friends");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCurrentPage(){
		return band.optInt("page");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPageTotalNumber(){
			return band.optInt("pages");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFriendTotalNumber(){
		return band.optInt("total");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFriendListLength(){
		return friendList.length();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper getFriend(int index) throws IllegalThoundsObjectException{
		JSONObject friend;
		friend = friendList.optJSONObject(index);

		if (friend != null)
			return new UserWrapper(friend);
		return null;
	}
	
	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper[] getThoundsList() throws IllegalThoundsObjectException {
		UserWrapper[] userList = new UserWrapper[getFriendListLength()];
		for (int i = 0; i < getFriendListLength(); i++)
			userList[i] = getFriend(i);
		return userList;
	}
}
