package com.appointmentmanager.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncJSONEmployeeLoader extends AsyncTask<Void, Void, HashMap<String, Object>>{

	private String url;

	public AsyncJSONEmployeeLoader(String url) {
//		Log.w("progress", "Async onstart URL");
		this.url = url;
	}
	
	protected HashMap<String, Object> doInBackground(Void... params) {
		
		JSONArray dataArray = null;
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(url);
		
		HashMap<String, Object> data = new HashMap<String, Object> ();
		
		try {
			dataArray = json.getJSONArray("employees");
			
			JSONObject c;
			
			String[] idList = new String[dataArray.length()];
			String[] nameList = new String[dataArray.length()];
			String[] portraitList = new String[dataArray.length()];
			
			for (int i=0; i<dataArray.length();i++ ) {
				c = dataArray.getJSONObject(i);
				idList[i] = c.getString("id");
				portraitList[i] = c.getString("portrait");
				nameList[i] = c.getString("name");
			}
			
			data.put("staffAmount", dataArray.length());
			data.put("idList", idList);
			data.put("nameList", nameList);
			data.put("portraitList", portraitList);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	@Override
	protected void onPostExecute(HashMap<String, Object> result) {
//		Log.w("progress", "Async URL post execute");
	}

}
