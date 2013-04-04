package com.appointmentmanager;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appointmentmanager.adapter.CalendarAdapter;
import com.appointmentmanager.helper.AsyncJSONEmployeeLoader;
import com.appointmentmanager.helper.AsyncJSONHoursLoader;
import com.appointmentmanager.helper.AsyncLoadImage;

public class StaffTimeList extends Activity{

	///////////////////////////////////////////////////
	final int PORTRAIT_SIZE = 56;
	final int MARGIN = 8;
	final int CALENDER_VIEW = 1;
	final String BASE_DAY_URL = "http://ienvynails.com/appman/json/hours.php?date=";
	///////////////////////////////////////////////////
	
	//Employee Data Lists:
	String[] nameList, urlList, idList;
	
	String[] dayList;
	String currentDay, today;

	Button[] butt_day;

	LinearLayout lin_staffList;
	LinearLayout[] lin_staff;
	
	ArrayList<ArrayList<RelativeLayout>> staffWorkBlock;
	RelativeLayout.LayoutParams lp_topRelative;
	RelativeLayout.LayoutParams lp_bottomRelative;

	ImageView[] iv_portrait;
	public static Bitmap[] portraitBitmap;

	Dialog dia_calendar;

	HorizontalScrollView hsv_employeeList;
	
	int screenWidth, screenHeight, staffAmount, totalStaffAmount;
	int selectedMonth, selectedYear;
	
	static Context context;
	Vibrator vibrator;
	ProgressBar pb_staffMain;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_staff_time_list);

		Log.v("progress","ZZZZZZZZZZZZZZZZZZZZZZ REAL XXXXXXXXXXXXXXXXXXXXXX");

		//MISC
		context = this.getApplicationContext();
		lin_staffList = (LinearLayout) findViewById(R.id.lin_staff_list);
		selectedMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
		selectedYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
		hsv_employeeList = (HorizontalScrollView) findViewById(R.id.hsv_employeeList);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		lp_topRelative = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp_topRelative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp_topRelative.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		
		lp_bottomRelative = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp_bottomRelative.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp_bottomRelative.addRule(RelativeLayout.CENTER_IN_PARENT, -1);

		//Day menu top
		dayList = new String[7];
		butt_day = new Button[7];
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
				today = currentDay = daysOfWeek[i];
				for(int j=0; j<daysOfWeek.length;j++) {
					i=i%daysOfWeek.length;
					dayList[j] = daysOfWeek[i++];
				}
				break;
			}
		}
		for (int i=0; i<dayList.length;i++)
			butt_day[i].setText(dayList[i]);

		//Display  metrics for screen size
		DisplayMetrics metrics =  new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		
		//JSON PARSING
		final String JSONURL = "http://ienvynails.com/appman/json/employees.php";
		try {
			HashMap<String, Object>employeeMap 
				= new AsyncJSONEmployeeLoader(JSONURL).execute().get();

			totalStaffAmount = (Integer) employeeMap.get("staffAmount");

			idList =  (String[]) employeeMap.get("idList");
			nameList = (String[]) employeeMap.get("nameList");
			urlList = (String[]) employeeMap.get("portraitList");
						
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		
		iv_portrait = new ImageView[totalStaffAmount];
		portraitBitmap = new Bitmap[totalStaffAmount];
		
		staffWorkBlock = new ArrayList<ArrayList<RelativeLayout>>(totalStaffAmount);
		for (int i=0; i<totalStaffAmount; i++)
			staffWorkBlock.add(new ArrayList<RelativeLayout> (10));
//		loadDay(currentDay);
	}

	public void onStart() {
		super.onStart();
//		loadDay("https://mywebspace.wisc.edu/kkxu/2012-03-27");
		loadDay("http://ienvynails.com/appman/json/hours.php?date=2013-04-02");
	}

	@SuppressWarnings("unchecked")
	public void loadDay(String day) {
		Log.w("progress", "XXXXXXXXXXXXXXXXXXXXXXXX NEW LOAD DAY ZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		
		lin_staffList.removeAllViewsInLayout();
		
		String JSON_HOURS_URL = day;
		HashMap<String, Object> hourMap = null;
		String[] idListThisDay = null;
		String[] urlListThisDay = null;
		int[] matchedIndex = null;
		
		try {
			
			hourMap = new AsyncJSONHoursLoader(JSON_HOURS_URL).execute().get();
			idListThisDay = (String[]) hourMap.get("idList");
			staffAmount = idListThisDay.length;
			
			//get URLs for thisDay's id's
			matchedIndex = new int[staffAmount];
			urlListThisDay = new String[staffAmount];
			for (int j=0; j<staffAmount; j++) {
				for (int k=0; k<idList.length; k++) {
					if (idListThisDay[j] == idList[k]) {
						urlListThisDay[j] = urlList[k];
						matchedIndex[j] = k;
//						Log.e("progress", "J:"+j + " K:"+urlListThisDay[j]);
						break;
					}
				}
			}

		} catch (InterruptedException e) {e.printStackTrace();
		} catch (ExecutionException e) {e.printStackTrace();
		};

		lin_staff = new LinearLayout[staffAmount];
		final ProgressBar[] pb_staff = new ProgressBar[staffAmount];
		final ImageView iv_tempPortrait[] = new ImageView[staffAmount];

		//Create staff linear layout
		LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
				toPX(PORTRAIT_SIZE + MARGIN), LinearLayout.LayoutParams.FILL_PARENT);
		
		for (int i=0; i<staffAmount;i++) {
			lin_staff[i] = new LinearLayout(this);
			lin_staff[i].setLayoutParams(lps);
			lin_staff[i].setPadding( toPX(MARGIN/2), toPX(4), toPX(MARGIN/2), toPX(4));
			lin_staff[i].setOrientation(1);
			lin_staff[i].setTag(i);
			lin_staffList.addView(lin_staff[i]);

			LinearLayout.LayoutParams ivlps = new LinearLayout.
					LayoutParams(toPX(PORTRAIT_SIZE), toPX(PORTRAIT_SIZE));
			ivlps.setMargins(0, 0, 0, toPX(4));
			iv_tempPortrait[i] = new ImageView(this);
			iv_tempPortrait[i].setLayoutParams(ivlps);
			iv_tempPortrait[i].setBackgroundColor(context.getResources().
					getColor(R.color.black3));
			iv_tempPortrait[i].setPadding(toPX(3), toPX(3), toPX(3), toPX(3));
			iv_tempPortrait[i].setVisibility(View.GONE);
			lin_staff[i].addView(iv_tempPortrait[i]);

			pb_staff[i] = new ProgressBar(this);
			pb_staff[i].setLayoutParams(ivlps);
			lin_staff[i].addView(pb_staff[i]);

			lin_staff[i].setBackgroundResource( R.drawable.white_darkblue );
			addEmployeeClickListener(lin_staff[i]);
			
			iv_portrait[i] = iv_tempPortrait[i];
			
			//ASync that shit
			if (portraitBitmap[matchedIndex[i]] == null)
				new AsyncLoadImage(iv_tempPortrait[i], urlListThisDay[i], 
						pb_staff[i],matchedIndex[i], context).execute();
			else  {
				iv_tempPortrait[i].setImageBitmap(portraitBitmap[matchedIndex[i]]);
				pb_staff[i].setVisibility(View.GONE);
				iv_tempPortrait[i].setVisibility(View.VISIBLE);
			}
		}
		
		//Temp arrayList, string array #swag
		ArrayList<Integer> hoursAL;
		ArrayList<String> tempListedTimes;
		
		RelativeLayout tempRL;
		for (int i=0; i<idListThisDay.length; i++) {
			hoursAL = (ArrayList<Integer>) hourMap.get(
					idListThisDay[i]+"_timeStack");
			tempListedTimes = (ArrayList<String>) hourMap.get(
					idListThisDay[i]+"_listedTimes");
			
			int counter = 0;
//			Log.w("progress", "HOURS AL " + hoursAL.size());
			for (int j=0; j<hoursAL.size();j++) {
				if (hoursAL.get(j)!=0) {
					
					if(j%2 == 1) {
						addBlock(hoursAL.get(j),0xAA6DDE3B, 
								lin_staff[i], tempListedTimes.get(counter++));
					} else {
						addBlock(hoursAL.get(j),-1, 
								lin_staff[i], null);
					}
					
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void addBlock(int height, int color, 
			LinearLayout linearLayout, String listedTimes) {
		LinearLayout box = new LinearLayout(this);
		box.setLayoutParams(new TableLayout.LayoutParams(
				toPX(PORTRAIT_SIZE), 0, height));
		if (color != -1) {
			box.setBackgroundColor(color);
			
			String listedArr[] = listedTimes.split("_");
			
			TextView tv_start = new TextView(this);
			tv_start.setPadding( toPX(8), toPX(2), 0, toPX(2));
			tv_start.setText(listedArr[0] +"\n to \n" + listedArr[1]);
			tv_start.setTextSize(16);
			tv_start.setTextColor(0xAA454545);
			tv_start.setTypeface(null, Typeface .BOLD);
			tv_start.setGravity(Gravity.CENTER);

		    box.addView(tv_start);    
//			Log.e("progress", "intv "+tv_start.getText());
		
//			TextView tv_end = new TextView(this);
//			tv_end.setPadding( 0, toPX(2), 0, toPX(2));
//			tv_end.setText(listedArr[1]);
//			tv_end.setTextSize(16);
//			tv_end.setTextColor(0xAA454545);
//			tv_end.setTypeface(null, Typeface.BOLD);

//		    box.addView(tv_start, lp_topRelative);
		}
		else box.setVisibility(View.INVISIBLE);
		linearLayout.addView(box);
		
	}

	protected Dialog onCreateDialog(int id) {
		switch (id)
		{
		case (CALENDER_VIEW):
		{

			final View v = View.inflate(this, R.layout.dialog_calender, null);
			final TextView calendarMonth = (TextView) v.findViewById(R.id.calender_month);
			calendarMonth.setText(getMonth(selectedMonth).toUpperCase()
					+ " " + selectedYear);
			//Gridview adapter
			final GridView gridView = (GridView) v.findViewById(R.id.gridView);
			final CalendarAdapter adapt_calendar 
			= new CalendarAdapter(this, selectedMonth);
			gridView.setAdapter(adapt_calendar );

			//Allow calender to detect swipe
			//			CalendarSwipeDetector calendarSwipeDetector = 
			//					new CalendarSwipeDetector(this, 
			//							selectedMonth, adapt_calendar, gridView, calendarMonth);
			//			LinearLayout mainLayout = (LinearLayout)v.findViewById(R.id.dialog_main);
			//			mainLayout.setOnTouchListener(calendarSwipeDetector);
			//			gridView.setOnTouchListener(calendarSwipeDetector);

			//click on grid elements
			gridView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					String temp = selectedYear+"-"+selectedMonth+"-"+((TextView)v).getText();
//					Log.e("progress", temp);
					loadDay(BASE_DAY_URL+temp);
					dia_calendar.cancel();
				}
			});

			Button butt_prevMonth = (Button) v.findViewById(R.id.prevMonth);
			butt_prevMonth.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectedMonth = (selectedMonth==1)? 12:selectedMonth-1;
					calendarMonth.setText(getMonth(selectedMonth).toUpperCase()
							+" "+  selectedYear);
					adapt_calendar.updateCalendarAdapter(selectedMonth);
					adapt_calendar.notifyDataSetChanged();
					gridView.setAdapter(adapt_calendar);
				}
			});

			Button butt_nextMonth = (Button) v.findViewById(R.id.nextMonth);
			butt_nextMonth.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectedMonth = (selectedMonth==12)? 1:selectedMonth+1;
					calendarMonth.setText(getMonth(selectedMonth).toUpperCase()  
							+" "+  selectedYear);
					adapt_calendar.updateCalendarAdapter(selectedMonth);
					adapt_calendar.notifyDataSetChanged();
					gridView.setAdapter(adapt_calendar);
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v);
			dia_calendar = builder.create();
			return dia_calendar;
		}
		}
		return null;
	}

	public void addEmployeeClickListener (final LinearLayout lin) {
		final double expansionRatio = 2.5;

		final LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(
				toPX(PORTRAIT_SIZE+MARGIN), LinearLayout.LayoutParams.FILL_PARENT);
		final LinearLayout.LayoutParams biglps = new LinearLayout.LayoutParams(
				(int) (toPX(PORTRAIT_SIZE+MARGIN)*expansionRatio), 
				LinearLayout.LayoutParams.FILL_PARENT);

		lin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				vibrator.vibrate(100);
				
				//If it is expanded, close it
				if(toPX(PORTRAIT_SIZE+MARGIN)*expansionRatio == lin.getMeasuredWidth()) {
					lin.setLayoutParams(lps);
					lin.setBackgroundResource( R.drawable.white_darkblue );
				} else {
					lin.setLayoutParams(biglps);
					lin.setBackgroundResource( R.drawable.employee_darkblue );
					
					int prevExpand = -1;
					LinearLayout tempLin = null;
					
					//search and destroy
					for (int i=0; i<staffAmount; i++) {
						tempLin = lin_staff[i];
						if (toPX(PORTRAIT_SIZE+MARGIN)*expansionRatio == tempLin.getMeasuredWidth()) {
							prevExpand = i;
							tempLin.setLayoutParams(lps);
							tempLin.setBackgroundResource( R.drawable.white_darkblue );
							break;
						}
					}
					
					//CLICKED IS BEFORE
					double offset = 0;
					if ((Integer)lin.getTag() < prevExpand || prevExpand == -1)
						offset = (lin.getLeft()-toPX(PORTRAIT_SIZE)-toPX(MARGIN));
					else
						offset =(lin.getLeft()-toPX(PORTRAIT_SIZE+MARGIN)*expansionRatio);
					
					final int temp = (int)offset;
					hsv_employeeList.post(new Runnable() {            
					    @Override
					    public void run() {
					    	hsv_employeeList.smoothScrollTo(temp, lin.getTop());             
					    }
					});
				}
			}
		});
	}

	public void goMakeReservation(View v) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:952.905.4298" ));
		startActivity(intent);
	}

	public void goChooseDay(View v) {		
		if ( !((Button)v).getText().equals(currentDay) ) {
			for (Button button:butt_day) {
				if ( button != ((Button)v) ) {
					button.setTextColor(context.getResources().
							getColor(R.color.black5));
				}
			}
//
			currentDay = ((Button)v).getText().toString();
			((Button)v).setTextColor(context.getResources().
					getColor(R.color.dark_blue));
		
			loadDay(BASE_DAY_URL+"2013-04-04");
		}
	}
	
	public void goBackMain(View v) {
		Intent nextScreen = new Intent(context, MainActivity.class);
		startActivity(nextScreen);
		loadDay(BASE_DAY_URL + "2013-04-02");
	}

	public void goCalenderCancel(View v) { 
		dia_calendar.cancel();
	}

	public void goCalenderView(View v) { 
		showDialog(CALENDER_VIEW);
//		for (Bitmap b: portraitBitmap) 
//			Log.v("progress", ""+b);
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month-1];
	}

	public void onStop() {
		super.onStop();
		lin_staffList.removeAllViews();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public int toDP(int px)
	{ 	return (int)(px/context.getResources().getDisplayMetrics().density); }

	private int toPX(int dp)
	{ 	return (int)(dp*context.getResources().getDisplayMetrics().density); }

	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }
}


