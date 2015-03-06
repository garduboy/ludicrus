package com.ludicrus.ludicrus.views;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ludicrus.ludicrus.R;
import com.ludicrus.core.model.interfaces.IMatch;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidSoccerMatch;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.util.MatchAdapter;

public class ScoresFragment extends Fragment
{
	private Date date;
	
	private int index;
	
	private ListView mListView;
	
	private MatchAdapter mMatchAdapter;
	
	private int position;
	
	private ProgressBar mProgress;
	
	private boolean mWaiting;
	
	private TextView text;
	
	public ScoresFragment()
	{
		mWaiting = true;
	}
	
	public void hideProgress()
	{
		if(mProgress != null)
		{
			mProgress.setVisibility(View.GONE);
//			text.setVisibility(View.GONE);
		}
	}
	
	public void resultsFetched()
	{
		hideProgress();
		loadAdapter();
//		setAdapter();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), ActivityHelper.getAppTheme());

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup rootView = (ViewGroup) localInflater.inflate(
                R.layout.fragment_slide_page, container, false);

        if(position > 0 && position < 4)
        {
        	mProgress = (ProgressBar)rootView.findViewById(R.id.loadingFragment);
        	mProgress.setVisibility(View.GONE);
        	
        	text = (TextView)rootView.findViewById(R.id.date);
        	//TODO remove
        	String pageDate = DateFormat.format("yyyy-MM-dd", this.date).toString();
	    	text.setText(pageDate + " I:" + index + " P:"+ position);
//	    	text.setVisibility(View.VISIBLE);
	    	text.setVisibility(View.GONE);
        	
	        RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.content);
	        
	        mListView = new ListView(getActivity());
	        mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mListView.setDividerHeight(5);
	        Typeface quickTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Quicksand-Regular.ttf");
            Typeface lobsterTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LobsterTwo-Regular.ttf");
	        mMatchAdapter = new MatchAdapter((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            mMatchAdapter.setmTypeface(quickTypeFace);
            mMatchAdapter.setmScoreTypeface(lobsterTypeFace);
	        
	        showProgress();
	        loadAdapter();
//	        setAdapter();
	        layout.addView(mListView);
        }
        return rootView;
    }

	public void loadAdapter()
	{
		try
		{
            if(loadMatches())
            {
                setAdapter();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    private boolean loadMatches() {
        boolean matchesLoaded = false;
        try {
            String pageDate = DateFormat.format("yyyy-MM-dd", this.date).toString();
            List<IMatch> matchList = ScoresPagerFragment.matchList.get(pageDate);
            if(matchList == null)
                return false;
            FragmentActivity activity = (FragmentActivity)getActivity();
            if(activity == null)
                return false;

            Typeface quickTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface lobsterTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LobsterTwo-Regular.ttf");
            mMatchAdapter = new MatchAdapter((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            mMatchAdapter.setmTypeface(quickTypeFace);
            mMatchAdapter.setmScoreTypeface(lobsterTypeFace);

            SharedPreferences settings = getActivity().getSharedPreferences(SportifiedApp.PREFS_NAME, Context.MODE_PRIVATE);
            boolean displayFavorites = settings.getBoolean(SportifiedApp.PREFS_DISPLAY_FAVORITES, true);

            for (int i = 0; i < matchList.size(); i++) {
                IMatch match = matchList.get(i);
                if (pageDate.equals(match.getDate()))
                    if(displayFavorites)
                    {
                        //TODO create sports enumeration and change value in Core/SoccerMatch
                        if(match.getSport().equals("soccer"))
                        {
                            AndroidSoccerMatch soccerMatch = (AndroidSoccerMatch)match;
                            if(soccerMatch.isHomeTeamFavorite(settings) || soccerMatch.isAwayTeamFavorite(settings))
                            {
                                mMatchAdapter.addItem(match);
                            }
                        }
                    } else
                    {
                        mMatchAdapter.addItem(match);
                    }
            }
            matchesLoaded = true;
        } catch (Exception e) {
            matchesLoaded = false;
        }
        return matchesLoaded;
    }

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
//		loadAdapter();
	}
	
	private void setAdapter()
	{
		if(mListView != null)
			mListView.setAdapter(mMatchAdapter);
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, index);
		date = calendar.getTime();
		//TODO remove
		if(text != null)
		{
	    	String pageDate = DateFormat.format("yyyy-MM-dd", this.date).toString();
	    	text.setText(pageDate + " I:" + index + " P:"+ position);
//	    	text.setVisibility(View.VISIBLE);
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		setDate();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void showProgress()
	{
		String pageDate = DateFormat.format("yyyy-MM-dd", this.date).toString();
		if(!ScoresPagerFragment.matchList.containsKey(pageDate))
			if(mProgress != null)
			{
	    		mProgress.setVisibility(View.VISIBLE);
//	    		text.setVisibility(View.VISIBLE);
			}
	}
	
	public MatchAdapter getMatchAdapter() {
		return mMatchAdapter;
	}

	public void setMatchAdapter(MatchAdapter matchAdapter) {
		mMatchAdapter = matchAdapter;
		setAdapter();
	}

	public boolean isWaiting() {
		return mWaiting;
	}

	public void setWaiting(boolean waiting) {
		this.mWaiting = waiting;
		hideProgress();
		if(this.mWaiting == true)
		{
			showProgress();
		}
	}

    public void toggleFavorites()
    {
        loadAdapter();
    }
}