package com.ludicrus.ludicrus.views.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ludicrus.ludicrus.R;

public class AddFavoriteTeamActivity extends ActionBarActivity {
	
	private AddFavoriteTeamFragment profile;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.aux_toolbar);
        setSupportActionBar(toolbar);

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
