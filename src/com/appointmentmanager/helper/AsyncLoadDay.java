package com.appointmentmanager.helper;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class AsyncLoadDay extends AsyncTask<Void, Void, Void>{

	private LinearLayout staffList;
	private ProgressBar pb;
	private String currentDay;

	public  AsyncLoadDay(String currentDay, 
			LinearLayout staffList) {//, ProgressBar pb) {
		Log.i("progress", "Async LoadDay onstart");
		this.currentDay = currentDay;
		this.staffList = staffList;
//		this.pb = pb;
	}
	
	protected Void doInBackground(Void... params) {
		
//		for (Button button:MainActivity.butt_day) {
//			if ( button != ((Button)v) )
//				button.setTextColor(context.getResources().
//						getColor(R.color.black5));
//		}
//
//		currentDay = ((Button)v).getText().toString();
//		((Button)v).setTextColor(context.getResources().
//				getColor(R.color.dark_blue));

//		staffList.removeAllViews();
//		loadDay(currentDay);
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		Log.w("progress", "Async post execute");
		staffList.removeAllViews();
//		pb.setVisibility(View.GONE);
//		staffList.setVisibility(View.VISIBLE);
//		iv.setImageBitmap(result);
	}

}
