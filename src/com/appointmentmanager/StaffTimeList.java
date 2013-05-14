package com.appointmentmanager;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appointmentmanager.adapter.CalendarAdapter;
import com.appointmentmanager.adapter.HoloDarkSpinnerAdapter;
import com.appointmentmanager.helper.AsyncJSONHoursLoader;
import com.appointmentmanager.helper.AsyncJSONRequestSlot;

public class StaffTimeList extends Activity{

	///////////////////////////////////////////////////
	final int PORTRAIT_SIZE = 56; //dp
	final int MARGIN = 8; //dp
	final int CALENDER_VIEW = 1; //don't change this shit
	final int SERVICE_TIME_VIEW = 2; //don't change this shit 
	final int ACCEPT_BOOKING_VIEW = 3; //don't change this shit 
	final static String BASE_DAY_URL = "http://ienvynails.com/appman/json/hours.php?date=";
	final static String BASE_REQUEST_URL = "http://ienvynails.com/appman/json/request.php?date=";
	///////////////////////////////////////////////////
	
	boolean hasStarted; //prevent oncreate hangs
	
	//Employee Data Lists:
	String[] nameList, urlList, idList;
	
	String[] dayList;
	String currentDay, today;

	Spinner spin_week;

	LinearLayout lin_timeList;
	LinearLayout lin_scheduleContainer, lin_currentSchedule, lin_oldSchedule;
	LinearLayout[] lin_staff;
	
	Button butt_scheduleLabel;

	ImageView[] iv_portrait;

	Dialog dia_calendar, dia_serviceTime, dia_acceptBooking;

	HorizontalScrollView hsv_employeeList;
	
	int screenWidth, screenHeight, staffAmount, totalStaffAmount;
	int selectedDay, selectedMonth;
	static int selectedYear;
	String selectedMonthFull;
	
	//Appointment time select dialog shit
	String selectedEmployee, selectedTime;
	String selectedSpecificTime;
	int selectedEmployeeID;
	int selectedTimeLength;
	int selectedServiceID;
	ArrayAdapter<String> timeSpinnerAdapter;
	ArrayList<String> timeList;
	
	String myPhoneNumber;
	
	HashMap<String, Object> memoryMap;
	
	static Context context;
	Activity activity;
	Vibrator vibrator;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_staff_time_list);

		Log.v("progress","ZZZZZZZZZZZZZZZZZZZZZZ REAL XXXXXXXXXXXXXXXXXXXXXX");

		//MISC
		context = this.getApplicationContext();
		activity = this;
		Intent intent = getIntent();
		lin_timeList = (LinearLayout) findViewById(R.id.time_list);
		lin_scheduleContainer = (LinearLayout) findViewById(R.id.schedule_container);
		selectedDay = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
		selectedMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
		selectedMonthFull = new SimpleDateFormat("MMMM").format(new Date());
		selectedYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
		hsv_employeeList = (HorizontalScrollView) findViewById(R.id.hsv_employeeList);
		butt_scheduleLabel = (Button) findViewById(R.id.scheduleButton);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		//Get Phone number
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneNumber = tMgr.getLine1Number();
		
		//Appointment Time dialog
		selectedEmployee = null;
		selectedTime = null;
		selectedTimeLength = 0;

		//Day menu top
		dayList = new String[7];
		String[] daysOfWeek = { "Monday", "Tuesday", "Wednesday",
				"Thursday", "Friday", "Saturday", "Sunday"};
		String dayOfTheWeek = new SimpleDateFormat("EEEE").format(new Date());

		for (int i=0; i<daysOfWeek.length;i++) {
			if (dayOfTheWeek.toLowerCase().equals(daysOfWeek[i].toLowerCase())) {
				today = currentDay = daysOfWeek[i];
				for(int j=0; j<daysOfWeek.length;j++) {
					i=i%daysOfWeek.length;
					if (j==0) dayList[j] = daysOfWeek[i++] + " (Today)";
					else dayList[j] = daysOfWeek[i++];
				}
				break;
			}
		}
		
		//week spinner shit
		Spinner spin_daysOfWeek = (Spinner) findViewById(R.id.spin_week);
		HoloDarkSpinnerAdapter adapter = new HoloDarkSpinnerAdapter(this, R.layout.single_spinner_week_item, dayList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_daysOfWeek.setAdapter(adapter);
		
		spin_daysOfWeek.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				//temporarily use currentDay/current month through day project
				int tempSelectedDay = selectedDay;
				int tempSelectedMonth = selectedMonth;
				int tempSelectedYear = selectedYear;
				
				if(arg2!=0 || hasStarted) {
					loadDay(dayProjection(arg2));
					hasStarted = true;
				}
					
				
				selectedDay = tempSelectedDay;
				selectedMonth = tempSelectedMonth;
				selectedYear = tempSelectedYear;
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				//do nothing fuck this method
			}
		});

		//Display  metrics for screen size
		DisplayMetrics metrics =  new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		
		memoryMap = (HashMap<String, Object>)intent.getSerializableExtra("memoryMap");

		totalStaffAmount = (Integer) memoryMap.get("STAFF_AMOUNT");
		idList =  (String[]) memoryMap.get("STAFF_ID_LIST");
		nameList = (String[]) memoryMap.get("STAFF_NAME_LIST");
		urlList = (String[]) memoryMap.get("STAFF_PORTRAIT_LIST");
		
		String tempString = "s";
		
		for (int i= Integer.parseInt((String) memoryMap.get("OPENING_TIME")); 
				 i< Integer.parseInt((String) memoryMap.get("CLOSING_TIME")); i+=100) {
			tempString = ""+i;
			TextView tempTV = new TextView(this);
			tempTV.setLayoutParams(new TableLayout.LayoutParams(
					(int)LayoutParams.FILL_PARENT, (int)LayoutParams.WRAP_CONTENT, 1));
			if (Integer.parseInt(tempString.substring(0,tempString.length()-2)) > 12)
				tempTV.setText(Integer.parseInt(tempString.substring(0,tempString.length()-2))-12+":"+tempString.substring(tempString.length()-2));
			else
				tempTV.setText(tempString.substring(0,tempString.length()-2)+":"+tempString.substring(tempString.length()-2));
			tempTV.setTextColor(0xAA5E5E5E);
//			tempTV.setTypeface(null, Typeface .BOLD);
			tempTV.setGravity(Gravity.RIGHT);
			lin_timeList.addView(tempTV);
		}
			
		iv_portrait = new ImageView[totalStaffAmount];
	}

	public void onStart() {
		super.onStart();
		loadDay(selectedYear+"-"+selectedMonth+"-"+selectedDay);
//		loadDay("http://ienvynails.com/appman/json/hours.php?date=2013-04-02");
	}

	@SuppressWarnings("unchecked")
	public void loadDay(String day) {
		Log.i("progress", "XXXXXXXXXXXXXXXXXXXXXXXX NEW LOAD DAY ZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		
		if (lin_currentSchedule != null) {
			lin_oldSchedule = lin_currentSchedule;
			lin_oldSchedule.setVisibility(View.GONE);
			lin_oldSchedule.removeAllViewsInLayout();
		}
		
		lin_currentSchedule = new LinearLayout(this);
		lin_currentSchedule.setLayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		lin_currentSchedule.setOrientation(LinearLayout.HORIZONTAL);
		
		//bottom button
		selectedMonthFull = getMonth(selectedMonth);
		butt_scheduleLabel.setText("Schedule for "+selectedMonthFull + 
				" " + selectedDay + ", " + selectedYear);
		
		HashMap<String, Object> hourMap = null;
		String[] idListThisDay = null;
		String[] urlListThisDay = null;
		int[] matchedIndex = null;
		
		try {
			
			//hourmap hides that shit swag
			hourMap = new AsyncJSONHoursLoader(BASE_DAY_URL+day).execute().get();
			idListThisDay = (String[]) hourMap.get("idList");
			staffAmount = idListThisDay.length;
			
			//get URLs for thisDay's id's
			matchedIndex = new int[staffAmount];
			urlListThisDay = new String[staffAmount];
			for (int j=0; j<staffAmount; j++) {
				for (int k=0; k<idList.length; k++) {
					//fucking string ID's
					if (idListThisDay[j].equals(idList[k])) {
						urlListThisDay[j] = urlList[k];
						matchedIndex[j] = k;
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
			lin_currentSchedule.addView(lin_staff[i]);

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
			
			//Expansion highlight
//			addEmployeeClickListener(lin_staff[i]);

			iv_portrait[i] = iv_tempPortrait[i];

			//ASync that shit
			File imageFile = new File(Environment.getExternalStorageDirectory().toString()+
					"/aptmgr/employeePortrait"+(matchedIndex[i]+1)+".JPEG");
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			iv_tempPortrait[i].setImageBitmap(bitmap);
			pb_staff[i].setVisibility(View.GONE);
			iv_tempPortrait[i].setVisibility(View.VISIBLE);
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
								lin_staff[i], tempListedTimes.get(counter++), Integer.parseInt(idListThisDay[i]));
					} else 
						addBlock(hoursAL.get(j),-1, lin_staff[i], null, -1);
				}
			}
		} //end for
		
		lin_scheduleContainer.addView(lin_currentSchedule);
	}

	@SuppressWarnings("deprecation")
	public void addBlock(final int height, int color, 
			LinearLayout linearLayout, final String listedTimes, final int employeeID) {
		LinearLayout box = new LinearLayout(this);
		box.setLayoutParams(new TableLayout.LayoutParams(
				toPX(PORTRAIT_SIZE), 0, height));
		box.setOrientation(LinearLayout.VERTICAL);
		
		//negative -1 = invis box
		if (color != -1) {
			box.setBackgroundColor(color);
			//Tag 1 = times
//			box.setTag(1, listedTimes);
			
			final String[] listedArr = listedTimes.split("_");
			if (height>=100) {
				TextView tv_start = new TextView(this);
				tv_start.setLayoutParams(new TableLayout.LayoutParams(
						toPX(PORTRAIT_SIZE), 0, height));
				tv_start.setPadding( 0, toPX(2), 0, 0);
				tv_start.setText(listedArr[0]);
				tv_start.setTextSize(16);
				tv_start.setTextColor(0xAA454545);
				tv_start.setTypeface(null, Typeface .BOLD);
				tv_start.setGravity(Gravity.CENTER_HORIZONTAL);
				box.addView(tv_start);    

				TextView tv_end = new TextView(this);
				tv_end.setPadding( 0, 0, 0, toPX(2));
				tv_end.setText(listedArr[1]);
				tv_end.setTextSize(16);
				tv_end.setTextColor(0xAA454545);
				tv_end.setTypeface(null, Typeface.BOLD);
				tv_end.setGravity(Gravity.CENTER_HORIZONTAL);
				box.addView(tv_end); 
				
				//Click on green box swag
				box.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						vibrator.vibrate(50);
						
						String[] tempArr = (String[])memoryMap.get("STAFF_NAME_LIST");
						
						selectedEmployeeID = employeeID;
						selectedTimeLength = height;
						selectedEmployee = tempArr[employeeID-1];
						selectedTime = listedArr[0]+" to " + listedArr[1];
						showDialog(SERVICE_TIME_VIEW);
					}
				});
			} else if (height >16) { //single line swag
				TextView tv_start = new TextView(this);
				tv_start.setLayoutParams(new TableLayout.LayoutParams(
						toPX(PORTRAIT_SIZE), 0, height));
//				tv_start.setPadding( 0, toPX(1), 0, 0);
				tv_start.setText(listedArr[0]+"-"+listedArr[1]);
				tv_start.setTextSize(10);
				tv_start.setTextColor(0xAA454545);
				tv_start.setTypeface(null, Typeface .BOLD);
				tv_start.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				box.addView(tv_start);  
				
				//Click on green box swag
				box.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						vibrator.vibrate(50);
						
						String[] tempArr = (String[])memoryMap.get("STAFF_NAME_LIST");
						
						selectedEmployeeID = employeeID;
						selectedTimeLength = height;
						selectedEmployee = tempArr[employeeID-1];
						selectedTime = listedArr[0]+" to " + listedArr[1];
						showDialog(SERVICE_TIME_VIEW);
					}
				});
			} else {
				//small shit only <10 min no text, invis
				box.setVisibility(View.INVISIBLE);
			}
		}
		else box.setVisibility(View.INVISIBLE);
		
		linearLayout.addView(box);
		
	}
	
	//Update on prepare
	protected void onPrepareDialog(final int id, final Dialog v) {
		switch(id) {
			case SERVICE_TIME_VIEW: {
				Spinner spin_service = (Spinner) v.findViewById(R.id.spin_service);
				final Spinner spin_time = (Spinner) v.findViewById(R.id.spin_time);
				
				TextView tv_employeeTitle = (TextView) v.findViewById(R.id.employee_title);
				TextView tv_selectedTimePeriod = (TextView) v.findViewById(R.id.selected_time_period);
	
				final TextView tv_price = (TextView) v.findViewById(R.id.service_price);
				final TextView tv_duration = (TextView) v.findViewById(R.id.service_duration);
				
				tv_employeeTitle.setText("Employee: " + selectedEmployee);
				tv_selectedTimePeriod.setText("Selected Time: " + selectedTime);
	
				final String[] serviceList = (String[]) memoryMap.get("SERVICE_NAME_LIST");
				final String[] priceList = (String[]) memoryMap.get("SERVICE_PRICE_LIST");
				final String[] durationList =(String[]) memoryMap.get("SERVICE_DURATION_LIST");
				
				//filter algorithm lol
				ArrayList<String> filteredServiceList = new ArrayList<String> ();
				for (int i=0; i<durationList.length;i++) {
					if ((int)(selectedTimeLength*(0.6)) >= Integer.parseInt(durationList[i])) {
						filteredServiceList.add(serviceList[i]);
					}
				}
				//set service ID for the first non 40 minute entry so duration wont be blank
				selectedServiceID = getID(filteredServiceList.get(0), serviceList);
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
						R.layout.single_spinner_big_font, filteredServiceList);
				dataAdapter.setDropDownViewResource(R.layout.spinner_big_font);
				spin_service.setAdapter(dataAdapter);
	
				spin_service.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
	
						selectedServiceID = getID(((TextView)arg1).getText()
								.toString(), serviceList);
						
						//new timelist does not work, must clear
						timeList.clear();
						String[] tempArr = selectedTime.split(" to ");
						Time endTime = new Time(tempArr[1]);
						
						Time temp = new Time(tempArr[0]);
						temp.add(Integer.parseInt(durationList[selectedServiceID]));
						
						Time currStartTime = new Time(tempArr[0]);
						Time currEndTime = temp;
					
						while (!currEndTime.isLaterThan(endTime)) {
							timeList.add(currStartTime.toString() + " to "+ currEndTime.toString());
							currEndTime.add(5);
							currStartTime.add(5);
						}
						
						timeSpinnerAdapter.notifyDataSetChanged();
						
						tv_price.setText("$"+priceList[selectedServiceID]+".00");
						tv_duration.setText("~"+durationList[selectedServiceID]+" min");
					}
					public void onNothingSelected(AdapterView<?> arg0) {
						//do nothing fuck this method
					}
				});
	
				//generate time slot algorithm
				//only works 
				timeList = new ArrayList<String>();
				
				String[] tempArr = selectedTime.split(" to ");
				
				Time endTime = new Time(tempArr[1]);
				
				Time temp = new Time(tempArr[0]);
				temp.add(Integer.parseInt(durationList[selectedServiceID]));
				
				Time currStartTime = new Time(tempArr[0]);
				Time currEndTime = temp;
			
				while (!currEndTime.isLaterThan(endTime)) {
					timeList.add(currStartTime.toString() + " to "+ currEndTime.toString());
					currEndTime.add(5);
					currStartTime.add(5);
				}

				timeSpinnerAdapter = new ArrayAdapter<String>(this,
						R.layout.single_spinner_big_font, timeList);
				timeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_big_font);
				spin_time.setAdapter(timeSpinnerAdapter);
				///////////////////
	
				Button butt_cancel = (Button) v.findViewById(R.id.cancel);
				butt_cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
	
						dia_serviceTime.cancel();
					}
				});
	
				Button butt_accept = (Button) v.findViewById(R.id.accept);
				butt_accept.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						selectedSpecificTime = spin_time.getSelectedItem().toString();
						
						//check for booking shit in the past

						//check for booking shit in the past

						//check for booking shit in the past

						//check for booking shit in the past
						

						//check for booking shit in the past

						//check for booking shit in the past
						
						dia_serviceTime.cancel();
						showDialog(ACCEPT_BOOKING_VIEW);
					}
				});
			}break;
			
			case ACCEPT_BOOKING_VIEW: 
			{
				ImageView bigPortrait = (ImageView) v.findViewById(R.id.employee_portrait);
				File imageFile = new File(Environment.getExternalStorageDirectory().toString()+
						"/aptmgr/employeePortrait"+selectedEmployeeID+".JPEG");
				bigPortrait.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
				
				TextView employeeName = (TextView) v.findViewById(R.id.employee_name);
				employeeName.setText(selectedEmployee);
				
				TextView serviceName = (TextView) v.findViewById(R.id.service_name);
				String[] tempArr = (String[])memoryMap.get("SERVICE_NAME_LIST");
				serviceName.setText(tempArr[selectedServiceID]);
				
				TextView serviceTime = (TextView) v.findViewById(R.id.service_time);
				serviceTime.setText(selectedSpecificTime);
				
				TextView serviceDate = (TextView) v.findViewById(R.id.service_date);
				serviceDate.setText(selectedMonthFull + " " + selectedDay + ", " + selectedYear);
				
				
				Button butt_cancel = (Button) v.findViewById(R.id.ab_cancel);
				butt_cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dia_acceptBooking.cancel();
					}
				});
	
				Button butt_accept = (Button) v.findViewById(R.id.ab_accept);
				butt_accept.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//2013-04-13&start_time=17:00&end_time=17:30&e_id=5&s_id=4&name=Colin&phone=715-271-1913
						String[] tempArr = selectedSpecificTime.split(" to ");
						
						String tempString = BASE_REQUEST_URL;
						tempString += selectedYear+"-";
						tempString += ((selectedMonth<10)?"0":"") + selectedMonth+"-";
						tempString += ((selectedDay<10)?"0":"") + selectedDay+"&start_time=";
						tempString += tempArr[0]+"&end_time="; 
						tempString += tempArr[1]+"&e_id="; 
						tempString += selectedEmployeeID+"&s_id=";
						tempString += selectedServiceID+"&name=";
						tempString += GetInfo("main", "NAME")+"&phone="; // fixed info!
						tempString += GetInfo("main", "PHONE_NUMBER");

						Log.i("progress", tempString);
						
						
						new AsyncJSONRequestSlot(tempString, activity).execute();
						
						dia_acceptBooking.cancel();
					}
				});
			}break;
			
			
		}
	}
	
	private String GetInfo(String tag, String which)
	{	
		SharedPreferences sharedPreferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
		String savedStr = sharedPreferences.getString(which, "");
		return savedStr;
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
						
						String tempDate = selectedYear+"-"+selectedMonth+"-"+((TextView)v).getText();
	//					Log.e("progress", temp);
						loadDay(tempDate);
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
			
			case (SERVICE_TIME_VIEW):
			{
				View v = View.inflate(this, R.layout.dialog_service_time, null);
				
				//stuff goes here that went into on prepare
	
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(v);
				dia_serviceTime = builder.create();
				return dia_serviceTime;
			}
			
			case (ACCEPT_BOOKING_VIEW):
			{
				View v = View.inflate(this, R.layout.dialog_accept_booking, null);
				// stuff goes here that is in on prepare?
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(v);
				dia_acceptBooking = builder.create();
				return dia_acceptBooking;
			}
			
		}
		return null;
	}
	
	//not used
	public void addEmployeeClickListener (final LinearLayout lin) {
		final double expansionRatio = 2.0;

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
	
	public void goBackMain(View v) {
		Intent nextScreen = new Intent(context, MainActivity.class);
		nextScreen.putExtra("memoryMap",memoryMap);
		startActivity(nextScreen);
	}

	public void goCalenderCancel(View v) { 
		dia_calendar.cancel();
	}

	public void goCalenderView(View v) { 
		showDialog(CALENDER_VIEW);
//		showDialog(ACCEPT_BOOKING_VIEW);
//		for (Bitmap b: portraitBitmap) 
//			Log.v("progress", ""+b);
	}
	
	public void goNextDay(View v) {
		loadDay(dayProjection(1));
	}
	
	public void goPreviousDay(View v) {
		loadDay(dayProjection(-1));
	}
	
	public String dayProjection(int offset) {
		//add year change handles
		Calendar gregCal = new GregorianCalendar( 
				Calendar.getInstance().get(Calendar.YEAR), selectedMonth, 1);
		
		gregCal.set(Calendar.YEAR, selectedMonth-1, 1);
		int dayAmount = gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		//handles 1 month traversal at a time
		if (selectedDay+offset > dayAmount) {
			selectedMonth = selectedMonth == 12? 1:selectedMonth+1;
			selectedDay = selectedDay+offset - dayAmount;
		} else if (selectedDay+offset < 1) {
			selectedMonth = selectedMonth == 1? 12:selectedMonth-1;
			gregCal.set(Calendar.YEAR, selectedMonth-1, 1);
			dayAmount = gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			selectedDay = dayAmount - (selectedDay+offset);
		} else 
			selectedDay += offset;
		
		return selectedYear+"-"+selectedMonth+"-"+selectedDay;
	}
	
	public static void reloadDay(String year, String month, String day) {
//		loadDay(dayProjection(0));
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month-1];
	}

	public int getID(String entry, String[] fullList) {
		for (int i=0; i<fullList.length; i++) {
			if (entry.equals(fullList[i])) return i;
		}
		return -1;
	}

	public void onStop() {
		super.onStop();
//		lin_staffList.removeAllViews();
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
	
	private void log(String s) {
		Log.e("progress", s);
	}
}
