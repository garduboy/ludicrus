package com.ludicrus.ludicrus.views.profile;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.views.SettingsFragment;

public class SelectTeamActivity extends ActionBarActivity implements EventListener 
{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
	
	public void setJSONObject(JSONObject json)
    {
    }
}
