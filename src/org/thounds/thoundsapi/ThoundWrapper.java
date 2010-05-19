package org.thounds.thoundsapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThoundWrapper{
	private JSONObject thound;
	private JSONArray tracks;

	public ThoundWrapper(JSONObject thound) {
		this.thound = thound;
		try {
			tracks = thound.getJSONArray("tracks");
		} catch (JSONException e) {
			tracks = null;
		}
	}

	public int getBmp() throws JSONException {
		return thound.getInt("bmp");
	}

	public boolean isBanned() throws JSONException {
		return thound.getBoolean("banned");
	}

	public boolean hasNewTracks() throws JSONException {
		return thound.getBoolean("has_new_tracks");
	}

	public int getId() throws JSONException {
		return thound.getInt("id");
	}

	public int getLeadTrackId() throws JSONException {
		return thound.getInt("lead_track_id");
	}

	public int getMixDuration() throws JSONException {
		return thound.getInt("mix_duration");
	}

	public String getMixUrl() throws JSONException {
		return thound.getString("mix_url");
	}

	public String getPrivacy() throws JSONException {
		return thound.getString("privacy");
	}

	public String getPublicId() throws JSONException {
		return thound.getString("public_id");
	}

	public String getPublicUrl() throws JSONException {
		return thound.getString("public_url");
	}

	public String getTags() throws JSONException {
		return thound.getString("tags");
	}

	public int getUserId() throws JSONException {
		return thound.getInt("user_id");
	}

	public String getUserAvatarUrl() throws JSONException {
		return thound.getString("user_avatar");
	}
	
	public int getTrackListLength(){
		return tracks.length();
	}
	
	public TrackWrapper getTrack(int index) throws JSONException{
		return new TrackWrapper(tracks.getJSONObject(index));
	}
	
	public TrackWrapper[] getTracksList()throws JSONException{
		TrackWrapper[] tracksList = new TrackWrapper[getTrackListLength()];
		for(int i=0; i < getTrackListLength(); i++)
			tracksList[i] = getTrack(i);
		return tracksList;
	}
}
