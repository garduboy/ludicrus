package com.ludicrus.ludicrus.helpers;

import android.content.SharedPreferences;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidOrganization;
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

    private void loadFavoriteTeams(JSONArray teams)
    {
        try {
            favoriteTeams = new ArrayList<IOrganization>();
            JSONArray items = teams;
            Set<String> teamIds = new HashSet<String>();
            for (int i = 0; i < items.length(); i++) {
                IOrganization fav = new AndroidOrganization((JSONObject) items.get(i));
                favoriteTeams.add(fav);
                teamIds.add(fav.getIdOrganization().toString());
            }
            storeFavoriteTeams(teamIds);
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
            loadFavoriteTeams((JSONArray)result.get("favTeams"));
            if(callback != null) {
                callback.eventNotification();
                callback = null;
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