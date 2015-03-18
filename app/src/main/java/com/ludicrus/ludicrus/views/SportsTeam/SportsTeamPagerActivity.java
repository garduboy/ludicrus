package com.ludicrus.ludicrus.views.sportsTeam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
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

import org.json.JSONObject;

/**
 * Created by jpgarduno on 3/14/15.
 */
public class SportsTeamPagerActivity extends ActionBarActivity implements PagerScroller, ViewPager.OnPageChangeListener, EventListener {

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
    //    private SpannableString mSpannableString;
//    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;


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

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        TypedValue mTypedValue = new TypedValue();

        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private int getScreenAvailableHeight()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Rect rect = new Rect();
        Window win = getWindow();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sports_team_pager_activity);

        mToolbar = (Toolbar)findViewById(R.id.sports_team_toolbar);
        setSupportActionBar(mToolbar);

        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);

        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
        mHeaderBackground = (ImageView)findViewById(R.id.sports_team_bg);
        mHeader = findViewById(R.id.sports_team_header);
        mTeamName = (TextView)findViewById(R.id.sports_team_name);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sports_team_tabs);
        mViewPager = (ViewPager) findViewById(R.id.sports_team_pager);

        mViewPager.setOffscreenPageLimit(4);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setTabHolderScrollingContent(this);

        mViewPager.setAdapter(mPagerAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(this);

        FrameLayout root = (FrameLayout)findViewById(R.id.sports_team_layout);
        root.post(new Runnable() {
            public void run() {
                mPagerAdapter.setAvailableSpace(getScreenAvailableHeight());
                //setListAdapter(getScreenAvailableHeight());
            }
        });


        Intent intent = getIntent();
        mTeamName.setText(intent.getStringExtra("sportsTeamName"));
        mTeamName.setTypeface(AssetsHelper.getTypeFace(this, EnumTypeface.QUICKSAND));
        long teamId = intent.getLongExtra("sportsTeamId", 0);
        RestClientHelper.getSportsTeamDetails(this, String.valueOf(teamId));

//        mSpannableString = new SpannableString(getString(R.string.actionbar_title));
//        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);

//        findViewById(android.R.id.home).setAlpha(0f);

        mActionBarBackgroundDrawable = mToolbar.getBackground();
        mActionBarBackgroundDrawable.setAlpha(0);
    }

    private void setListAdapter(int screenHeight)
    {

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

    private void parallaxImage(View view) {
        if(view != null)
        {
            int headerHeight = mHeaderBackground.getHeight() - mSlidingTabLayout.getHeight() - getActionBarHeight();
            float ratio = 0;
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
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            int headerTranslation = -(mHeaderBackground.getHeight() - mSlidingTabLayout.getHeight() - getActionBarHeight());
            mHeader.setTranslationY(Math.max(-scrollY, headerTranslation));
//            float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
//            ImageView iconView = (ImageView) findViewById(android.support.v7.appcompat.R.id.home);
//            interpolate(mHeaderLogo, iconView, sSmoothInterpolator.getInterpolation(ratio));
//            setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            parallaxImage(mHeaderBackground);
        }
    }

    private void populateTeamInfo(JSONObject sportsTeam)
    {
        try
        {
            IOrganization organization = new AndroidSportsTeam((JSONObject) sportsTeam);
            Bitmap teamLogo = OrganizationLogoRenderer.getMaskedOrganizationLogo(organization.getLogo(), this);
            mHeaderLogo.setImageBitmap(teamLogo);

        } catch (Exception e)
        {

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

    private void setTitleAlpha(float alpha) {
//        mAlphaForegroundColorSpan.setAlpha(alpha);
//        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getSupportActionBar().setTitle(mSpannableString);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

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

        public void setAvailableSpace(int availableSpace)
        {
            mAvailableSpace = availableSpace;
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
            SportsTeamPagerFragment fragment = (SportsTeamPagerFragment) SportsTeamPagerFragment.newInstance(position, mAvailableSpace);

            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);
            }

            getFragments()[position] = fragment;

            return fragments[position];
        }

        public SparseArrayCompat<PagerScroller> getScrollTabHolders() {
            return mScrollTabHolders;
        }

    }

}
//************Original activity kept here for the record. REMOVE later
//
//package com.ludicrus.ludicrus.views.sportsTeam;
//
//        import android.app.ActionBar;
//        import android.content.Context;
//        import android.content.res.TypedArray;
//        import android.graphics.Point;
//        import android.graphics.Rect;
//        import android.graphics.drawable.Drawable;
//        import android.os.Bundle;
//        import android.support.v4.view.PagerAdapter;
//        import android.support.v4.view.ViewPager;
//        import android.support.v4.widget.SwipeRefreshLayout;
//        import android.support.v7.widget.Toolbar;
//        import android.text.Layout;
//        import android.util.Log;
//        import android.view.Display;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.view.Window;
//        import android.widget.AbsListView;
//        import android.widget.ArrayAdapter;
//        import android.widget.BaseAdapter;
//        import android.widget.FrameLayout;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.ListView;
//        import android.widget.ScrollView;
//        import android.widget.TextView;
//
//        import com.ludicrus.core.model.interfaces.IMatch;
//        import com.ludicrus.ludicrus.R;
//        import com.ludicrus.ludicrus.library.view.SlidingTabLayout;
//        import com.ludicrus.ludicrus.util.MatchAdapter;
//        import com.ludicrus.ludicrus.views.BaseActivity;
//        import com.ludicrus.ludicrus.views.ScoresPagerFragment;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
///**
// * Created by jpgarduno on 3/11/15.
// */
//public class SportsTeamActivity extends BaseActivity implements AbsListView.OnScrollListener{
//
//    private ImageView backgroundImage;
//    private int lastTopValueAssigned = 0;
//    private ListView mListView;
//    private Drawable mActionBarBackgroundDrawable;
//    private Toolbar mToolbar;
//    private int mStatusHeight;
//
//    ListView mListView2;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sports_team_activity);
//
//        mToolbar = (Toolbar)findViewById(R.id.sports_team_toolbar);
//        setSupportActionBar(mToolbar);
//        mActionBarBackgroundDrawable = mToolbar.getBackground();
//        mActionBarBackgroundDrawable.setAlpha(0);
//
//        getSupportActionBar().setDisplayUseLogoEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        inflateHeader();
//    }
//
//    @Override
//    public void onScrollStateChanged(AbsListView absListView, int i) {}
//
//    @Override
//    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
//
//
//        parallaxImage(backgroundImage);
//    }
//
//    private void parallaxImage(View view) {
//        if(view != null)
//        {
//            int headerHeight = backgroundImage.getHeight();
//            float ratio = 0;
//
//            Rect rect = new Rect();
//            view.getLocalVisibleRect(rect);
//            if (lastTopValueAssigned != rect.top) {
//                lastTopValueAssigned = rect.top;
//                float headerTop = (float)(rect.top / 2);
//                view.setY(headerTop);
//
//                float viewTop = rect.top * 2;
//
//                if (viewTop > 0 && headerHeight > 0)
//                    ratio = (float) Math.min(Math.max(viewTop, 0), headerHeight) / headerHeight;
//                int newAlpha = (int) (ratio * 255);
//
//                mActionBarBackgroundDrawable.setAlpha(newAlpha);
//                mToolbar.setBackground(mActionBarBackgroundDrawable);
//            }
//        }
//    }
//
//    private void inflateHeader() {
//        mListView = (ListView)findViewById(R.id.sports_team_main);
//        mListView.setOnScrollListener(this);
//
//        LinearLayout root = (LinearLayout)findViewById(R.id.sports_team_layout);
//        root.post(new Runnable() {
//            public void run() {
//                setListAdapter(getScreenAvailableHeight());
//            }
//        });
//
//        ViewGroup header = (ViewGroup)getLayoutInflater().inflate(R.layout.sports_team_header, mListView, false);
//        mListView.addHeaderView(header, null, false);
//
//        // we take the background image and button reference from the header
////        backgroundImage = (ImageView) header.findViewById(R.id.teamHeaderBackground);
//    }
//
//    private void setListAdapter(int screenHeight)
//    {
//        ArrayList<String> contents = new ArrayList<String>();
//        contents.add("content");
//        mListView.setAdapter(new SportsTeamContentAdapter(this, contents, screenHeight));
//    }
//
//    private int getScreenAvailableHeight()
//    {
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//
//        Rect rect = new Rect();
//        Window win = getWindow();
//        win.getDecorView().getWindowVisibleDisplayFrame(rect);
//        mStatusHeight = rect.top;
////        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
////        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
////        styledAttributes.recycle();
//        int screenSize = size.y;
//
//        int toolbarHeight = mToolbar.getHeight();
//        int height = screenSize - toolbarHeight - mStatusHeight;
//
//        return height;
//    }
//
//    class SportsTeamContentAdapter extends BaseAdapter {
//
//        private List<String> content;
//        private LayoutInflater inflater;
//        private int screenHeight;
//        public SportsTeamContentAdapter(Context context, ArrayList<String> contents, int screenHeight) {
//            content = contents;
//            inflater = LayoutInflater.from(context);
//            this.screenHeight = screenHeight;
//        }
//
//        @Override
//        public int getCount() {
//            return content.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return content.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            // Check if an existing view is being reused, otherwise inflate the view
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.sports_team_content, parent, false);
//            }
//            ViewPager mViewPager = (ViewPager)convertView.findViewById(R.id.sportsTeamPager);
//            mViewPager.setAdapter(new SamplePagerAdapter());
//            SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout)convertView.findViewById(R.id.sliding_tabs);
//            mSlidingTabLayout.setViewPager(mViewPager);
//
//            convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, screenHeight));
//            // Return the completed view to render on screen
//            return convertView;
//        }
//    }
//
//    /**
//     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
//     * The individual pages are simple and just display two lines of text. The important section of
//     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
//     * {@link com.ludicrus.ludicrus.library.view.SlidingTabLayout}.
//     */
//    class SamplePagerAdapter extends PagerAdapter {
//
//        /**
//         * @return the number of pages to display
//         */
//        @Override
//        public int getCount() {
//            return 2;
//        }
//
//        /**
//         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
//         * same object as the {@link View} added to the {@link android.support.v4.view.ViewPager}.
//         */
//        @Override
//        public boolean isViewFromObject(View view, Object o) {
//            boolean isVFromO = (o == view);
//            return isVFromO;
//        }
//
//        // BEGIN_INCLUDE (pageradapter_getpagetitle)
//        /**
//         * Return the title of the item at {@code position}. This is important as what this method
//         * returns is what is displayed in the {@link com.ludicrus.ludicrus.library.view.SlidingTabLayout}.
//         * <p>
//         * Here we construct one using the position value, but for real application the title should
//         * refer to the item's contents.
//         */
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Item " + (position + 1);
//        }
//        // END_INCLUDE (pageradapter_getpagetitle)
//
//        /**
//         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
//         * inflate a layout from the apps resources and then change the text view to signify the position.
//         */
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            // Inflate a new layout from our resources
//            View view = getLayoutInflater().inflate(R.layout.fragment_slide_page, container, false);
//            container.addView(view);
//
//            MatchAdapter mMatchAdapter = new MatchAdapter(getLayoutInflater());
//
//            List<IMatch> matchList = ScoresPagerFragment.matchList.get("2014-09-13");
//            for (int i = 0; i < matchList.size(); i++) {
//                IMatch match = matchList.get(i);
//                mMatchAdapter.addItem(match);
//            }
//
//            mListView2 = new ListView(container.getContext());
//            mListView2.setAdapter(mMatchAdapter);
//            mListView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            SwipeRefreshLayout layout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_layout);
//            layout.addView(mListView2);
//            //Return the View
//            return view;
//        }
//
//        /**
//         * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
//         * {@link View}.
//         */
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//            Log.i("", "destroyItem() [position: " + position + "]");
//        }
//
//    }
//}

