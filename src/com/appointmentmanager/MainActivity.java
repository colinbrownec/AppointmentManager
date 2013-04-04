package com.appointmentmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity{

	static Context context;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		//MISC
		context = this.getApplicationContext();
//
//		Intent nextScreen = new Intent(context, StaffTimeList.class);
//		startActivity(nextScreen);

		//		Log.v("progress","ZZZZZZZZZZZZZZZZZZZZZZ REAL XXXXXXXXXXXXXXXXXXXXXX");
	}

	public void onStart() {
		super.onStart();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void goSchedule(View v) {
		Intent nextScreen = new Intent(context, StaffTimeList.class);
		startActivity(nextScreen);
	}

	public void goNothing(View v) {

	}

	public int toDP(int px)
	{ 	return (int)(px/context.getResources().getDisplayMetrics().density); }

	private int toPX(int dp)
	{ 	return (int)(dp*context.getResources().getDisplayMetrics().density); }

	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }
}


