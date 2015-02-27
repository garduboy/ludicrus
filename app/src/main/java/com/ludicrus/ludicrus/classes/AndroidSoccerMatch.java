package com.ludicrus.ludicrus.classes;

import com.ludicrus.core.classes.SoccerMatch;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidSoccerMatch extends SoccerMatch
{
	private static final long serialVersionUID = 1L;

	public AndroidSoccerMatch(JSONObject json) throws JSONException
	{
		this.tournamentName = (json.has("tournamentName")? json.getString("tournamentName"):"");
		this.homeTeamName = (json.has("homeTeamName")? json.getString("homeTeamName"):"");
		this.awayTeamName = (json.has("awayTeamName")? json.getString("awayTeamName"):"");
		this.homeTeamLogo = (json.has("homeTeamLogo")? json.getString("homeTeamLogo"):"");
		this.awayTeamLogo = (json.has("awayTeamLogo")? json.getString("awayTeamLogo"):"");
		this.homeScore = (json.has("homeScore") && !json.isNull("homeScore")? json.getInt("homeScore"):0);
		this.awayScore = (json.has("awayScore") && !json.isNull("awayScore")? json.getInt("awayScore"):0);
		this.idMatch = (json.has("idMatch")? json.getInt("idMatch"):0);
		this.time = (json.has("time")? json.getString("time"):"");
		this.startTime = (json.has("startTime")? json.getString("startTime"):"");
		this.date = (json.has("date")? json.getString("date"):"");
		this.location = (json.has("location")? json.getString("location"):"");
		this.stadiumName = (json.has("stadiumName")? json.getString("stadiumName"):"");
		this.type = (json.has("type")? json.getInt("type"):0);
	}
}
