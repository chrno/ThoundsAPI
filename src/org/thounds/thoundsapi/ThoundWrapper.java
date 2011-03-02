package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *
 */
public class ThoundWrapper implements ThoundsObjectInterface{
	private JSONObject thound;
	private JSONArray tracks;

	/**
	 * 
	 * @param thound
	 */
	public ThoundWrapper(JSONObject thound)
			throws IllegalThoundsObjectException {
		this.thound = thound;
		tracks = thound.optJSONArray("tracks");
	}

	/**
	 * 
	 * @return
	 */
	public int getBmp() {
		return thound.optInt("bmp");
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBanned() {
		return thound.optBoolean("banned");
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasNewTracks() {
		return thound.optBoolean("has_new_tracks");
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return thound.optInt("id");
	}

	/**
	 * 
	 * @return
	 */
	public int getLeadTrackId() {
		return thound.optInt("lead_track_id");
	}

	/**
	 * 
	 * @return
	 */
	public int getMixDuration() {
		return thound.optInt("mix_duration");
	}

	/**
	 * 
	 * @return
	 */
	public String getMixUrl() {
		return thound.optString("mix_url","");
	}

	/**
	 * 
	 * @return
	 */
	public String getPrivacy() {
		return thound.optString("privacy");
	}

	/**
	 * 
	 * @return
	 */
	public String getPublicId(){
		return thound.optString("public_id");
	}

	/**
	 * 
	 * @return
	 */
	public String getPublicUrl(){
		return thound.optString("public_url");
	}

	/**
	 * 
	 * @return
	 */
	public String getTags(){
		return thound.optString("tags");
	}

	/**
	 * 
	 * @return
	 */
	public int getUserId(){
		return thound.optInt("user_id");
	}

	/**
	 * 
	 * @return
	 */
	public String getUserAvatarUrl(){
		return thound.optString("user_avatar","");
	}

	/**
	 * 
	 * @return
	 */
	public int getTrackListLength() {
		return tracks.length();
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public TrackWrapper getTrack(int index)
			throws IllegalThoundsObjectException {
		JSONObject obj = null;
		try {
			obj = tracks.getJSONObject(index);
		} catch (JSONException e) {
			throw new IllegalThoundsObjectException();
		}
		if (obj == null)
			return null;
		return new TrackWrapper(obj);
	}

	/**
	 * 
	 * @return
	 * @throws IllegalThoundsObjectException
	 */
	public TrackWrapper[] getTracksList() throws IllegalThoundsObjectException {
		TrackWrapper[] tracksList = new TrackWrapper[getTrackListLength()];
		for (int i = 0; i < getTrackListLength(); i++)
			tracksList[i] = getTrack(i);
		return tracksList;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCreatedAt(){
		return thound.optString("created_at");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUpdatedAt(){
		return thound.optString("updated_at");
	}
}
