package com.ludicrus.ludicrus.views.sportsTeam;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.classes.AndroidSportsTeam;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.helpers.ui.AssetsHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.interfaces.PagerScroller;
import com.ludicrus.ludicrus.library.view.SlidingTabLayout;
import com.ludicrus.ludicrus.util.EnumTypeface;
import com.ludicrus.ludicrus.util.OrganizationLogoRenderer;
import com.ludicrus.ludicrus.views.MainActivity;

import org.json.JSONObject;

/**
 * Created by jpgarduno on 3/14/15.
 */
public class SportsTeamMainFragment extends Fragment implements PagerScroller, ViewPager.OnPageChangeListener, EventListener {

    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    private Drawable    mActionBarBackgroundDrawable;
    private int         mActionBarHeight;
    private View        mHeader;
    private ImageView   mHeaderBackground;
    private int         mHeaderHeight;
    private ImageView   mHeaderLogo;
    private PagerAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private TextView    mTeamName;
    private Toolbar     mToolbar;
    private ViewPager   mViewPager;
    private float       mRatio = -1;

    private ActionBar   mainActionBar;


    private JSONObject  result;
    private float lastTopValueAssigned = 0;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    @Override
    public void adjustScroll(int scrollHeight) {
        // nothing
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private void interpolate(View view1, View view2, float interpolation, boolean scale) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        if(scale) {
            float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
            float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
            view1.setScaleX(scaleX);
            view1.setScaleY(scaleY);
        }

        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));
        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY);

        mRatio = interpolation;
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        TypedValue mTypedValue = new TypedValue();

        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private int getScreenAvailableHeight()
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Rect rect = new Rect();
        Window win = getActivity().getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusHeight = rect.top;
//        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
//        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();
        int screenSize = size.y;

        int toolbarHeight = mToolbar.getHeight();
        int tabsHeight = mSlidingTabLayout.getHeight();
        int height = screenSize - toolbarHeight - statusHeight - tabsHeight;

        return height;
    }

    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        //Strange issue where the first item in the list view is offset by 2 pixels everytime there is a page change
        //This alleviates it but it is not the proper fix
        int top = c.getTop() + 2;

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewTreeObserver vto = mToolbar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mRatio > 0) {
                    ImageView iconView = (ImageView) getActivity().findViewById(R.id.toolbar_logo);
                    interpolate(mHeaderLogo, iconView, mRatio, true);
                    TextView toolbarText = (TextView) getActivity().findViewById(R.id.toolbar_team_name);
                    interpolate(mTeamName, toolbarText, mRatio, false);
                }
                //Recalculate list view extra space
                mPagerAdapter.setAvailableSpace();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.sports_team_pager_activity, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActionBar.show();
        ((MainActivity)getActivity()).resetMainToolbar();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetainInstance(true);
        mainActionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        mainActionBar.hide();
        mToolbar = (Toolbar)getActivity().findViewById(R.id.sports_team_toolbar);
        ((ActionBarActivity)getActivity()).setSupportActionBar(mToolbar);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        ((MainActivity)getActivity()).syncDrawerToggle();

        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);

        mHeaderLogo = (ImageView) getActivity().findViewById(R.id.header_logo);
        mHeaderBackground = (ImageView)getActivity().findViewById(R.id.sports_team_bg);
        mHeader = getActivity().findViewById(R.id.sports_team_header);
        mTeamName = (TextView)getActivity().findViewById(R.id.sports_team_name);

        mSlidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sports_team_tabs);
        mViewPager = (ViewPager) getActivity().findViewById(R.id.sports_team_pager);

        mViewPager.setOffscreenPageLimit(4);

        mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        mPagerAdapter.setTabHolderScrollingContent(this);

        mViewPager.setAdapter(mPagerAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(this);

        Bundle bundle = getArguments();
        mTeamName.setText(bundle.getString("sportsTeamName"));
        mTeamName.setTypeface(AssetsHelper.getTypeFace(getActivity(), EnumTypeface.QUICKSAND));
        mTeamName.setVisibility(View.INVISIBLE);
        int teamId = bundle.getInt("sportsTeamId");
        RestClientHelper.getSportsTeamDetails(this, String.valueOf(teamId));

        mActionBarBackgroundDrawable = mToolbar.getBackground();
        mActionBarBackgroundDrawable.setAlpha(0);
        getActivity().setTitle("");
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // nothing
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing
    }

    @Override
    public void onPageSelected(int position) {
        SparseArrayCompat<PagerScroller> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
        PagerScroller currentHolder = scrollTabHolders.valueAt(position);

        if(currentHolder != null) {
            currentHolder.adjustScroll((int) (mHeader.getHeight() + mHeader.getTranslationY()));
        }
    }

    private float parallaxImage(View view) {
        float ratio = 0;
        if(view != null)
        {
            int headerHeight = mHeaderBackground.getHeight() - mSlidingTabLayout.getHeight() - getActionBarHeight();
            float headerViewTop = -(mHeader.getTranslationY());
            if (lastTopValueAssigned != headerViewTop) {
                lastTopValueAssigned = headerViewTop;
                float headerTop = (float)(headerViewTop / 2);
                view.setY(headerTop);

                float viewTop = -(mHeader.getTranslationY());

                if (viewTop > 0 && headerHeight > 0)
                    ratio = (float) Math.min(Math.max(viewTop, 0), headerHeight) / headerHeight;
                int newAlpha = (int) (ratio * 255);

                mActionBarBackgroundDrawable = mToolbar.getBackground();
                mActionBarBackgroundDrawable.setAlpha(newAlpha);
            }
        }
        return ratio;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            int headerTranslation = -(mHeaderBackground.getHeight() - mSlidingTabLayout.getHeight() - getActionBarHeight());
            mHeader.setTranslationY(Math.max(-scrollY, headerTranslation));

            float ratio = parallaxImage(mHeaderBackground);
            if(ratio > 0) {
                ImageView iconView = (ImageView) getActivity().findViewById(R.id.toolbar_logo);
                interpolate(mHeaderLogo, iconView, sSmoothInterpolator.getInterpolation(ratio), true);
                TextView toolbarText = (TextView) getActivity().findViewById(R.id.toolbar_team_name);
                interpolate(mTeamName, toolbarText, sSmoothInterpolator.getInterpolation(ratio), false);
            }
        }
    }

    private void populateTeamInfo(JSONObject sportsTeam) {
        try {
            IOrganization organization = new AndroidSportsTeam(sportsTeam);
            Bitmap teamLogo = OrganizationLogoRenderer.getMaskedOrganizationLogo(organization.getLogo(), getActivity());
            mHeaderLogo.setImageBitmap(teamLogo);
            mTeamName.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }

    }

    public void setJSONObject(JSONObject json)
    {
        try
        {
            result = json;
            if(result == null)
                return;
            String resultInfo = (String)result.get("resultInfo");
            if(resultInfo.equals("teamDetails"))
            {
                JSONObject sportsTeam = (JSONObject)result.get("team");
                populateTeamInfo(sportsTeam);
                //Populate Results, Calendar, Squad
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        private SparseArrayCompat<PagerScroller> mScrollTabHolders;
        private final String[] TITLES = { getString(R.string.results), getString(R.string.calendar), getString(R.string.squad)};
        private SportsTeamPagerFragment[] fragments;
        private PagerScroller mListener;
        private int mAvailableSpace = 0;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            mScrollTabHolders = new SparseArrayCompat<PagerScroller>();
        }

        private SportsTeamPagerFragment[] getFragments()
        {
            if(fragments == null)
            {
                fragments = new SportsTeamPagerFragment[getCount()];
            }
            return fragments;
        }

        public void setAvailableSpace()
        {
            mAvailableSpace = getScreenAvailableHeight();
            for(int i = 0; i < getFragments().length; i++)
            {
                if(fragments[i] != null)
                    fragments[i].setMinimumHeight(mAvailableSpace);
            }
        }

        public void setTabHolderScrollingContent(PagerScroller listener) {
            mListener = listener;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            int teamId = getArguments().getInt("sportsTeamId");
            SportsTeamPagerFragment fragment = (SportsTeamPagerFragment) SportsTeamPagerFragment.newInstance(position, teamId,mAvailableSpace);
            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);
            }

            getFragments()[position] = fragment;
            setAvailableSpace();

            return fragments[position];
        }

        public SparseArrayCompat<PagerScroller> getScrollTabHolders() {
            return mScrollTabHolders;
        }

    }

}