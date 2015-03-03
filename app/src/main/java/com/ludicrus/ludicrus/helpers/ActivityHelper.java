package com.ludicrus.ludicrus.helpers;

import android.content.Context;
import android.content.Intent;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.login.Login;
import com.ludicrus.ludicrus.views.MainActivity;
import com.ludicrus.ludicrus.views.SettingsActivity;
import com.ludicrus.ludicrus.views.profile.AddFavoriteTeamActivity;
import com.ludicrus.ludicrus.views.profile.SelectTeamActivity;

public class ActivityHelper
{
    public static int getActionBarTheme() {
        return R.style.LudicrusActionBarDarkTheme;
    }

    public static int getAppTheme() {
        return R.style.LudicrusLightTheme;
    }

	public static final Intent startLoginActivity(Context context)
	{
		try
		{
			Intent intent = new Intent(context, Login.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	return intent;
		}
    	catch(Exception e)
    	{
    		return null;
    	}
	}
	
	public static final Intent startAddFavoriteTeamsActivity(Context context)
	{
		try
		{
			Intent intent = new Intent(context, AddFavoriteTeamActivity.class);
	    	return intent;
		}
    	catch(Exception e)
    	{
    		return null;
    	}
	}
	
	public static final Intent startSelectTeamActivity(Context context)
	{
		try
		{
			Intent intent = new Intent(context, SelectTeamActivity.class);
	    	return intent;
		}
    	catch(Exception e)
    	{
    		return null;
    	}
	}
	
	public static final Intent startMainActivity(Context context)
	{
		try
		{
			Intent intent = new Intent(context, MainActivity.class);
	    	return intent;
		}
    	catch(Exception e)
    	{
    		return null;
    	}
	}
	
	public static final Intent startSettingsActivity(Context context)
	{
		try
		{
			Intent intent = new Intent(context, SettingsActivity.class);
	    	return intent;
		}
    	catch(Exception e)
    	{
    		return null;
    	}
	}
}
