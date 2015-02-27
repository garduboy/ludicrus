package com.ludicrus.ludicrus.views.profile;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.interfaces.EventListener;
import com.viewpagerindicator.TitlePageIndicator;

public class ProfileFragment extends Fragment implements EventListener{

	private JSONObject result;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{	
        return inflater.inflate(R.layout.user_profile, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
        SportifiedApp sportApp = (SportifiedApp)getActivity().getApplicationContext();
        TextView username = (TextView)getView().findViewById(R.id.username);
        username.setText(sportApp.getUser().getName());
	}
	
	public void setJSONObject(JSONObject json)
    {
    	try
    	{
	    	result = json;
	    	if(result == null)
	    		return;
	    	String resultInfo = (String)result.get("resultInfo");
	    	if(resultInfo != null)
	    	{
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
