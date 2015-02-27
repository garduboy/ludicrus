package com.ludicrus.ludicrus.helpers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.ludicrus.ludicrus.interfaces.EventListener;

import android.os.AsyncTask;

public class RestClient extends AsyncTask<Void, Void, JSONObject>
{

    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;

    private EventListener listener;
    
    private int method;
    
    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url, int method)
    {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        this.method = method;
    }

    public void AddParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void setMethod(int method)
    {
    	this.method = method;
    }
    
    @Override
    protected JSONObject doInBackground(Void... param)
    {
    	JSONObject json = null;
    	try
    	{
	        switch(method) {
	            case 0: //GET
	            {
	                //add parameters
	                String combinedParams = "";
	                if(!params.isEmpty()){
	                    combinedParams += "?";
	                    for(NameValuePair p : params)
	                    {
	                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
	                        if(combinedParams.length() > 1)
	                        {
	                            combinedParams  +=  "&" + paramString;
	                        }
	                        else
	                        {
	                            combinedParams += paramString;
	                        }
	                    }
	                }
	
	                HttpGet request = new HttpGet(url + combinedParams);
	
	                //add headers
	                for(NameValuePair h : headers)
	                {
	                    request.addHeader(h.getName(), h.getValue());
	                }
	
	                json = executeRequest(request, url);
	                break;
	            }
	            case 1: //POST:
	            {
	                HttpPost request = new HttpPost(url);
	
	                //add headers
	                for(NameValuePair h : headers)
	                {
	                    request.addHeader(h.getName(), h.getValue());
	                }
	
	                if(!params.isEmpty()){
	                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	                }
	
	                json = executeRequest(request, url);
	                break;
	            }
	        }
    	}
    	catch(Exception e)
    	{
    		
    	}
    	return json;
    }

    private JSONObject executeRequest(HttpUriRequest request, String url)
    {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

//            	String data = EntityUtils.toString(entity);
//            	 
//                JSONObject json = new JSONObject(data);
                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // A Simple JSONObject Creation
                JSONObject json=new JSONObject(response);
                
                // Closing the input stream will trigger connection release
                instream.close();
                
                return json;
            }

        }
        catch (ClientProtocolException e)
        {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        catch (Exception e)
        {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream is)
    {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    @Override
    protected void onPostExecute(JSONObject result) {
    	// TODO Auto-generated method stub
    	super.onPostExecute(result);
    	listener.setJSONObject(result);
    }
    
    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}