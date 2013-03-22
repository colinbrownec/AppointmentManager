package com.appointmentmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends Activity {


	String[] dayList, nameList;
	Button[] butt_day;
	LinearLayout[] lin_staff;
	static Context context;

	ImageView iv_portrait;

	int screenWidth, screenHeight, staffAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Log.v("progress","ZZZZZZZZZZZZZZZZZZZZZZ REAL");

		context = this.getApplicationContext();

		//numStaff = db.getStaffNumber();
		final int PORTRAIT_SIZE = 56;
		staffAmount = 7;

		dayList = new String[7];
		butt_day = new Button[7];
		lin_staff = new LinearLayout[staffAmount];
		//
		DisplayMetrics metrics =  new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		LinearLayout lin_staffList = (LinearLayout) findViewById(R.id.lin_staff_list);

		final int[] portaitID = new int[staffAmount];
		portaitID[0] = R.drawable.social_person;
		portaitID[1] = R.drawable.staff1;
		portaitID[2] = R.drawable.staff2;
		portaitID[3] = R.drawable.staff3;
		portaitID[4] = R.drawable.staff4;
		portaitID[5] = R.drawable.staff5;
		portaitID[6] = R.drawable.staff6;

		iv_portrait = (ImageView) findViewById(R.id.test);

		// initialize parse
		Parse.initialize(this, "j1KIXfxXQTWXP7B5wa3hOwffvrzmRDBT5EFZ0fhc", "sFOlebSAj3I1eyOG2M4L8TLDjFxyqf8KjnOesoWE");
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);

		ParseAnalytics.trackAppOpened(getIntent());

		getParseInformation task = new getParseInformation(iv_portrait);
		task.execute();

		//Create staff linear layout
		LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.FILL_PARENT);
		lps.setMargins(toPX(8), toPX(8), 0, 0);
		for (int i=0; i<staffAmount;i++) {
			lin_staff[i] = new LinearLayout(this);
			lin_staff[i].setLayoutParams(lps);
			lin_staff[i].setOrientation(1);
			lin_staffList.addView(lin_staff[i]);

			ImageView portrait = new ImageView(this);
			portrait.setImageResource(portaitID[i]);  
			portrait.setLayoutParams(new LinearLayout.
					LayoutParams(toPX(PORTRAIT_SIZE), toPX(PORTRAIT_SIZE)));
			lin_staff[i].addView(portrait);
		}

		ArrayList<ArrayList<String>> staffTimeList = 
				new ArrayList<ArrayList<String>> (staffAmount);

		for (int i=0; i<staffAmount;i++)
			staffTimeList.add(new ArrayList<String> ());


		staffTimeList.get(0).add("0938:1141");
		//staffTimeList.get(0).add("1200:1300");
		//staffTimeList.get(0).add("1535:1700");

		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		for (int i=1; i<staffAmount;i++) {
			addBlock(i*(screenHeight-toPX(108)-toPX(56))/9, 0xAA6DDE3B, lin_staff[i]);
		}

		butt_day[0] = (Button) findViewById(R.id.butt_day0);
		butt_day[1] = (Button) findViewById(R.id.butt_day1);
		butt_day[2] = (Button) findViewById(R.id.butt_day2);
		butt_day[3] = (Button) findViewById(R.id.butt_day3);
		butt_day[4] = (Button) findViewById(R.id.butt_day4);
		butt_day[5] = (Button) findViewById(R.id.butt_day5);
		butt_day[6] = (Button) findViewById(R.id.butt_day6);

		String[] daysOfWeek = { "MONDAY", "TUESDAY", "WEDNESDAY",
				"THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

		String dayOfTheWeek = new SimpleDateFormat("EEEE").format(new Date());

		for (int i=0; i<daysOfWeek.length;i++) {
			if (dayOfTheWeek.toLowerCase().equals(daysOfWeek[i].toLowerCase())) {
				for(int j=0; j<daysOfWeek.length;j++) {
					i=i%daysOfWeek.length;
					dayList[j] = daysOfWeek[i++];
				}
				break;
			}
		}

		for (int i=0; i<dayList.length;i++)
			butt_day[i].setText(dayList[i]);

		displayTime(staffTimeList.get(0));

	}

	public void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void displayTime(ArrayList<String> timeList) {
		//Start time: 9:00 AM
		//End time: 5:00 PM
		//1 hour = screenHeight/8

		int ASH = screenHeight-toPX(108)-toPX(56);

		int prevTime = 0;
		double timePeriod;
		String minutes = null;
		String[] tempString = null;
		if (timeList == null || timeList.size() == 0) return;
		for (int i=0; i<timeList.size();i++) {
			//			if (prevTime == 0){
			//				
			//				Log.i("progress", ""+tempString);
			//				tempString = timeList.get(i).split(":");
			//				timePeriod = Integer.parseInt(tempString[1])-Integer.parseInt(tempString[0]);
			//
			//				addBlock((int)(timePeriod*(ASH/800.0)), 0xAA6DDE3B, lin_staff[0]);
			//
			//				toast("" + timePeriod);
			//			}
			//			else{
			tempString = timeList.get(i).split(":");


			timeCalc(timeList.get(i));
			//convert into 100 for minutes
			minutes = tempString[0].substring(tempString[0].length()-2);
			if (!minutes.equals("00")) 
				tempString[0] = tempString[0].substring(0,tempString[0].length()-2) 
				+ 1.667*(Integer.parseInt(minutes));

			minutes = tempString[1].substring(tempString[1].length()-2);
			if (!minutes.equals("00")) 
				tempString[1] = tempString[1].substring(0,tempString[1].length()-2) 
				+ 1.667*(Integer.parseInt(minutes));

			timePeriod = Double.parseDouble(tempString[1]) - 
					Double.parseDouble(tempString[0]);

			addBlock((int)(timePeriod*(ASH/800)), 0xAA6DDE3B, lin_staff[0]);

			//			}
		}	
	}

	@SuppressWarnings("deprecation")
	public void addBlock(int height, int color, LinearLayout linearLayout) {
		View box = new View(this);
		box.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, height));
		box.setBackgroundColor(color);
		linearLayout.addView(box);
	}

	public int timeCalc(String s) {
		//100  = 1 hour
		String times[] = s.split(":");
		double tempMin, tempHour, ret;
		String minutes0, minutes1, hours0, hours1;

		minutes0 = times[0].substring(times[0].length()-2);
		minutes1 = times[1].substring(times[1].length()-2);

		hours0 = times[0].substring(0,2);
		hours1 = times[1].substring(0,2);

		tempMin = (Double.parseDouble(minutes1)-Double.parseDouble(minutes0))*(5/3.0);
		tempHour = Double.parseDouble(hours1) - Double.parseDouble(hours0);

		ret = Double.parseDouble(hours1)*100+Double.parseDouble(minutes1);

		//staffTimeList.get(0).add("0938:1141");

		Log.i("progress", "minutes0: "+ minutes0);
		Log.i("progress", "minutes0: "+ minutes0);
		Log.i("progress", "hours1: "+ Double.parseDouble(hours1)*100+Double.parseDouble(minutes1));
		Log.i("progress", "hours0: "+ Double.parseDouble(hours0)*100+Double.parseDouble(minutes0));

		//		if (!minutes.equals("00")) 
		//			times[0] = times[0].substring(0,times[0].length()-2) 
		//					+ 1.667*(Integer.parseInt(minutes));

		//		toast( ""+tempString[0]);
		//		
		//		minutes = tempString[1].substring(tempString[1].length()-2);
		//		if (!minutes.equals("00")) 
		//			tempString[1] = tempString[1].substring(0,tempString[1].length()-2) 
		//					+ 1.667*(Integer.parseInt(minutes));
		//		
		//		timePeriod = Double.parseDouble(tempString[1]) - 
		//				Double.parseDouble(tempString[0]);
		//
		//		addBlock((int)(timePeriod*(ASH/800)), 0xAA6DDE3B, lin_staff[0]);
		return 0;
	}

	public void goChooseWeek(View v) {
		//sweg
	}

	public void goMakeReservation(View v) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:952.905.4298" ));
		startActivity(intent);
	}

	public void goChooseDay(View v) {
		//
	}

	public int toDP(int px)
	{ 	return (int)(px/this.context.getResources().getDisplayMetrics().density); }

	private int toPX(int dp)
	{ 	return (int)(dp*this.context.getResources().getDisplayMetrics().density); }

	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }
}


