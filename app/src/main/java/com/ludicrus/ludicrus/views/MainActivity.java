package com.ludicrus.ludicrus.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.util.EnumNavAction;

public class MainActivity extends SpinnerActivity{

    private boolean getDisplayFavorites()
    {
        SharedPreferences settings = getSharedPreferences(SportifiedApp.PREFS_NAME, MODE_PRIVATE);
        boolean favorites = settings.getBoolean(SportifiedApp.PREFS_DISPLAY_FAVORITES, true);

        return favorites;
    }

    private void setDisplayFavorites(boolean value)
    {
        SharedPreferences settings = getSharedPreferences(SportifiedApp.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SportifiedApp.PREFS_DISPLAY_FAVORITES, value);
        editor.commit();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scores_pager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem favoritesItem = menu.findItem(R.id.favorites);
        toggleFavoritesIcon(getDisplayFavorites(), favoritesItem);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String[] strings;
        ScoresPagerFragment scoresFragment;
    	// Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.calendar:
            	strings = getResources().getStringArray(R.array.action_list);
                scoresFragment = (ScoresPagerFragment) getSupportFragmentManager().findFragmentByTag(strings[EnumNavAction.NAVIGATION_SCORES]);
            	scoresFragment.onCalendarClick();
                return true;
            case R.id.favorites:
                //Store the favorites display option at app level
                boolean favorites = !getDisplayFavorites();
                setDisplayFavorites(favorites);
                toggleFavoritesIcon(getDisplayFavorites(), item);

                //Update the interface to display favorites/all
                strings = getResources().getStringArray(R.array.action_list);
                scoresFragment = (ScoresPagerFragment) getSupportFragmentManager().findFragmentByTag(strings[EnumNavAction.NAVIGATION_SCORES]);
                scoresFragment.toggleFavorites();
                return true;
            case R.id.menu_settings:
            	Intent intent = ActivityHelper.startSettingsActivity(this);
    	    	startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleFavoritesIcon(boolean displayFavorites, MenuItem item)
    {
        if(displayFavorites) {
            item.setIcon(R.drawable.star_full);
        } else {
            item.setIcon(R.drawable.star_empty);
        }
    }
}
