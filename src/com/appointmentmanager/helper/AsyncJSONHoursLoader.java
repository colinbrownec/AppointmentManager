package com.appointmentmanager.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class AsyncJSONHoursLoader extends AsyncTask<Void, Void, HashMap<String, Object>>{

	private String url;
	private final int START_TIME = 950; //mod 100 adjusted
	private final int END_TIME= 1850; //mod 100 adjusted

	public AsyncJSONHoursLoader(String url) {
		//		Log.e("progress", "Async onstart URL");
		this.url = url;
//		this.pb = pb;
	}

	protected HashMap<String, Object> doInBackground(Void... params) {

		JSONArray dataArray = null;
		JSONArray tempHoursArray = null;
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(url);

		HashMap<String, Object> data = new HashMap<String, Object> ();

		try {
			dataArray = json.getJSONArray("employees");

			JSONObject c, b;
			
			//Independent of shifts
			String[] idList = new String[dataArray.length()];
			
			//Shift dependent
			ArrayList<String> tempListedTimes;		
			ArrayList<Integer> tempTimeStack;
			
			String[] startArr, endArr; //temps #swag
			int startTime, endTime, prevEndTime;
			for (int i=0; i<dataArray.length();i++ ) {
				c = dataArray.getJSONObject(i);
				tempHoursArray = dataArray.getJSONObject(i).getJSONArray("hours");

				tempTimeStack =  new ArrayList<Integer> ();
				tempListedTimes = new ArrayList<String> ();
				
				prevEndTime = START_TIME;
				for (int j=0;j<tempHoursArray.length(); j++) {
					b = tempHoursArray.getJSONObject(j);

					startArr = b.getString("start").split(":");
					endArr = b.getString("end").split(":");

					endTime = Integer.parseInt(endArr[0]+mod100(endArr[1]));
					startTime = Integer.parseInt(startArr[0]+mod100(startArr[1]));
					
					//Add string lables for times
					tempListedTimes.add(mod12(startArr[0])+":"+startArr[1] 
							+ "_" + mod12(endArr[0])+":"+endArr[1]);
					
					//break work, break work, break work;
					tempTimeStack.add(startTime-prevEndTime);
					tempTimeStack.add(endTime-startTime);
					
					prevEndTime = endTime;
//					Log.e("progress", ""+tempTimeStack.get(tempTimeStack.size()-1));
				}
				tempTimeStack.add(END_TIME - prevEndTime);
				
				idList[i] = c.getString("id");
				data.put(c.getString("id")+"_timeStack",tempTimeStack);
				data.put(c.getString("id")+"_listedTimes",tempListedTimes);
				
			}
			
			data.put("idList", idList);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	@Override
	protected void onPostExecute(HashMap<String, Object> result) {
		//		Log.w("progress", "Async URL post execute");
	}

	public String mod100(String s) {
		int temp = (int)( (5/3.0)*(Integer.parseInt(s)) );
		return (temp<10)? "0"+temp : ""+temp;
	}
	
	public String mod12(String s) {
		int temp = Integer.parseInt(s);		
		return (temp>=13)? ""+(temp-12):""+temp; 
	}

}
