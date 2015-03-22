package com.ludicrus.ludicrus;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.ludicrus.ludicrus.parcelable.UserMobile;
import com.ludicrus.core.util.EnumLoginType;
import com.ludicrus.ludicrus.storage.model.OrganizationLogo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmResults;

public class SportifiedApp extends Application{

	public static final String PREFS_NAME = "SportPreferences";
    public static final String PREFS_DISPLAY_FAVORITES = "SportDisplayFavorites";
    public static final String PREFS_FAVORITE_TEAMS = "SportFavoriteTeams";
	
	private boolean isUserLogged = false;
	private UserMobile user;
    private static Context appContext;
	
	public Integer getLoginType()
	{
		return user.getLoginType();
	}
	
	public UserMobile getUser()
	{
		return user;
	}
	
	public void setUser(UserMobile user)
	{
		this.user = user;
	}
	
	private void createUserInfo()
	{
		if(checkCredentials())
		{
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			
			user = new UserMobile();
	    	user.setIdUser(settings.getInt("userID", 0));
	    	user.setName(settings.getString("name", ""));
	    	user.setLastName(settings.getString("lastName", ""));
	    	user.setUserName(settings.getString("userName", ""));
	    	user.setEmail(settings.getString("email", ""));
	    	user.setLoginType(settings.getInt("loginType", 0));
		}
		else
		{
			user = null;
		}
	}

    public static SharedPreferences getAppPreferences()
    {
        try
        {
            return appContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        } catch (Exception e)
        {
            return null;
        }
    }

    public static int getSystemWindowHeight(String windowType)
    {
        try
        {
            int result = 0;
            int resourceId;
            switch (windowType)
            {
                case "NAVIGATION":
                    resourceId = appContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                    break;
                case "STATUS":
                    resourceId = appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
                    break;
                default:
                    resourceId = appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
                    break;
            }

            if (resourceId > 0) {
                result = appContext.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        } catch (Exception e)
        {
            return 0;
        }
    }

	public boolean isUserLogged()
	{
		return isUserLogged;
	}
	
	private boolean checkCredentials()
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String userName = settings.getString("userName", "");
//		String password = settings.getString("password", "");
		//TODO Is this the only check for logged user?
		if(userName.equals(""))
		{
			isUserLogged = false;
		}
		else
		{
			isUserLogged = true;
		}
		
		return isUserLogged;
	}
	
//	public void logFBUser(GraphUser userInfo)
//	{
//		try
//		{
//			//Log the user to our system
//			SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//		    SharedPreferences.Editor editor = settings.edit();
//		    editor.putString("userName", userInfo.getUsername());
////		    editor.putInt("userID", (Integer)userInfo.get("userID"));
//		    editor.putString("name", userInfo.getFirstName());
//		    editor.putString("lastName", userInfo.getLastName());
//		    editor.putString("email", (String)userInfo.getProperty("email"));
//		    editor.putInt("loginType", EnumLoginType.FACEBOOK);
//		    
//		    // Commit the edits!
//		    editor.commit();
//		    
//			createUserInfo();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//    	
//	}
	
	public void logoutUser()
	{
		if(user.getLoginType() == EnumLoginType.FACEBOOK)
		{
			if(Session.getActiveSession() != null)
			{
				Session.getActiveSession().closeAndClearTokenInformation();
			}
			Session.setActiveSession(null);
		}
		isUserLogged = false;
		user = null;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove(PREFS_NAME);
        editor.clear();
        editor.commit();
	}

	public void logUser(JSONObject userInfo)
	{
		try
		{
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		    SharedPreferences.Editor editor = settings.edit();
		    if(userInfo.has("userName")) editor.putString("userName", (String)userInfo.get("userName"));
//		    if(userInfo.has("password")) editor.putString("password", password);
		    editor.putInt("userID", (Integer)userInfo.get("userID"));
		    editor.putString("name", (String)userInfo.get("name"));
		    editor.putString("lastName", (String)userInfo.get("lastName"));
		    if(userInfo.has("email")) editor.putString("email", (String)userInfo.get("email"));

		    //TODO Remove this check, it should be required
		    if(userInfo.has("loginType")) editor.putInt("loginType", (Integer)userInfo.get("loginType"));

		    // Commit the edits!
		    editor.commit();

			createUserInfo();
		}
		catch (JSONException je)
		{
			je.printStackTrace();
		}

	}
	@Override
	public void onCreate()
	{
        appContext = getApplicationContext();
		createUserInfo();
	}

    public static void storeOrganizationLogo(int orgId, String logo) {

        Realm realm = Realm.getInstance(appContext);
        realm.beginTransaction();

        OrganizationLogo orgLogo = realm.createObject(OrganizationLogo.class);
        orgLogo.setOrgId(orgId);
        orgLogo.setLogo(logo);

        realm.commitTransaction();
        realm.close();
    }

    public static String getOrganizationLogo(int orgId) {

        Realm realm = Realm.getInstance(appContext);
        RealmResults<OrganizationLogo> orgLogos = realm.where(OrganizationLogo.class).equalTo("orgId", orgId).findAll();

        String logo = "";
        if(orgLogos.size() > 0) {
            OrganizationLogo orgLogo = orgLogos.first();
            logo = orgLogo.getLogo();
        }

        return logo;
    }
}
