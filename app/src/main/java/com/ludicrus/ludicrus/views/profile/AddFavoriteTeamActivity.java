package com.ludicrus.ludicrus.views.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.views.ScoresPagerFragment;

public class AddFavoriteTeamActivity extends ActionBarActivity {
	
	private AddFavoriteTeamFragment profile;
	
	public void displayConfederations(View view)
	{
		profile.displayConfederations(view);
	}
	
	public void displayFederations(View view)
	{
		profile.displayFederations(view);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_activity);
        
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        FragmentTransaction ft;
        ft = getSupportFragmentManager().beginTransaction();
        profile = new AddFavoriteTeamFragment();
        ft.add(R.id.auxContent, profile);
        ft.commit();
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	// Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
