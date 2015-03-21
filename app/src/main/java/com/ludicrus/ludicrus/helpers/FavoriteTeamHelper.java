package com.ludicrus.ludicrus.helpers;

import android.content.SharedPreferences;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.core.util.EnumSportItemType;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidOrganization;
import com.ludicrus.ludicrus.classes.AndroidSportsTeam;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.interfaces.AppEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jpgarduno on 3/7/15.
 */
public class FavoriteTeamHelper implements EventListener{

    private JSONObject result;

    private static ArrayList<IOrganization> favoriteTeams;
    private static FavoriteTeamHelper favoriteTeamListener;
    private AppEvent callback;

    protected FavoriteTeamHelper()
    {
        if(favoriteTeamListener != null)
        {
            throw new RuntimeException("Only one instance of this class is allowed");
        }
    }

    public static FavoriteTeamHelper getInstance()
    {
        if(favoriteTeamListener == null)
        {
            favoriteTeamListener = new FavoriteTeamHelper();
        }
        return favoriteTeamListener;
    }

    public static ArrayList<IOrganization> getFavoriteTeams()
    {
        return favoriteTeams;
    }

    public void setCallback(AppEvent callback)
    {
        this.callback = callback;
    }

    public static void setFavoriteTeams(ArrayList<IOrganization> value)
    {
        favoriteTeams = value;
    }

    private void executeCallback()
    {
        if(callback != null) {
            callback.eventNotification();
            callback = null;
        }
    }

    private void loadFavoriteTeams(JSONArray teams)
    {
        try {
            favoriteTeams = new ArrayList<IOrganization>();
            ArrayList<String> orgsNoLogo = new ArrayList<String>();
            JSONArray items = teams;
            Set<String> teamIds = new HashSet<String>();
            for (int i = 0; i < items.length(); i++) {
                //TODO Check org type in item if we have favorite leagues too
                IOrganization fav = new AndroidSportsTeam((JSONObject) items.get(i));
                favoriteTeams.add(fav);
                teamIds.add(fav.getIdOrganization().toString());
                if(!fav.hasLogo())
                {
                    orgsNoLogo.add(fav.getIdOrganization() + ";" + fav.getOrgType());
                }
            }
            storeFavoriteTeams(teamIds);

            if(orgsNoLogo.size() > 0)
            {
                RestClientHelper.getOrganizationIcons(this, orgsNoLogo, 0); //Don't care about item type
            } else {
                executeCallback();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadFavoriteTeamsIcons(JSONArray logos)
    {
        try
        {
            for (int i = 0; i < logos.length(); i++)
            {
                JSONObject obj = (JSONObject) logos.get(i);
                int orgId = obj.getInt("orgId");
                for(int j = 0; j < favoriteTeams.size(); j++)
                {
                    if(favoriteTeams.get(j).getOrgType() == EnumSportItemType.TEAM)
                    {
                        AndroidSportsTeam team = (AndroidSportsTeam)favoriteTeams.get(j);
                        if (orgId == team.getIdOrganization()) {
                            String logo = obj.getString("orgLogo");
                            team.setLogo(logo);
                        }
                    }
                }
            }
            executeCallback();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processResult()
    {
        try
        {
            if(result == null)
            {
                return;
            }
            if(result.has("icons"))
            {
                loadFavoriteTeamsIcons((JSONArray) result.get("icons"));
            } else {
                loadFavoriteTeams((JSONArray)result.get("favTeams"));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setJSONObject(JSONObject json)
    {
        try
        {
            result = json;
            processResult();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void storeFavoriteTeams(Set<String> favTeams)
    {
        try {
            SharedPreferences settings = SportifiedApp.getAppPreferences();
            SharedPreferences.Editor editor = settings.edit();
            editor.putStringSet(SportifiedApp.PREFS_FAVORITE_TEAMS, favTeams);
            editor.commit();
        } catch (Exception e)
        {

        }
    }
}