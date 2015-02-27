package com.ludicrus.ludicrus.views;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
	
	public void closeAllActivities() {
		sendBroadcast(new Intent(BaseActivity.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
	}
}
