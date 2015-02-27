package com.ludicrus.ludicrus;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.parcelable.UserMobile;
import com.ludicrus.core.util.EnumLoginType;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

public class SportifiedApp extends Application{

	public static final String PREFS_NAME = "SportPreferences";
	
	private boolean isUserLogged = false;
	private UserMobile user;
	
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
		createUserInfo();
	}
	
}
