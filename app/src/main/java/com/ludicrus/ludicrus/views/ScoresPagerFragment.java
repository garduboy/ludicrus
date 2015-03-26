package com.ludicrus.ludicrus.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidSoccerMatch;
import com.ludicrus.ludicrus.classes.ScoresChangeListener;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.core.model.interfaces.IMatch;
import com.ludicrus.ludicrus.util.EnumNavAction;
import com.ludicrus.ludicrus.util.MatchAdapter;
import com.viewpagerindicator.TitlePageIndicator;

public class ScoresPagerFragment extends Fragment implements EventListener{

	// we name the left, middle and right page
	private static final int PAGE_LEFT = 0;
	private static final int PAGE_MIDDLE = 2;
	private static final int PAGE_RIGHT = 4; 
	 
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    public static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    
    private int mSelectedPageIndex = PAGE_MIDDLE;
    
    private TitlePageIndicator mTitleIndicator;
    
    private JSONObject result;
    
    public static HashMap<String, ArrayList<IMatch>> matchList = new HashMap<String, ArrayList<IMatch>>();
    
    private boolean initialized;
    
    private MediaPlayer _shootMP = null;

    private ArrayList<String> datesRequested = new ArrayList<String>();
    
    
    // we save each page in a model
    private static ScoresFragment[] mPageModel = new ScoresFragment[NUM_PAGES];
    private static String[] mPageModelTags = new String[NUM_PAGES];
    private static int mCurrentIndex = 0;
    
    Object callback;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;

    private void addDateRequest(String date, int offset) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cal.setTime(sdf.parse(date));
            for (int i = 0; i <= offset; i++) {
                if (!datesRequested.contains(date)) {
                    datesRequested.add(date);
                }
                cal.add(Calendar.DATE, 1);
                date = sdf.format(cal.getTime());
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    private void createCallbacks() {
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPageModel();
            }
        };
    	if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    		callback = new mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener()
    		{
    			@Override
    		    public void onDateSet(mirko.android.datetimepicker.date.DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth)
    			{
    				createOnDateSet(year, monthOfYear, dayOfMonth);
    		    }

    		};
    	}
    	else {
    		callback = new android.app.DatePickerDialog.OnDateSetListener()
    		{
    			@Override
    		    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
    			{
    				createOnDateSet(year, monthOfYear, dayOfMonth);
    		    }

    		};
    	}
    }

    private void createOnDateSet(int year, int monthOfYear, int dayOfMonth) {
    	Calendar calendarSelected = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        calendarSelected.set(year, monthOfYear, dayOfMonth, 0,0,0);
        currentCalendar.set(Calendar.HOUR, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        
        
        //TODO Compare to current date displayed on fragment, not current system date
        int increment = currentCalendar.compareTo(calendarSelected);
        
        if(increment == 0)
        	return;
        increment = increment * (-1);
        int difference = 0;
        
        Date date;
        date = currentCalendar.getTime();
    	String pageDate = DateFormat.format("yyyy-MM-dd", date).toString();
    	System.out.println("Current" + pageDate);
    	date = calendarSelected.getTime();
    	pageDate = DateFormat.format("yyyy-MM-dd", date).toString();
    	System.out.println("Selected" + pageDate);
        while(!((currentCalendar.get(Calendar.YEAR) == calendarSelected.get(Calendar.YEAR)) &&
        		(currentCalendar.get(Calendar.MONTH) == calendarSelected.get(Calendar.MONTH)) &&
        		(currentCalendar.get(Calendar.DATE) == calendarSelected.get(Calendar.DATE)))) {
        	currentCalendar.add(Calendar.DATE, increment);
        	difference++;
        	date = currentCalendar.getTime();
	    	pageDate = DateFormat.format("yyyy-MM-dd", date).toString();
	    	System.out.println("Current" + pageDate);
        }
        
        difference = difference * increment;
        loadDate(calendarSelected, difference);
    }

    private boolean getDisplayFavorites() {
        SharedPreferences settings = getActivity().getSharedPreferences(SportifiedApp.PREFS_NAME, Context.MODE_PRIVATE);
        boolean favorites = settings.getBoolean(SportifiedApp.PREFS_DISPLAY_FAVORITES, true);

        return favorites;
    }

	private void loadDate(Calendar calendar, int difference) {
		mTitleIndicator.invalidate();
		mPager.invalidate();
		for(int i = PAGE_LEFT; i <= PAGE_RIGHT; i++) {
    		mPageModel[i].setIndex((difference - 2) + i);
    		String pageDate = DateFormat.format("yyyy-MM-dd", mPageModel[i].getDate()).toString();
    		if(matchList.containsKey(pageDate)) {
    			mPageModel[i].loadAdapter();
			}
    		else {
	    		mPageModel[i].setMatchAdapter(null);
	    		mPageModel[i].setWaiting(true);
    		}
    	}
        initialized = false;
        calendar.add(Calendar.DATE, -1);
    	RestClientHelper.getFixtures(calendar, 2, this);
    	mPager.setCurrentItem(PAGE_MIDDLE, false);
    	mPagerAdapter.notifyDataSetChanged();
    	mCurrentIndex = mPageModel[PAGE_MIDDLE].getIndex();
	}
	
	public void onCalendarClick() {
		try {
			Calendar myCalendar = Calendar.getInstance();
	    	myCalendar.setTime(mPageModel[PAGE_MIDDLE].getDate());
	    	String tag = "";
	    	if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	    		mirko.android.datetimepicker.date.DatePickerDialog.newInstance((mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener)callback, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), tag);
	    	else
	    		new android.app.DatePickerDialog(getActivity(), (android.app.DatePickerDialog.OnDateSetListener)callback, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scores_pager_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.screen_slide, container, false); 
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.calendar:
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTime(mPageModel[PAGE_MIDDLE].getDate());
                String tag = "";
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    mirko.android.datetimepicker.date.DatePickerDialog.newInstance((mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener)callback, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), tag);
                else
                    new android.app.DatePickerDialog(getActivity(), (android.app.DatePickerDialog.OnDateSetListener)callback, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            case R.id.favorites:
                //Store the favorites display option at app level
                boolean favorites = !getDisplayFavorites();
                setDisplayFavorites(favorites);
                toggleFavoritesIcon(getDisplayFavorites(), item);

                //Update the interface to display favorites/all
                toggleFavorites();
                return true;
            case R.id.menu_settings:
                Intent intent = ActivityHelper.startSettingsActivity(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPageScrollStateChanged(int state) {
        shootSound();
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            Calendar calendar = Calendar.getInstance();
            if (mSelectedPageIndex < PAGE_MIDDLE) {
                calendar.setTime(mPageModel[PAGE_LEFT].getDate());
                mPageModel[PAGE_LEFT].setWaiting(true);
            }
            else if (mSelectedPageIndex > PAGE_MIDDLE) {
                calendar.setTime(mPageModel[PAGE_RIGHT].getDate());
                mPageModel[PAGE_RIGHT].setWaiting(true);
            }
            for(int i = PAGE_LEFT; i <= PAGE_RIGHT; i++) {
                ScoresFragment tempPage;
                int index;
                // user swiped to right direction --> left page
                if (mSelectedPageIndex < PAGE_MIDDLE) {
                    tempPage = mPageModel[(NUM_PAGES - 1) - i];
                    index = tempPage.getIndex();
                    tempPage.setIndex(index - 1);
                    setContent((NUM_PAGES - 1) - i);
                }
                // user swiped to left direction --> right page
                else if (mSelectedPageIndex > PAGE_MIDDLE) {
                    tempPage = mPageModel[i];
                    index = tempPage.getIndex();
                    tempPage.setIndex(index + 1);
                    setContent(i);
                }
            }
            RestClientHelper.getFixtures(calendar, 0, this);
            mPager.setCurrentItem(PAGE_MIDDLE, false);
            mCurrentIndex = mPageModel[PAGE_MIDDLE].getIndex();
        }
    }

    public void onPageSelected(int position) {
        mSelectedPageIndex = position;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem favoritesItem = menu.findItem(R.id.favorites);
        toggleFavoritesIcon(getDisplayFavorites(), favoritesItem);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        
    	super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        initialized = false;

        createCallbacks();

        initPageModel(savedInstanceState);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) getView().findViewById(R.id.pager);
        FragmentManager manager = getChildFragmentManager();
        mPagerAdapter = new ScreenSlidePagerAdapter(manager);
        
        //Bind the title indicator to the adapter
        mTitleIndicator = (TitlePageIndicator)getView().findViewById(R.id.titles);

        Typeface quickTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Quicksand-Regular.ttf");
        mTitleIndicator.setTypeface(quickTypeFace);
        mTitleIndicator.setOnPageChangeListener(new ScoresChangeListener(this));
		
        mPager.setAdapter(mPagerAdapter);
		mTitleIndicator.setViewPager(mPager);
		mPager.setCurrentItem(PAGE_MIDDLE, false);
		
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mPageModel[PAGE_MIDDLE].getDate());
        calendar.add(Calendar.DATE, -1);
        RestClientHelper.getFixtures(calendar, 2, this);
        handler.postDelayed(refreshRate, delay);
    }

    final Handler handler = new Handler();
    final long delay = 30000;
    public void runTask()
    {
//    	RestClientHelper.getLiveScores(this);
    }
    Runnable refreshRate = new Runnable() {	
    	@Override
    	public void run() {
//	    		Log.d(TAG, "Each minute task executing");
    		runTask();
    		handler.postDelayed(this, delay);
    	}
    };
    
//    @Override
//    public void onBackPressed() {
//        if (mPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//        }
//    }

    private void processIcons() {
        try {
            JSONArray logos = (JSONArray) result.get("icons");
            for (int i = 0; i < logos.length(); i++) {
                JSONObject obj = (JSONObject) logos.get(i);
                int orgId = obj.getInt("orgId");
                String logo = obj.getString("orgLogo");
                //Update matches to avoid going to database for logos
                ArrayList<IMatch> matches;
                for (int j = 0; j < datesRequested.size(); j++) {
                    String tempDate = datesRequested.get(j);
                    if (matchList.containsKey(tempDate)) {
                        matches = matchList.get(tempDate);
                        for (int k = 0; k < matches.size(); k++) {
                            IMatch match = matches.get(k);
                            if (match.containsTeamId(orgId)) {
                                match.setTeamLogo(orgId, logo);
                            }
                        }
                    }
                }
                SportifiedApp.storeOrganizationLogo(orgId, logo);
            }
            initScoresModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processLiveScores() {
        try {
            //TODO MOve this code to a common function
            JSONArray items = (JSONArray)result.get("items");
            IMatch match;
            for(int i = 0; i < items.length(); i++) {
                JSONObject itemMap = (JSONObject)items.get(i);
                match = new AndroidSoccerMatch((JSONObject)itemMap.get("matches"));
                ArrayList<IMatch> matches;
                if(matchList.containsKey(match.getDate())) {
                    matches = matchList.get(match.getDate());
                    for(int j = 0; j < matches.size(); j++) {
                        if(match.getIdMatch().equals(matches.get(j).getIdMatch())) {
                            matches.remove(j);
                            break;
                        }
                    }
                    matches.add(match);
                }
                else {
                    matches = new ArrayList<IMatch>();
                    matches.add(match);
                    matchList.put(match.getDate(), matches);
                }
            }
            mPageModel[PAGE_MIDDLE].loadAdapter();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void processResult() {
    	try {
            JSONArray items = (JSONArray) result.get("items");
            String startDate = (String)result.get("startDate");
            int offset = (int)result.get("offset");
            String cache = (String)result.get("dataCache");
            addDateRequest(startDate, offset);
            IMatch match;
            ArrayList<String> orgsNoLogo = new ArrayList<String>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject itemMap = (JSONObject) items.get(i);
                match = new AndroidSoccerMatch((JSONObject) itemMap.get("matches"));
                ArrayList<IMatch> matches;
                if (matchList.containsKey(match.getDate())) {
                    matches = matchList.get(match.getDate());
                    for (int j = 0; j < matches.size(); j++) {
                        if (match.getIdMatch().equals(matches.get(j).getIdMatch())) {
                            matches.remove(j);
                            break;
                        }
                    }
                    matches.add(match);
                } else {
                    matches = new ArrayList<IMatch>();
                    matches.add(match);
                    matchList.put(match.getDate(), matches);
                }

                if (!match.hasLogos()) {
                    orgsNoLogo.add(match.getLogoRequest());
                }
            }
            if (orgsNoLogo.size() > 0) {
                RestClientHelper.getOrganizationIcons(this, orgsNoLogo, 0); //Don't care about item type
            } else {
                initScoresModel();
            }
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void shootSound() {
        AudioManager meng = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (_shootMP == null)
                _shootMP = MediaPlayer.create(this.getActivity(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (_shootMP != null)
                _shootMP.start();
        }
    }

    private void refreshPageModel() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mPageModel[PAGE_MIDDLE].getDate());
        mPageModel[PAGE_MIDDLE].setWaiting(true);
        RestClientHelper.getFixtures(calendar, 0, this);
    }

    private void setContent(int index) {
        final ScoresFragment model =  mPageModel[index];
        if (mSelectedPageIndex < PAGE_MIDDLE) {
            if(index > PAGE_LEFT) {
              MatchAdapter adapter = mPageModel[index - 1].getMatchAdapter();
              model.setMatchAdapter(adapter);
              model.setWaiting(mPageModel[index - 1].isWaiting());
            }
            else
              model.setMatchAdapter(null);
        }
        else if (mSelectedPageIndex > PAGE_MIDDLE) {
            if(index < PAGE_RIGHT) {
              MatchAdapter adapter = mPageModel[index + 1].getMatchAdapter();
              model.setMatchAdapter(adapter);
              model.setWaiting(mPageModel[index + 1].isWaiting());
            }
            else
              model.setMatchAdapter(null);
        }

//		  model.setAdapter();
	}

    private void setDisplayFavorites(boolean value) {
        SharedPreferences settings = getActivity().getSharedPreferences(SportifiedApp.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SportifiedApp.PREFS_DISPLAY_FAVORITES, value);
        editor.commit();
    }

    public void setJSONObject(JSONObject json) {
        try {
            result = json;
            if(result == null)
                return;
            if(result.has("icons")) {
                processIcons();
            } else {
                String resultInfo = (String) result.get("resultInfo");
                if (resultInfo != null) {
                    if (resultInfo.equals("liveScores")) {
//                        processLiveScores();
                    }
                    else
                        processResult();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void toggleFavorites()
    {
        try {
            for(int i = PAGE_LEFT; i <= PAGE_RIGHT; i++) {
                mPageModel[i].toggleFavorites();
            }
        }
        catch (Exception e) {

        }
    }

    private void toggleFavoritesIcon(boolean displayFavorites, MenuItem item) {
        if(displayFavorites) {
            item.setIcon(R.drawable.star_full);
        } else {
            item.setIcon(R.drawable.star_empty);
        }
    }

    /* PAGE MODEL INITIALIZATION */

    private void initPageModel(Bundle savedInstanceState) {
    	for (int i = 0; i < mPageModel.length; i++) {
    		ScoresFragment fragment;
    	    if (savedInstanceState != null) {
    	    	if(mPageModelTags[i] == null)
    	    		fragment = new ScoresFragment();
    	    	else
    	    		fragment = (ScoresFragment)getChildFragmentManager().findFragmentByTag(mPageModelTags[i]);
    	    } else {
    	        fragment = new ScoresFragment();
//	    	        getSupportFragmentManager().beginTransaction().add(fragment, i+"").commit(); 
    	    }
    		// initializing the pagemodel with indexes of -1, 0 and 1
    	    if(fragment != null) {
	    	    mPageModel[i] = fragment;
	    	    mPageModel[i].setIndex((mCurrentIndex - 2) + i);
	    	    mPageModel[i].setPosition(i);
                mPageModel[i].setRefreshListener(refreshListener);
    	    }
    	}
    }

    private void initScoresModel() {
        try {
            ArrayList<String> datesLoaded = new ArrayList<String>();
            for (int i = 0; i < mPageModel.length; i++) {
                if(datesRequested.size() == 0) {
                    mPageModel[i].setWaiting(false);
                    mPageModel[i].resultsFetched();
                } else {
                    for (int j = 0; j < datesRequested.size(); j++) {
                        String tempDate = datesRequested.get(j);
                        String pageDate = DateFormat.format("yyyy-MM-dd", mPageModel[i].getDate()).toString();
                        if (tempDate.equals(pageDate) && mPageModel[i].isWaiting() == true) {
                            mPageModel[i].setWaiting(false);
                            mPageModel[i].resultsFetched();
                            datesLoaded.add(tempDate);
                        }
                    }
                }
            }
            for(int i = 0; i < datesLoaded.size(); i++) {
                datesRequested.remove(datesLoaded.get(i));
            }
            if(!initialized) {
                for (int j = 0; j < mPageModel.length; j++) {
                    mPageModel[j].setWaiting(false);
                    mPageModel[j].resultsFetched();
                }
                initialized = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    	
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//	        	return (ScoresPagerFragment)getSupportFragmentManager().findFragmentByTag(position+"");
            return mPageModel[position];
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ScoresFragment fragment = (ScoresFragment) super.instantiateItem(container, position);
            mPageModelTags[position] = fragment.getTag();
            return fragment;
        }
        
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
        	CharSequence date = DateFormat.format("E, MMM dd", mPageModel[position].getDate()).toString();
        	return date;
        }
    }
}
