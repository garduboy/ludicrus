package com.ludicrus.ludicrus.classes;

import com.ludicrus.core.classes.SportsTeam;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidSportsTeam extends SportsTeam
{
	public AndroidSportsTeam(JSONObject json) throws JSONException
	{
		this.countryName = json.getString("countryName");
		this.description = json.getString("description");
		this.homePageURL = json.getString("homePageURL");
		this.idTeam = json.getInt("idTeam");
		this.logo = json.getString("logo");
		this.name = json.getString("name");
		this.nickname = json.getString("nickname");
		this.sportName = json.getString("sportName");
		this.stadiumName = json.getString("stadiumName");
		this.wikiLink = json.getString("wikiLink");
	}
}
