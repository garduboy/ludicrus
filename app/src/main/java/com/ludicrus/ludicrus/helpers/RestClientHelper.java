package com.ludicrus.ludicrus.helpers;

import java.util.ArrayList;
import java.util.Calendar;

import android.text.format.DateFormat;

import com.facebook.model.GraphLocation;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.ludicrus.ludicrus.classes.AndroidOrganization;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.core.util.EnumSportItemType;

public class RestClientHelper
{
//	final static String server = "10.0.2.2:8080";
//	final static String server = "10.22.42.106:8080";
	final static String server = "kincua.com:8080";
	
	public static final void addFavoriteTeams(Integer userId, ArrayList<Integer> addedTeams, ArrayList<Integer> removedTeams, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/addFavoriteTeams.do", 0);
		restClient.AddParam("action", "addFavoriteTeams");
		restClient.AddParam("userId", userId.toString());
		int i;
		String addedTeamIds = "";
		String removedTeamIds = "";
		for(i = 0; i < addedTeams.size(); i ++) 
		{
			addedTeamIds += addedTeams.get(i) + ":";
		}
		restClient.AddParam("addedTeams", addedTeamIds);
		for(i = 0; i < removedTeams.size(); i ++) 
		{
			removedTeamIds += removedTeams.get(i) + ":";
		}
		restClient.AddParam("removedTeams", removedTeamIds);
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void login(String userName, String password, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/mobileLogin.do", 0);
		restClient.AddParam("action", "login");
		restClient.AddParam("userName", userName);
		restClient.AddParam("password", password);
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void logGuest(EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/mobileLogin.do", 0);
		restClient.AddParam("action", "logGuest");
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void loginFBUser(GraphUser user, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/mobileLogin.do", 0);
		restClient.AddParam("action", "loginFB");
		restClient.AddParam("userName", user.getFirstName());
		restClient.AddParam("name", user.getFirstName());
		restClient.AddParam("lastName", user.getLastName());
		restClient.AddParam("email", (String)user.getProperty("email"));
		GraphPlace location = user.getLocation();
		if(location != null)
		{
			String locName = location.getName();
			GraphLocation gLoc = location.getLocation();
			if(gLoc != null)
			{
				String country = gLoc.getCountry();
				restClient.AddParam("country", country);
			}
		}
		restClient.AddParam("dob", user.getBirthday());
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getConfederationList(EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadConfederations.do", 0);
		restClient.AddParam("action", "loadConfederations");
		restClient.AddParam("orgType", EnumSportItemType.CONFEDERATION + "");
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getFederationList(String confederationID, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadFederations.do", 0);
		restClient.AddParam("action", "loadFederations");
		restClient.AddParam("orgID", confederationID);
		restClient.AddParam("orgType", EnumSportItemType.FEDERATION + "");
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getFixtures(Calendar startDate, int offsetDays, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadSoccerFixturesByDate.do", 0);
		restClient.AddParam("action", "loadSoccerFixtures");
		restClient.AddParam("startDate", DateFormat.format("yyyy-MM-dd", startDate.getTime()).toString());
		startDate.add(Calendar.DATE, offsetDays);
		restClient.AddParam("endDate", DateFormat.format("yyyy-MM-dd", startDate.getTime()).toString());
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getLiveScores(EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadSoccerScores.do", 0);
		restClient.AddParam("action", "loadSoccerLiveScores");
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getTeams(EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadSoccerTeams.do", 0);
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getTeamsByFederation(Integer userId, String federationID, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadTeamsByFederation.do", 0);
		restClient.AddParam("action", "loadTeamsByFed");
		restClient.AddParam("orgID", federationID);
		restClient.AddParam("userId", userId.toString());
		restClient.AddParam("orgType", EnumSportItemType.TEAM + "");
		restClient.setListener(listener);
		restClient.execute();
	}
	
	public static final void getUserFavTeams(Integer userId, EventListener listener)
	{
		RestClient restClient = new RestClient("http://" + server + "/ludicrus/util/loadUserFavTeams.do", 0);
		restClient.AddParam("userId", userId.toString());
		restClient.AddParam("action", "loadUserFavTeams");
		restClient.setListener(listener);
		restClient.execute();
	}
}
