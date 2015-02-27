package com.ludicrus.ludicrus.login;

import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.helpers.ActivityHelper;
import com.ludicrus.ludicrus.helpers.Hash;
import com.ludicrus.ludicrus.helpers.RestClientHelper;
import com.ludicrus.ludicrus.interfaces.EventListener;

public class Login extends Activity implements EventListener 
{

	private JSONObject result;
	private String userName;
	private String password;
	
	private ProgressBar progressBar;
	
	static final int DIALOG_NO_NETWORK_ID = 1;
	static final int DIALOG_LOGIN_FAILED_ID = 2;
	
	private UiLifecycleHelper uiHelper;
	private boolean isResumed = false;
	
	
	/*********************START OF FACEBOOK FUNCTIONS *******************/
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void getFBUserInfoRequest(final Session session) {
		final EventListener eventListener = this;
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user == null)
	                {
	                	//Handle error
	                }
	                else
	                {
	                	RestClientHelper.loginFBUser(user, eventListener);
//	                	SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
//	                	sportApp.logFBUser(user);
//	                	
//	                	Intent intent = ActivityHelper.startMainActivity(getBaseContext());
//	        	    	startActivity(intent);
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if(isResumed)
		{
		    if (state.isOpened())
		    {
		    	progressBar.setVisibility(View.VISIBLE);
		    	getFBUserInfoRequest(session);
		    }
		    else if (state.isClosed()) {
		    	System.out.println("FB State CLosed");
		    }
		}
	}
	/*******************END OF FACEBOOK FUNCTIONS***********************/
	
	/** Called when the user clicks the Login button */
	public void login(View view) {
	    // 
		EditText editText;
		editText = (EditText) findViewById(R.id.username);
		userName = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password);
		password = editText.getText().toString();
		
		try
		{
			progressBar.setVisibility(View.VISIBLE);
			
			password = Hash.hash(password);
	    	
			RestClientHelper.login(userName, password, this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/** Called when the user clicks the Skip button */
	public void skipLogin(View view) {
		try
		{
			progressBar.setVisibility(View.VISIBLE);
			RestClientHelper.logGuest(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
//	
//	@Override
//    public void onBackPressed()
//	{
//		this.finish();
//	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        
        Typeface lobsterTypeFace = Typeface.createFromAsset(getAssets(),"fonts/LobsterTwo-Regular.ttf");
        Typeface quickTypeFace = Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Regular.ttf");
        
        EditText userName = (EditText)findViewById(R.id.username);
        userName.setTypeface(quickTypeFace);
        EditText password = (EditText)findViewById(R.id.password);
        password.setTypeface(quickTypeFace);
        Button signInButton = (Button)findViewById(R.id.login);
        signInButton.setTypeface(lobsterTypeFace);
        Button skipButton = (Button)findViewById(R.id.skipLogin);
        skipButton.setTypeface(lobsterTypeFace);
        
        LoginButton authButton = (LoginButton) findViewById(R.id.fbLogin);
        authButton.setBackgroundResource(R.drawable.fb);
        authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday","email"));
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        
    }

    @Override
    public void onResume()
    {
    	super.onResume();
    	SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
		if(sportApp.isUserLogged())
		{
			this.finish();
			return;
		}
    	isResumed = true;
    	uiHelper.onResume();
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	isResumed = false;
    	uiHelper.onPause();
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	uiHelper.onDestroy();
    }
    
    protected Dialog onCreateDialog(int id)
    {
    	AlertDialog alert;
        switch(id)
        {
	        case DIALOG_NO_NETWORK_ID:
	        	alert = null;
	            break;
	        case DIALOG_LOGIN_FAILED_ID:
	            AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("Incorrect user name and password combination")
	            .setCancelable(false)
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                     dialog.cancel();
	                }
	            });
	        	alert = builder.create();
	            break;
	        default:
	            alert = null;
        }
        return alert;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    
    private void processResult()
    {
    	try
    	{
    		progressBar.setVisibility(View.GONE);
    		if(result == null)
    		{
    			showDialog(DIALOG_LOGIN_FAILED_ID);
    			return;
    		}
    		SportifiedApp sportApp = (SportifiedApp)getApplicationContext();
    		sportApp.logUser(result);
    	    
		    Intent intent = ActivityHelper.startMainActivity(this);
	    	startActivity(intent);
    	}
    	catch(Exception e)
    	{
    		
    	}
    }
    
    public void setJSONObject(JSONObject json)
    {
    	result = json;
    	processResult();
    }
}
