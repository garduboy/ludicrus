package com.ludicrus.ludicrus.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.helpers.RestClientHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.parcelable.UserMobile;
import com.ludicrus.ludicrus.util.ExpandableListAdapter;
import com.ludicrus.ludicrus.views.profile.ProfileFragment;

abstract public class SpinnerActivity extends BaseActivity implements EventListener {
	protected OnNavigationListener navigationListener;
	private boolean initialized = false;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	ScoresPagerFragment scores;
	ProfileFragment profile;
	private JSONObject result;
	
	public void addFavoriteTeam(View view)
	{
		Intent intent = ActivityHelper.startAddFavoriteTeamsActivity(this);
		startActivity(intent);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
        UserMobile user = sportApp.getUser();
        //Setting the adapter dynamically
        RestClientHelper.getUserFavTeams(user.getIdUser(), this);
        
        setContentView(R.layout.main_activity);
        mTitle = getString(R.string.title_activity_scores);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
    	
        final EventListener context = this;
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.title_activity_user_home  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Open Drawer");
                
                SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
                UserMobile user = sportApp.getUser();
                RestClientHelper.getUserFavTeams(user.getIdUser(), context);
            }
        };
        
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        //Dynamically add the scores fragment
		FragmentTransaction ft;
		scores = new ScoresPagerFragment();
	    ft = getSupportFragmentManager().beginTransaction();
	    //Should be add, let's find out where do we need to remove it when the configuration changes
	    ft.replace(R.id.mainContent, scores, mTitle.toString());
	    ft.commit();
		
		//Set up drop-down navigation
//  		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
//  		          R.layout.simple_spinner_item_sportified);
//  		ActionBar actionBar = getSupportActionBar();
//  		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//  		actionBar.setListNavigationCallbacks(spinnerAdapter, navigationListener);
//  		actionBar.setDisplayShowTitleEnabled(false);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	private void processResult()
	{
		try
		{
			if(result == null)
			{
				return;
			}
			String[] navItems = getResources().getStringArray(R.array.action_list);
			ArrayList<String> headers = new ArrayList<String>();
			HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
			
			for(int i = 0; i < navItems.length; i++)
			{
				String navItem = navItems[i];
				List<String> subItems = new ArrayList<String>();
				if(navItem.equals(getString(R.string.title_activity_myTeams)))
				{
					JSONArray items = (JSONArray)result.get("favTeams");
					if(items.length() > 0)
						subItems = new ArrayList<String>();
					
					for(int j = 0; j < items.length(); j++)
					{
						JSONObject obj = (JSONObject)items.get(j);
						subItems.add((String)obj.get("name"));
					}
				}
				headers.add(navItem);
				listDataChild.put(navItem, subItems);
			}
			
	        // Set the adapter for the list view
	        mDrawerList.setAdapter(new ExpandableListAdapter(this, headers, listDataChild));
	        mDrawerList.setGroupIndicator(null);
	        
	        // Set the list's click listener
	        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
	        	@Override
	    	    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
	    	        selectItem(groupPosition);
	    	        return true;
	    	    }
	        });
	        
	        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					selectItem(groupPosition);
					return false;
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	 
	public void setJSONObject(JSONObject json)
    {
		result = json;
    	processResult();
    }
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
	
	/** Swaps fragments in the main content view */
	private void selectItem(int groupPosition) {
		// Get the same strings provided for the drop-down's ArrayAdapter
    	String[] strings = getResources().getStringArray(R.array.action_list);
		Intent intent;
		FragmentTransaction ft;
		switch(groupPosition)
		{
		case 0:
			//Scores
			// Create new fragment from our own Fragment class
//			if(scores == null)
				scores = new ScoresPagerFragment();
		    ft = getSupportFragmentManager().beginTransaction();
		    // Replace whatever is in the fragment container with this fragment
		    // and give the fragment a tag name equal to the string at the position selected
		    ft.replace(R.id.mainContent, scores, strings[groupPosition]);
		    // Apply changes
		    ft.commit();
			break;
		case 1:
			//Profile
//			if(profile == null)
				profile = new ProfileFragment();
			ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.mainContent, profile, strings[groupPosition]);
		    // Apply changes
		    ft.commit();
			break;
		case 2:
			//My Teams
			return;
		}
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(groupPosition, true);
	    setTitle(strings[groupPosition]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
}
