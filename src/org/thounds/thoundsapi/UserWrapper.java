package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 */
public class UserWrapper implements ThoundsObjectInterface{

	private JSONObject profile;
	static private String[] fieldList = { "id", "name", "site_url",
			"city", "country", "about", "avatar", "tags", "default_thound", "created_at" };

	/**
	 * 
	 * @param profile
	 * @throws IllegalThoundsObjectException
	 */
	public UserWrapper(JSONObject profile) throws IllegalThoundsObjectException {
		this.profile = profile;
		/*for (int i = 0; i < fieldList.length; i++)
			if (!profile.has(fieldList[i])){
				System.out.println(fieldList[i]);
				System.out.println(i);
				throw new IllegalThoundsObjectException();
			}*/
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return profile.optInt("id");
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return profile.optString("name");
	}

	/**
	 * 
	 * @return
	 */
	public String getSiteUrl() {
		return profile.optString("site_url", "");
	}

	/**
	 * 
	 * @return
	 */
	public String getMail(){
		return profile.optString("email");
	}

	/**
	 * 
	 * @return
	 */
	public String getCity(){
		return profile.optString("city");
	}

	/**
	 * 
	 * @return
	 */
	public String getCountry(){
		return profile.optString("country");
	}

	/**
	 * 
	 * @return
	 */
	public String getAbout() {
		return profile.optString("about", "");
	}

	/**
	 * 
	 * @return
	 */
	public String getAvatarUrl() {
		return profile.optString("avatar", null);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getTagList(){
		JSONArray tags;
		tags = profile.optJSONArray("tags");
		if (tags != null && tags.length() > 0) {
			String tagList[] = new String[tags.length()];
			for (int i = 0; i < tags.length(); i++) {
				JSONObject obj = tags.optJSONObject(i);
				if(obj != null)
					tagList[i] = obj.optString("name",null);
				else
					tagList[i] = null;
			}
			return tagList;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public ThoundWrapper getDefaultThound()
			throws IllegalThoundsObjectException {
		JSONObject thound;
		thound = profile.optJSONObject("default_thound");
		if (thound != null)
			return new ThoundWrapper(thound);
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getCreatedAt(){
		return profile.optString("created_at");
	}
}
