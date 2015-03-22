package com.ludicrus.ludicrus.classes;

import com.ludicrus.core.classes.SportsTeam;
import com.ludicrus.ludicrus.SportifiedApp;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidSportsTeam extends SportsTeam
{
	public AndroidSportsTeam(JSONObject json) throws JSONException
	{
		this.countryName = (json.has("countryName")? json.getString("countryName"):"");
		this.description = (json.has("description")? json.getString("description"):"");
		this.homePageURL = (json.has("homePageURL")? json.getString("homePageURL"):"");
		this.idTeam = (json.has("idTeam")? json.getInt("idTeam"):0);
        this.feedId = (json.has("feedId")? json.getInt("feedId"):0);
		this.logo = (json.has("logo")? json.getString("logo"):"");
		this.name = (json.has("name")? json.getString("name"):"");
		this.nickname = (json.has("nickname")? json.getString("nickname"):"");
		this.sportType = (json.has("sportType")? json.getInt("sportType"):0);
		this.stadiumName = (json.has("stadiumName")? json.getString("stadiumName"):"");
		this.wikiLink = (json.has("wikiLink")? json.getString("wikiLink"):"");
	}

    @Override
    public String getLogo() {
        String logo = super.getLogo();
        if (logo == null || logo.equals("null") || logo.equals("")) {
            //Check cache
            String dbLogo = SportifiedApp.getOrganizationLogo(getIdOrganization());
            if (!dbLogo.equals("")) {
                setLogo(dbLogo);
                logo = dbLogo;
            }
        }
        return logo;
    }
}
