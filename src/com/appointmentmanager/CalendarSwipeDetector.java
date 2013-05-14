package com.appointmentmanager;

import java.text.DateFormatSymbols;
import com.appointmentmanager.adapter.CalendarAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CalendarSwipeDetector implements View.OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	private Activity activity;
	static final int MIN_DISTANCE = 80;
	private float downX, downY, upX, upY;

	private int selectedMonth;
	private Adapter adapt_calendar;
	private GridView gridView;
	private TextView calendarMonth;

	public CalendarSwipeDetector(Activity activity, int selectedMonth,
			Adapter adapt_calendar, GridView gridView, TextView calendarMonth){
		this.activity = activity;
		this.selectedMonth = selectedMonth;
		this.adapt_calendar = adapt_calendar;
		this.gridView = gridView;
		this.calendarMonth = calendarMonth;
	}

	public void onRightToLeftSwipe(){
		Log.i(logTag, "RightToLeftSwipe!");
		//AKA NEXT MONTH
//		selectedMonth = (selectedMonth==12)? 1:selectedMonth+1;
//		calendarMonth.setText(getMonth(selectedMonth).toUpperCase());
//		((CalendarAdapter) adapt_calendar).updateCalendarAdapter(selectedMonth);
//		((BaseAdapter) adapt_calendar).notifyDataSetChanged();
//		gridView.setAdapter((ListAdapter) adapt_calendar);
	}

	public void onLeftToRightSwipe(){
		Log.i(logTag, "LeftToRightSwipe!");
		//AKA PREV MONTH
//		selectedMonth = (selectedMonth==1)? 12:selectedMonth-1;
//		calendarMonth.setText(getMonth(selectedMonth).toUpperCase());
//		((CalendarAdapter) adapt_calendar).updateCalendarAdapter(selectedMonth);
//		((BaseAdapter) adapt_calendar).notifyDataSetChanged();
//		gridView.setAdapter((GridAdapter) adapt_calendar);
	}

	public void onTopToBottomSwipe(){
		Log.i(logTag, "onTopToBottomSwipe!");
	}

	public void onBottomToTopSwipe(){
		Log.i(logTag, "onBottomToTopSwipe!");
		//    activity.doSomething();
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// swipe horizontal?
			if(Math.abs(deltaX) > MIN_DISTANCE){
				// left or right
				if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
				if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
			}
			else {
				Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
				return false; // We don't consume the event
			}

			// swipe vertical?
			if(Math.abs(deltaY) > MIN_DISTANCE){
				// top or down
				if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
				if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
			}
			else {
				Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
				return false; // We don't consume the event
			}

			return true;
		}
		}
		return false;
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month-1];
	}

}