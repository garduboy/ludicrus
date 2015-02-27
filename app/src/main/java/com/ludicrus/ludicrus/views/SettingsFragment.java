package com.ludicrus.ludicrus.views;

import com.facebook.Session;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.helpers.ActivityHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	public static final String KEY_PREF_LOGOUT = "pref_logout";
	public static final String KEY_PREF_PROFILE = "pref_Profile";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
	
	@Override
    public void onResume()
    {
    	super.onResume();
    	SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
		if(sportApp.isUserLogged())
		{
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    	Preference profilePref = getPreferenceManager().findPreference(KEY_PREF_PROFILE);
	    	Preference logoutPref = getPreferenceManager().findPreference(KEY_PREF_LOGOUT);
	    	
	    	profilePref.setTitle(sportApp.getUser().getName());
	    	profilePref.setSummary(getString(R.string.edit_profile));
	        logoutPref.setSummary(sportApp.getUser().getEmail());
	        
	        profilePref.setOnPreferenceClickListener(this);
	        logoutPref.setOnPreferenceClickListener(this);
		} else
		{
			this.getActivity().finish();
		}
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_PREF_LOGOUT)) {
			
		}
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(KEY_PREF_LOGOUT)) {
			System.out.println("Logout");
			SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
			sportApp.logoutUser();
			goToLogin();
		} else if (preference.getKey().equals(KEY_PREF_PROFILE)) {
//			goToEditProfile();
		}
		return false;
	}
	
//	private void goToEditProfile()
//	{
//		Intent intent = ActivityHelper.startProfileActivity(this.getActivity());
//		startActivity(intent);
//	}
	
	private void goToLogin()
	{
		Intent intent = ActivityHelper.startLoginActivity(this.getActivity());
		startActivity(intent);
		this.getActivity().finish();
		((SettingsActivity)this.getActivity()).closeAllActivities();
	}
}
