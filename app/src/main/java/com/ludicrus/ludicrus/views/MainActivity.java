package com.ludicrus.ludicrus.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.core.model.interfaces.ISportsTeam;
import com.ludicrus.core.util.EnumSportItemType;
import com.ludicrus.core.util.EnumSportType;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.helpers.FavoriteTeamHelper;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.parcelable.UserMobile;
import com.ludicrus.ludicrus.interfaces.AppEvent;
import com.ludicrus.ludicrus.util.EnumNavAction;
import com.ludicrus.ludicrus.util.ExpandableListAdapter;
import com.ludicrus.ludicrus.util.TypefaceSpan;
import com.ludicrus.ludicrus.views.profile.ProfileFragment;
import com.ludicrus.ludicrus.views.sportsTeam.SportsTeamMainFragment;

public class MainActivity extends BaseActivity implements AppEvent {
	protected OnNavigationListener navigationListener;
	private boolean initialized = false;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	ScoresPagerFragment scores;
	ProfileFragment profile;
    SportsTeamMainFragment mTeam;
    private Toolbar mainToolbar;
	private JSONObject result;
	
	public void addFavoriteTeam(View view)
	{
		Intent intent = ActivityHelper.startAddFavoriteTeamsActivity(this);
		startActivity(intent);
	}

    public void eventNotification()
    {
        setupNavigationDrawer();
    }

    public void resetMainToolbar() {
        setSupportActionBar(mainToolbar);
        syncDrawerToggle();
    }

    public void syncDrawerToggle() {
        mDrawerToggle.syncState();
    }

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
        UserMobile user = sportApp.getUser();
        //Setting the adapter dynamically
        RestClientHelper.getUserFavTeams(user.getIdUser(), this);

        setContentView(R.layout.main_activity);

        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);

        final AppEvent callback = this;
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
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

                //Favorite teams might have changed so let's reload
                setupNavigationDrawer();
            }
        };
        
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        //Dynamically add the scores fragment
        mTitle = getString(R.string.matches);

		FragmentTransaction ft;
		scores = new ScoresPagerFragment();
	    ft = getSupportFragmentManager().beginTransaction();
	    //Should be add, let's find out where do we need to remove it when the configuration changes
	    ft.replace(R.id.mainContent, scores, mTitle.toString());
	    ft.commit();
        setTitle("");
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
	
	private void setupNavigationDrawer()
	{
		try
		{
			String[] navItems = getResources().getStringArray(R.array.action_list);
			ArrayList<String> headers = new ArrayList<String>();
			HashMap<String, List<Object>> listDataChild = new HashMap<String, List<Object>>();

            ArrayList<IOrganization> favTeams = FavoriteTeamHelper.getFavoriteTeams();

			for(int i = 0; i < navItems.length; i++)
			{
				String navItem = navItems[i];
				List<Object> subItems = new ArrayList<Object>();
				if(i == EnumNavAction.NAVIGATION_FAVORITES)
				{
					if(favTeams.size() > 0)
						subItems = new ArrayList<Object>();
					
					for(int j = 0; j < favTeams.size(); j++)
					{
						IOrganization org = (IOrganization)favTeams.get(j);
						subItems.add(org);//org.getName() + "::" + org.getIdOrganization());
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
                    selectItem(groupPosition, childPosition, id);
	    	        return true;
	    	    }
	        });
	        
	        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					selectItem(groupPosition, -1, 0);
					return false;
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
        SpannableString s = new SpannableString(mTitle);
        s.setSpan(new TypefaceSpan(this, "LobsterTwo-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int groupPosition, int childPosition, long id) {

		// Get the same strings provided for the drop-down's ArrayAdapter
    	String[] strings = getResources().getStringArray(R.array.action_list);
		Intent intent;
		FragmentTransaction ft;
        String title = strings[groupPosition];
		switch(groupPosition) {
		case EnumNavAction.NAVIGATION_SCORES:
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
            //TODO
            title = "";
			break;
		case EnumNavAction.NAVIGATION_PROFILE:
			//Profile
//			if(profile == null)
				profile = new ProfileFragment();
			ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.mainContent, profile, strings[groupPosition]);
		    // Apply changes
		    ft.commit();
			break;
		case EnumNavAction.NAVIGATION_FAVORITES:
			//My Teams
            if(childPosition >= 0) {
                ArrayList<IOrganization> favTeams = FavoriteTeamHelper.getFavoriteTeams();
                IOrganization team = null;
                for(int i = 0; i < favTeams.size(); i++) {
                    if(favTeams.get(i).getIdOrganization() == id) {
                        team = favTeams.get(i);
                    }
                }
                if(team != null) {
                    if(team.getOrgType() == EnumSportItemType.TEAM) {
                        try {
                            ISportsTeam sportsTeam = (ISportsTeam)team;
                            if(sportsTeam.getSportType() == EnumSportType.SOCCER) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("sportsTeamId", sportsTeam.getIdTeam());
                                bundle.putString("sportsTeamName", sportsTeam.getName());
                                mTeam = new SportsTeamMainFragment();
                                mTeam.setArguments(bundle);
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.mainContent, mTeam, "");
                                // Apply changes
                                ft.commit();
                                title = "";
                            }
                        }
                        catch (Exception e){

                        }
                    }
                }
            } else {
                return;
            }
			break;
		}
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(groupPosition, true);
	    setTitle(title);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
}
