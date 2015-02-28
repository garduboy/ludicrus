package com.ludicrus.ludicrus.views;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(
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
			String pageDate = DateFormat.format("yyyy-MM-dd", this.date).toString();
	        List<IMatch> matchList = ScoresPagerFragment.matchList.get(pageDate);
	        if(matchList == null)
	        	return;
	        FragmentActivity activity = (FragmentActivity)getActivity();
	        if(activity == null)
	        	return;
	        Typeface quickTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Quicksand-Regular.ttf");
            Typeface lobsterTypeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LobsterTwo-Regular.ttf");
	        mMatchAdapter = new MatchAdapter((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            mMatchAdapter.setmTypeface(quickTypeFace);
            mMatchAdapter.setmScoreTypeface(lobsterTypeFace);
	        
	        for(int i = 0; i < matchList.size(); i++)
	        {
	        	IMatch match = matchList.get(i);
	        	if(pageDate.equals(match.getDate()))
	        		mMatchAdapter.addItem(match);
	        }
	        setAdapter();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
}