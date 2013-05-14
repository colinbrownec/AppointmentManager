package com.appointmentmanager.adapter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.appointmentmanager.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarAdapter extends BaseAdapter {
	private Context context;
	private int today;
	private int firstDay;
	private int dayAmount, lastMonthAmount;
	private int [] days;
	private boolean currentMonth;

	public CalendarAdapter(Context context, int month) {
		this.context = context;
		updateCalendarAdapter(month);
	}
	
	public void updateCalendarAdapter(int month) {
		//Current month detection
		currentMonth = (month == Integer.parseInt(
				new SimpleDateFormat("MM").format(new Date())))? true: false;
		
		//starts at 0
		month--;
		
		//First Day | -1 for logic adjustments
		Calendar gregCal = new GregorianCalendar( 
				Calendar.getInstance().get(Calendar.YEAR), month, 1);
		firstDay = gregCal.get(Calendar.DAY_OF_WEEK) -1;
		firstDay = (firstDay==0)? 7: firstDay; //adds a week for sunday starts
		Log.i("progress", "First Day: "+ firstDay );

		//# of days
		gregCal.set(Calendar.YEAR, month, 1);
		dayAmount = gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Log.i("progress", "Day Amount " + dayAmount );
		
		//# of days in previous month, resets at jan to dec
		gregCal.set(Calendar.YEAR, (month==0)?11:month-1, 1);
		lastMonthAmount = gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Log.i("progress", "LastMonthAmount " + lastMonthAmount );

		//Today offset by first day
		SimpleDateFormat dayOnly = new SimpleDateFormat("dd");
		today = Integer.parseInt(dayOnly.format(new Date())) + firstDay;

		days = new int[getCount()];
		
		//Int array population # swag
		for (int i=0; i<firstDay; i++)
			days[i] = lastMonthAmount-firstDay+i+1; 
		for (int i=firstDay; i<dayAmount+firstDay; i++)
			days[i] = i+1-firstDay; 
		for (int i=dayAmount+firstDay; i<getCount(); i++)
			days[i] = i-(dayAmount+firstDay)+1; 
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		TextView textView;
		if (convertView == null) {

			textView = (TextView) new TextView(context);
			textView.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(14);
			textView.setPadding(16, 16, 16, 16);
			
			if (position>=dayAmount+firstDay || position<firstDay)
				textView.setTextColor(0xFFABABAB);
			else textView.setTextColor(0xFF000000);
			
			if (currentMonth) {
				if (position>=today-1 && position<=today+5)
					textView.setBackgroundResource( R.drawable.calendar_darkblue );
				if (position==today-1)
					textView.setTypeface(null, Typeface.BOLD);
			}
				
			if (days[position] != 0)
				textView.setText(""+days[position]);

		} else {
			textView = (TextView) convertView;
		}

		return textView;
	}

	@Override
	public int getCount() {
		for (int i=28; i<48; i=i+7)
			if (i>(dayAmount+firstDay))
				return i;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
