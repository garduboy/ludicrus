package com.ludicrus.ludicrus.classes;

import android.content.SharedPreferences;

import com.ludicrus.core.classes.SoccerMatch;
import com.ludicrus.core.util.EnumSportItemType;
import com.ludicrus.ludicrus.SportifiedApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class AndroidSoccerMatch extends SoccerMatch
{
	private static final long serialVersionUID = 1L;

	public AndroidSoccerMatch(JSONObject json) throws JSONException
	{
		this.tournamentName = (json.has("tournamentName")? json.getString("tournamentName"):"");
		this.homeTeamName = (json.has("homeTeamName")? json.getString("homeTeamName"):"");
        this.homeTeamId = (json.has("homeTeamId")? Integer.parseInt(json.getString("homeTeamId")):0);
		this.awayTeamName = (json.has("awayTeamName")? json.getString("awayTeamName"):"");
        this.awayTeamId = (json.has("awayTeamId")? Integer.parseInt(json.getString("awayTeamId")):0);
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

    public boolean isHomeTeamFavorite(SharedPreferences preferences)
    {
        boolean isFavTeam = false;
        try {
            SharedPreferences settings = preferences;
            Set<String> favTeams = settings.getStringSet(SportifiedApp.PREFS_FAVORITE_TEAMS, new HashSet<String>());

            if (favTeams.contains(this.homeTeamId.toString())) {
                isFavTeam = true;
            }
            return isFavTeam;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean isAwayTeamFavorite(SharedPreferences preferences)
    {
        boolean isFavTeam = false;
        try {
            SharedPreferences settings = preferences;
            Set<String> favTeams = settings.getStringSet(SportifiedApp.PREFS_FAVORITE_TEAMS, new HashSet<String>());

            if (favTeams.contains(this.awayTeamId.toString())) {
                isFavTeam = true;
            }
            return isFavTeam;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String getLogoRequest()
    {
        String request = "";
        boolean requestEmpty = true;
        if(getHomeTeamLogo() == null || getHomeTeamLogo().equals("null") || getHomeTeamLogo().equals(""))
        {
            request += getHomeTeamId() + ";" + EnumSportItemType.TEAM;
            requestEmpty = false;
        }
        if(getAwayTeamLogo() == null || getAwayTeamLogo().equals("null") || getAwayTeamLogo().equals(""))
        {
            if(!requestEmpty)
            {
                request += ":";
            }
            request += getAwayTeamId() + ";" + EnumSportItemType.TEAM;
        }
        return request;
    }

    @Override
    public String getHomeTeamLogo()
    {
        String logo = super.getHomeTeamLogo();
        if(logo == null || logo.equals("null") || logo.equals("")) {
            //Check cache
            String dbLogo = SportifiedApp.getOrganizationLogo(getHomeTeamId());
            if (!dbLogo.equals("")) {
                setHomeTeamLogo(dbLogo);
                logo = dbLogo;
            }
        }
        return logo;
    }

    @Override
    public String getAwayTeamLogo()
    {
        String logo = super.getAwayTeamLogo();
        if(logo == null || logo.equals("null") || logo.equals("")) {
            //Check cache
            String dbLogo = SportifiedApp.getOrganizationLogo(getAwayTeamId());
            if (!dbLogo.equals("")) {
                setAwayTeamLogo(dbLogo);
                logo = dbLogo;
            }
        }
        return logo;
    }
}
