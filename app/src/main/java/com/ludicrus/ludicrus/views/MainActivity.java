package com.ludicrus.ludicrus.views;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.helpers.ActivityHelper;

public class MainActivity extends SpinnerActivity{
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scores_pager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	// Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.calendar:
            	String[] strings = getResources().getStringArray(R.array.action_list);
            	ScoresPagerFragment scoresFragment = (ScoresPagerFragment) getSupportFragmentManager().findFragmentByTag(strings[0]);
            	scoresFragment.onCalendarClick();
                return true;
            case R.id.menu_settings:
            	Intent intent = ActivityHelper.startSettingsActivity(this);
    	    	startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
