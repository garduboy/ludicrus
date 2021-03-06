package com.ludicrus.ludicrus.views.profile;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.core.util.EnumSportItemType;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.classes.AndroidOrganization;
import com.ludicrus.ludicrus.helpers.network.RestClientHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.ludicrus.ludicrus.parcelable.UserMobile;
import com.ludicrus.ludicrus.util.OrganizationAdapter;
import com.ludicrus.ludicrus.util.OrganizationAdapter.OrgHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddFavoriteTeamFragment extends Fragment implements EventListener{

	private JSONObject result;
	private boolean selectionBarVisible = false;
	private ArrayList<Integer> mAddedTeams;
	private ArrayList<Integer> mRemovedTeams;
	private ListView mTeamList;
	private String mConfederationID;
	private Button mConfederation;
	private Button mFederation;
	private Button mHome;
	private boolean misDirty = false;
	private View loadingPanel;
    private View mActionBarButtons;
    private OrganizationAdapter mOrgAdapter;
	
	final EventListener listener = this;

    View.OnClickListener favTeamsHomeListener = new View.OnClickListener() {
        public void onClick(View v)
        {
            displayConfederations(v);
        }
    };
    View.OnClickListener confederationListener = new View.OnClickListener() {
        public void onClick(View v)
        {
            displayFederations(v);
        }
    };
	View.OnClickListener cancelListener = new View.OnClickListener() {
		public void onClick(View v)
		{
			misDirty = false;
			displayFederations(v);
		}
	};
	View.OnClickListener doneListener = new View.OnClickListener() {
		public void onClick(View v)
		{
			ListAdapter adapter = mTeamList.getAdapter();
			if(misDirty)
			{
				SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
		        UserMobile user = sportApp.getUser();
				RestClientHelper.addFavoriteTeams(user.getIdUser(), mAddedTeams, mRemovedTeams, listener);
				loadingPanel.setVisibility(View.VISIBLE);
				mAddedTeams.clear();
				mRemovedTeams.clear();
			}
			else {
				displayFederations(v);
			}
			misDirty = false;
		}
	};
	
	private void setupSelectionActionBar()
	{
		mActionBarButtons = getActivity().getLayoutInflater().inflate(R.layout.cancel_done_action_bar, new LinearLayout(getActivity()), false);
		View cancelActionView = mActionBarButtons.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(cancelListener);
		View doneActionView = mActionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(doneListener);
		ActionBarActivity mainActivity = (ActionBarActivity)getActivity();
        Toolbar toolbar = (Toolbar)mainActivity.findViewById(R.id.aux_toolbar);
        toolbar.addView(mActionBarButtons, 0);
        toolbar.setNavigationIcon(null);
        //Before the toolbar, this is how I set the custom view. That same process for the toolbar needs review
//		mainActivity.getSupportActionBar().setCustomView(actionBarButtons);
//		mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}
	
	public void displayConfederations(View view)
	{
		RestClientHelper.getConfederationList(this);
		mHome.setVisibility(View.GONE);
		mConfederation.setVisibility(View.GONE);
		if(mFederation != null)
			mFederation.setVisibility(View.GONE);
    	mTeamList.setAdapter(null);
    	loadingPanel.setVisibility(View.VISIBLE);
    	if(selectionBarVisible)
    	{
    		ActionBarActivity mainActivity = (ActionBarActivity)getActivity();

            //There is not a lot of documentation regarding how to set the default toolbar view after a custom view
            //So far this solution works but I'm not sure how expensive it is.
            Toolbar toolbar = (Toolbar)mainActivity.findViewById(R.id.aux_toolbar);
            mainActivity.setSupportActionBar(toolbar);
            if(mActionBarButtons != null) {
                toolbar.removeView(mActionBarButtons);
            }

    		mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
    		selectionBarVisible = false;
    	}
	}
	
	public void displayFederations(View view)
	{
		RestClientHelper.getFederationList(mConfederationID, this);
		if(mFederation != null)
			mFederation.setVisibility(View.GONE);
    	mTeamList.setAdapter(null);
    	loadingPanel.setVisibility(View.VISIBLE);
    	if(selectionBarVisible)
    	{
    		ActionBarActivity mainActivity = (ActionBarActivity)getActivity();
            Toolbar toolbar = (Toolbar)mainActivity.findViewById(R.id.aux_toolbar);
            //There is not a lot of documentation regarding how to set the default toolbar view after a custom view
            //So far this solution works but I'm not sure how expensive it is.
            mainActivity.setSupportActionBar(toolbar);
            if(mActionBarButtons != null) {
                toolbar.removeView(mActionBarButtons);
            }

    		mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
    		selectionBarVisible = false;
    	}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        return inflater.inflate(R.layout.add_fav_team, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
        Button favTeamsHome = (Button) getActivity().findViewById(R.id.favTeamsHome);
        favTeamsHome.setOnClickListener(favTeamsHomeListener);
        Button confederation = (Button) getActivity().findViewById(R.id.confederation);
        confederation.setOnClickListener(confederationListener);

		//Setting the adapter dynamically
        RestClientHelper.getConfederationList(this);
        
		mTeamList = (ListView) getActivity().findViewById(R.id.teamList);
		mAddedTeams = new ArrayList<Integer>();
		mRemovedTeams = new ArrayList<Integer>();
	}
	
	private void processResult()
	{
		try
		{
			if(result == null)
			{
				return;
			}
			if(result.has("result"))
            {
				loadingPanel.setVisibility(View.GONE);
				if(result.get("result").equals("SUCCESS")) {
                    SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
                    UserMobile user = sportApp.getUser();
                    RestClientHelper.getUserFavTeams(user.getIdUser(), null);
					displayConfederations(this.getView());
				}
				return;
			}
            if(result.has("icons"))
            {

                JSONArray items = (JSONArray) result.get("icons");
                Integer itemType = (Integer) result.get("itemType");
                for (int i = 0; i < items.length(); i++)
                {
                    JSONObject obj = (JSONObject) items.get(i);
                    int orgId = obj.getInt("orgId");
                    for(int j = 0; j < mOrgAdapter.getCount(); j++)
                    {
                        AndroidOrganization organization = (AndroidOrganization)mOrgAdapter.getItem(j);
                        if(orgId == organization.getIdOrganization())
                        {
                            String logo = obj.getString("orgLogo");
                            organization.setLogo(logo);
                        }
                    }
                }
                setOrganizationAdapter(mOrgAdapter, itemType);
                mOrgAdapter = null;
            }
            else{
                JSONArray items = (JSONArray) result.get("itemList");
                Integer itemType = (Integer) result.get("itemType");
                ArrayList<String> orgsNoLogo = new ArrayList<String>();

                OrganizationAdapter adapter = new OrganizationAdapter((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), itemType, getActivity());
                IOrganization organization;
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = (JSONObject) items.get(i);
                    organization = new AndroidOrganization(obj);
                    adapter.addItem(organization);
                    if(!organization.hasLogo())
                    {
                        orgsNoLogo.add(organization.getIdOrganization() + ";" + organization.getOrgType());
                    }
                }

                if(orgsNoLogo.size() > 0)
                {
                    RestClientHelper.getOrganizationIcons(this, orgsNoLogo, itemType);
                    mOrgAdapter = adapter;
                } else
                {
                    setOrganizationAdapter(adapter, itemType);
                }
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    private void setOrganizationAdapter(OrganizationAdapter adapter, Integer itemType)
    {
        try
        {
            AdapterView.OnItemClickListener clickListener = null;
            loadingPanel = getActivity().findViewById(R.id.loadingPanel);
            SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
            final UserMobile user = sportApp.getUser();
            if(itemType == EnumSportItemType.CONFEDERATION)
            {
                clickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        OrgHolder holder = (OrgHolder)v.getTag();
                        //Setting the adapter dynamically
                        mConfederationID = holder.organizationId;
                        RestClientHelper.getFederationList(mConfederationID, listener);

                        mHome = (Button) getActivity().findViewById(R.id.favTeamsHome);
                        mConfederation = (Button) getActivity().findViewById(R.id.confederation);
                        RelativeLayout l = (RelativeLayout)v;
                        ImageView i = (ImageView)l.getChildAt(0);
                        TextView t = (TextView)l.getChildAt(1);
                        mConfederation.setText(t.getText());
                        mConfederation.setCompoundDrawablesWithIntrinsicBounds(i.getDrawable(), null, null, null);
                        mHome.setVisibility(View.VISIBLE);
                        mConfederation.setVisibility(View.VISIBLE);
                        mTeamList.setAdapter(null);
                        loadingPanel.setVisibility(View.VISIBLE);
                    }
                };
            } else if(itemType == EnumSportItemType.FEDERATION)
            {
                clickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        OrgHolder holder = (OrgHolder)v.getTag();
                        //Setting the adapter dynamically
                        RestClientHelper.getTeamsByFederation(user.getIdUser(), holder.organizationId, listener);

                        mFederation = (Button) getActivity().findViewById(R.id.federation);
                        RelativeLayout l = (RelativeLayout)v;
                        ImageView i = (ImageView)l.getChildAt(0);
                        TextView t = (TextView)l.getChildAt(1);
                        mFederation.setText(t.getText());
                        mFederation.setCompoundDrawablesWithIntrinsicBounds(i.getDrawable(), null, null, null);
                        mHome.setVisibility(View.VISIBLE);
                        mConfederation.setVisibility(View.VISIBLE);
                        mFederation.setVisibility(View.VISIBLE);
                        mTeamList.setAdapter(null);
                        loadingPanel.setVisibility(View.VISIBLE);
                        if (!selectionBarVisible)
                        {
                            setupSelectionActionBar();
                            selectionBarVisible = true;
                        }
                    }
                };
            } else if(itemType == EnumSportItemType.TEAM) {
                clickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        misDirty = true;
                        OrgHolder holder = (OrgHolder)v.getTag();
                        AndroidOrganization organization = null;
                        ListAdapter adapter = mTeamList.getAdapter();
                        for(int i = 0; i < adapter.getCount(); i++)
                        {
                            organization = (AndroidOrganization)adapter.getItem(i);
                            if(organization.getIdOrganization() == Integer.parseInt(holder.organizationId))
                            {
                                break;
                            }
                        }
                        if(organization != null) {
                            //Setting the adapter dynamically
                            if(organization.getIsFavorite())
                            {
                                mRemovedTeams.add(organization.getIdOrganization());
                                if(mAddedTeams.contains(organization.getIdOrganization()))
                                {
                                    mAddedTeams.remove(organization.getIdOrganization());
                                }
                                organization.setIsFavorite(false);
                                holder.favoriteOrg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_empty));
                            }
                            else
                            {
                                mAddedTeams.add(organization.getIdOrganization());
                                if(mRemovedTeams.contains(organization.getIdOrganization()))
                                {
                                    mRemovedTeams.remove(organization.getIdOrganization());
                                }
                                organization.setIsFavorite(true);
                                holder.favoriteOrg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.star_full));
                            }
                        }
                    }
                };
            }

            // Set the adapter for the list view
            mTeamList.setAdapter(adapter);

            mTeamList.setOnItemClickListener(clickListener);
            loadingPanel.setVisibility(View.GONE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	public void setJSONObject(JSONObject json)
    {
    	try
    	{
	    	result = json;
	    	processResult();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
