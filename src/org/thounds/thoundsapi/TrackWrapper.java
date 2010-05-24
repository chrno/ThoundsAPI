package org.thounds.thoundsapi;

import org.json.JSONObject;

/**
 * 
 *
 */
public class TrackWrapper {

	private JSONObject track;
	private static String[] fieldList = { "cover", "delay", "duration", "host",
			"id", "offset", "path", "privacy", "tags", "thound_id", "title",
			"uri", "user_id", "user", "lat", "lng", "created_at" };

	/**
	 * 
	 * @param track
	 * @throws IllegalThoundsObjectException
	 */
	public TrackWrapper(JSONObject track) throws IllegalThoundsObjectException {
		this.track = track;
		for (int i = 0; i < fieldList.length; i++)
			if (!track.has(fieldList[i]))
				throw new IllegalThoundsObjectException();
	}

	/**
	 * 
	 * @return
	 */
	public String getCover() {
		return track.optString("cover");
	}

	/**
	 * 
	 * @return
	 */
	public int getDelay() {
		return track.optInt("delay");
	}

	/**
	 * 
	 * @return
	 */
	public int getDuration() {
		return track.optInt("duration");
	}

	/**
	 * 
	 * @return
	 */
	public String getHost() {
		return track.optString("host");
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return track.optInt("id");
	}

	/**
	 * 
	 * @return
	 */
	public int getOffset() {
		return track.optInt("offset");
	}

	/**
	 * 
	 * @return
	 */
	public String getPath() {
		return track.optString("path", null);
	}

	/**
	 * 
	 * @return
	 */
	public String getPrivacy() {
		return track.optString("privacy");
	}

	/**
	 * 
	 * @return
	 */
	public String getTags() {
		return track.optString("tags");
	}

	/**
	 * 
	 * @return
	 */
	public int getThoundId() {
		return track.optInt("thound_id");
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return track.optString("title");
	}

	/**
	 * 
	 * @return
	 */
	public String getUri() {
		return track.optString("uri");
	}

	/**
	 * 
	 * @return
	 */
	public int getUserId() {
		return track.optInt("user_id");
	}

	/**
	 * 
	 * @return
	 */
	public String getUserAvatarUrl() {
		JSONObject obj;
		obj = track.optJSONObject("user");
		if (obj != null)
			return obj.optString("avatar");
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserCity(){
		JSONObject obj;
		obj = track.optJSONObject("user");
		if (obj != null)
			return obj.optString("city");
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserCountry(){
		JSONObject obj;
		obj = track.optJSONObject("user");
		if (obj != null)
			return obj.optString("country");
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserName(){
		JSONObject obj;
		obj = track.optJSONObject("user");
		if (obj != null)
			return obj.optString("name");
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public double getLat(){
		return track.optDouble("lat");
	}

	/**
	 * 
	 * @return
	 */
	public double getLng() {
		return track.optDouble("lng");
	}

	/**
	 * 
	 * @return
	 */
	public String getCreatedAt(){
		return track.optString("created_at");
	}
}
