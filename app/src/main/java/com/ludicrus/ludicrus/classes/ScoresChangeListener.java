package com.ludicrus.ludicrus.classes;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.ludicrus.ludicrus.views.ScoresPagerFragment;

public class ScoresChangeListener implements OnPageChangeListener
{
	private ScoresPagerFragment mFragment;
	
	public ScoresChangeListener(ScoresPagerFragment fragment)
	{
		mFragment = fragment;
	}
	
	@Override
	public void onPageSelected(int position)
	{
		mFragment.onPageSelected(position);
	}
   
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) { }
   
	@Override
	public void onPageScrollStateChanged(int state)
	{
		mFragment.onPageScrollStateChanged(state);
	}
}
