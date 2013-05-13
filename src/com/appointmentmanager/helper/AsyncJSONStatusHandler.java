package com.appointmentmanager.helper;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.appointmentmanager.R;

public class AsyncJSONStatusHandler extends AsyncTask<Void, Void, HashMap<String, String>>{

	private String url;
	private Activity activity;
	private static Context context;
	private Button button;
	private Drawable accepted, pending, declined, searching;
	private Dialog dia_info;
	
	private final String BASE_REQUEST_URL = "http://ienvynails.com/appman/json/nextappointment.php?name=";
	//http://ienvynails.com/appman/json/nextappointment.php?name=Dr.BrightSwagger&phone=715-271-1913&debug

	public AsyncJSONStatusHandler(String url, Button button, Activity activity) {
		context = activity.getApplicationContext();
		this.url = url;
		this.button = button;
		this.activity = activity;
		
		accepted = context.getResources().getDrawable(R.drawable.l_rating_good);
		pending = context.getResources().getDrawable(R.drawable.l_action_help);
		declined = context.getResources().getDrawable(R.drawable.l_rating_bad);
	}

	protected HashMap<String, String> doInBackground(Void... params) {
		url = "http://ienvynails.com/appman/json/nextappointment.php?name=Dr.BrightSwagger&phone=715-271-1913";
		HashMap<String, String> data = new HashMap<String, String> ();

		//hollistic
		JSONArray dataArray = null;
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(url);
		try {
			data.put("STATUS", json.getString("status"));
		} catch (JSONException e) {
			Log.v("progress", "JSON EXCEPTION");
			e.printStackTrace();
		}
		
		return data;
	}
	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		if ( (result.get("STATUS")).toLowerCase().equals("pending") ) {
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, pending, null);
		} else if ( (result.get("STATUS")).toLowerCase().equals("accepted") ) {
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, accepted, null);
		} else if ( (result.get("STATUS")).toLowerCase().equals("declined") ) {
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, declined, null);
		}
			
	}
	
	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }
	
	private void SaveInfo(String area, String saveTag, String value){
		SharedPreferences sharedPreferences = context.getSharedPreferences(area, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(saveTag, value);
		editor.commit();
	}

	private String GetInfo(String tag, String which)
	{	
		SharedPreferences sharedPreferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
		String savedStr = sharedPreferences.getString(which, "");
		return savedStr;
	}	  	

}
