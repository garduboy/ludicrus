package com.ludicrus.ludicrus.views.sportsTeam;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.ludicrus.core.model.interfaces.IMatch;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.interfaces.PagerScroller;
import com.ludicrus.ludicrus.util.MatchAdapter;
import com.ludicrus.ludicrus.views.ScoresPagerFragment;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpgarduno on 3/14/15.
 */
public class SportsTeamPagerFragment extends Fragment implements PagerScroller, OnScrollListener, EventListener{

    private static final int RESULTS = 0;
    private static final int CALENDAR = 1;
    private static final int SQUAD = 2;
    protected static final String ARG_POSITION = "position";
    private PagerScroller mScrollTabHolder;
    protected ListView mListView;
    private ArrayList<String> mListItems;
    protected int mMinimumHeight;
    protected boolean mExtraSpaceCalculated = false;
    protected View mExtraSpace;

    protected int mPosition;
    protected JSONObject  result;

    public static Fragment newInstance(int position, int teamId, int minimumHeight) {
        SportsTeamPagerFragment f;
        switch (position) {
            case RESULTS:
                f = new SportsTeamResultsFragment();
                break;
            default:
                f = new SportsTeamPagerFragment();
                break;
        }

        f.setMinimumHeight(minimumHeight);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt("sportsTeamId", teamId);
        f.setArguments(b);
        return f;
    }

    public void setMinimumHeight(int minimumHeight)
    {
        mMinimumHeight = minimumHeight;
        mExtraSpaceCalculated = false;
        if(mListView != null)
            mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mListView.setVisibility(View.GONE);
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);
        mListItems = new ArrayList<String>();

        for (int i = 1; i <= 100; i++) {
            mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
        }

        mListView = new ListView(container.getContext());

        View placeHolderView = inflater.inflate(R.layout.sports_team_header, mListView, false);
        mListView.addHeaderView(placeHolderView);


        mListView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            if(mMinimumHeight > 0 && !mExtraSpaceCalculated) {
                int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
                View itemView = mListView.getChildAt(1);
                int itemHeight = itemView.getHeight();
                int itemCount = mListView.getAdapter().getCount() - 1;
                int listViewDividerHeight = mListView.getDividerHeight();
                int listViewHeight = (itemHeight * itemCount) + (listViewDividerHeight * (itemCount - 1));
                if (mMinimumHeight > listViewHeight && mListView.getFooterViewsCount() == 0) {
                    if(mExtraSpace == null) {
                        mExtraSpace = new View(mListView.getContext());
                        mExtraSpace.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mMinimumHeight - listViewHeight));
                    }
                    mListView.addFooterView(mExtraSpace);
                } else {

                    if(mListView.getFooterViewsCount() > 0 && mExtraSpace != null) {
                        mListView.removeFooterView(mExtraSpace);
                    }
                }
                mExtraSpaceCalculated = true;
            }
            }
        });
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mPosition > 0) {  //TODO REMOVE
            mListView.setOnScrollListener(this);

            MatchAdapter mMatchAdapter = new MatchAdapter(getActivity().getLayoutInflater());

            List<IMatch> matchList = ScoresPagerFragment.matchList.get("2015-03-14");
            for (int i = 0; i < matchList.size(); i++) {
                IMatch match = matchList.get(i);
                mMatchAdapter.addItem(match);
            }

            mListView.setAdapter(mMatchAdapter);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    public void setScrollTabHolder(PagerScroller scrollTabHolder)
    {
        mScrollTabHolder = scrollTabHolder;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        // nothing
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing
    }

    public void setJSONObject(JSONObject json)
    {

    }
}
