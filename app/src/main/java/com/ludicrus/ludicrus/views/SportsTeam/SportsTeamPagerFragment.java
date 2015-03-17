package com.ludicrus.ludicrus.views.sportsTeam;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ludicrus.core.model.interfaces.IMatch;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.interfaces.PagerScroller;
import com.ludicrus.ludicrus.util.MatchAdapter;
import com.ludicrus.ludicrus.views.ScoresPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpgarduno on 3/14/15.
 */
public class SportsTeamPagerFragment extends Fragment implements PagerScroller, OnScrollListener{

    private static final String ARG_POSITION = "position";
    private PagerScroller mScrollTabHolder;
    private ListView mListView;
    private ArrayList<String> mListItems;
    private int mMinimumHeight;
    private boolean mExtraSpaceCalculated = false;

    private int mPosition;

    public static Fragment newInstance(int position, int minimumHeight) {
        SportsTeamPagerFragment f = new SportsTeamPagerFragment();
        f.setMinimumHeight(minimumHeight);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    public SportsTeamPagerFragment()
    {
        super();
    }

    public void setMinimumHeight(int minimumHeight)
    {
        mMinimumHeight = minimumHeight;
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPosition = getArguments().getInt(ARG_POSITION);

        mListItems = new ArrayList<String>();

        for (int i = 1; i <= 100; i++) {
            mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    if (mMinimumHeight > listViewHeight) {
                        View extraSpace = new View(mListView.getContext());
                        extraSpace.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mMinimumHeight - listViewHeight));
                        mListView.addFooterView(extraSpace);
                    }
                    mExtraSpaceCalculated = true;
                }
            }
        });
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setOnScrollListener(this);

        MatchAdapter mMatchAdapter = new MatchAdapter(getActivity().getLayoutInflater());

        List<IMatch> matchList = ScoresPagerFragment.matchList.get("2015-03-14");
        for (int i = 0; i < matchList.size(); i++) {
            IMatch match = matchList.get(i);
            mMatchAdapter.addItem(match);
        }

        mListView.setAdapter(mMatchAdapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();

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
}
