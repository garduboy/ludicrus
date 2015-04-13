package com.ludicrus.ludicrus.views.sportsTeam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ludicrus.core.model.interfaces.IMatch;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidSoccerMatch;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.util.MatchAdapter;
import com.ludicrus.ludicrus.views.ScoresPagerFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jpgarduno on 3/24/15.
 */
public class SportsTeamResultsFragment extends SportsTeamPagerFragment{

    private MatchAdapter mMatchAdapter;
    private Integer mTeamId;
    private ArrayList<IMatch> mMatches;
    private boolean mReady = false;
    private boolean mAutoIncrement = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        mTeamId = getArguments().getInt("sportsTeamId");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        RestClientHelper.getTeamResults(mTeamId, 20, calendar.getTime(), this);
    }

    private void processIcons()
    {
        try {
            JSONArray logos = (JSONArray) result.get("icons");
            for (int i = 0; i < logos.length(); i++) {
                JSONObject obj = (JSONObject) logos.get(i);
                int orgId = obj.getInt("orgId");
                String logo = obj.getString("orgLogo");
                //Update matches to avoid going to database for logos
                for (int j = 0; j < mMatches.size(); j++) {
                    IMatch match = mMatches.get(j);
                    if (match.containsTeamId(orgId)) {
                        match.setTeamLogo(orgId, logo);
                    }
                }
                SportifiedApp.storeOrganizationLogo(orgId, logo);
            }
            initResultsModel();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initResultsModel() {
        try {
            if(mMatchAdapter == null) {
                mMatchAdapter = new MatchAdapter(getActivity().getLayoutInflater());
                mMatchAdapter.setDisplayDate(true);
                mListView.setAdapter(mMatchAdapter);
            }

            for (int i = 0; i < mMatches.size(); i++) {
                IMatch match = mMatches.get(i);
                mMatchAdapter.addItem(match);
            }
            mAutoIncrement = false;
            mReady = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setJSONObject(JSONObject json)
    {
        try
        {
            result = json;
            if(result == null)
                return;
            if(result.has("icons")) {
                processIcons();
            }
            else if(result.has("resultInfo")) {
                String resultInfo = (String)result.get("resultInfo");
                if(resultInfo.equals("teamResults")) {
                    JSONArray items = (JSONArray) result.get("items");
                    ArrayList<String> orgsNoLogo = new ArrayList<String>();
                    mMatches  = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject itemMap = (JSONObject) items.get(i);
                        IMatch match = new AndroidSoccerMatch((JSONObject) itemMap.get("matches"));
                        mMatches.add(match);

                        if (!match.hasLogos()) {
                            orgsNoLogo.add(match.getLogoRequest());
                        }
                    }

                    if (orgsNoLogo.size() > 0) {
                        RestClientHelper.getOrganizationIcons(this, orgsNoLogo, 0); //Don't care about item type
                    } else {
                        initResultsModel();
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    //TODO REMOVE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(container.getContext());

        View placeHolderView = inflater.inflate(R.layout.sports_team_header, mListView, false);
        mListView.addHeaderView(placeHolderView);

        final AbsListView.OnScrollListener listener = this;
        mListView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mMinimumHeight > 0 && !mExtraSpaceCalculated && mReady) {
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
                    //Set the scroll listener until the list has been populated
                    mListView.setOnScrollListener(listener);

                    mExtraSpaceCalculated = true;
                }
            }
        });
        return mListView;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

        try {
            final int lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem == totalItemCount && !mAutoIncrement) {
                if (mMatchAdapter != null) {
                    if (mMatchAdapter.getCount() < 60) {
                        IMatch match = (IMatch) mMatchAdapter.getItem(mMatchAdapter.getCount() - 1);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cal.setTime(sdf.parse(match.getDate()));
                        RestClientHelper.getTeamResults(mTeamId, 20, cal.getTime(), this);
                        mAutoIncrement = true;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
