package com.appointmentmanager.helper;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appointmentmanager.MainActivity;
import com.appointmentmanager.StaffTimeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncJSONRequestSlot extends AsyncTask<Void, Void, HashMap<String, String>>{

	private String url;
	private ProgressDialog progressDialog;
	private HashMap<String, String> data;
	private Activity activity;

	public AsyncJSONRequestSlot(String url, Activity activity) {
		//		Log.w("progress", "Async onstart URL");
		this.activity = activity;
		this.url = url;
		data = new HashMap<String, String> ();
	}

	protected void onPreExecute() {
		progressDialog= ProgressDialog.show(activity, "Sending Request",
				"Virtual Handshaking going on...", true);
		//do initialization of required objects objects here                
	}

	protected HashMap<String, String> doInBackground(Void... params) {

		//hollistic
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(url);
		try {
			data.put("REQUEST_RESULT", json.getString("result"));
//			data.put("RESULT_REASON", json.getString("closing time"));
		} catch (JSONException e) {
			Log.v("progress", "JSON EXCEPTION");
			e.printStackTrace();
		}
		
		return data;
	}
	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		progressDialog.cancel();
		Toast.makeText(activity, "Booking Submitted", Toast.LENGTH_LONG).show(); 
		
		StaffTimeList.reloadDay("2013", "05", "08");
		
		for (Entry<String, String> s:result.entrySet()) {
			Log.i("progress", ((Entry<String, String>) s).getKey() + ": " + ((Entry<String, String>) s).getValue());
		}
	}

}
