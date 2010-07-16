package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONObject;

public class UsersCollectionWrapper implements ThoundsObjectInterface {

	JSONObject collection;
	JSONArray usersList;

	/**
	 * 
	 * @param collection
	 */
	public UsersCollectionWrapper(JSONObject collection){
		this.collection = collection;
		usersList = collection.optJSONArray("users");
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
	public int getusersTotalNumber() {
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
	public int getUsersListLength() {
		if (usersList != null) {
			return usersList.length();
		}
		return 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper getUsers(int index)
			throws IllegalThoundsObjectException {
		JSONObject user;
		user = usersList.optJSONObject(index);
		if (user != null)
			return new UserWrapper(user);
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper[] getUsersList()
			throws IllegalThoundsObjectException {
		UserWrapper[] usersList = new UserWrapper[getUsersListLength()];
		for (int i = 0; i < getUsersListLength(); i++)
			usersList[i] = getUsers(i);
		return usersList;
	}
	
}
