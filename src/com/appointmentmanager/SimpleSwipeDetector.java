package com.appointmentmanager;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class SimpleSwipeDetector extends SimpleOnGestureListener {

	static final String logTag = "ActivitySwipeDetector";
	private Activity activity;
	static final int MIN_DISTANCE = 80;
	private float downX, downY, upX, upY;
	
	private int SWIPE_MIN_DISTANCE = 160;
	private int SWIPE_MAX_OFF_PATH = 250;
	private int SWIPE_THRESHOLD_VELOCITY = 200;

	public SimpleSwipeDetector(Activity activity){
		this.activity = activity;
	}

	public void onRightToLeftSwipe(){
		Log.i(logTag, "RightToLeftSwipe!");
	}

	public void onLeftToRightSwipe(){
		Log.i(logTag, "LeftToRightSwipe!");
	}

	public void onTopToBottomSwipe(){
		Log.i(logTag, "onTopToBottomSwipe!");
	}

	public void onBottomToTopSwipe(){
		Log.i(logTag, "onBottomToTopSwipe!");
		//    activity.doSomething();
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				this.onRightToLeftSwipe();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				this.onRightToLeftSwipe();
			}
		} catch (Exception e) {
			// nothing
		}
		return false;
	}

	//	public boolean onTouch(View v, MotionEvent event) {
	//		switch(event.getAction()){
	//		case MotionEvent.ACTION_DOWN: {
	//			downX = event.getX();
	//			downY = event.getY();
	//			return true;
	//		}
	//		case MotionEvent.ACTION_UP: {
	//			upX = event.getX();
	//			upY = event.getY();
	//
	//			float deltaX = downX - upX;
	//			float deltaY = downY - upY;
	//
	//			// swipe horizontal?
	//					if(Math.abs(deltaX) > MIN_DISTANCE){
	//						// left or right
	//						if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
	//						if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
	//					}
	//					else {
	//						Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
	//						return false; // We don't consume the event
	//					}
	//
	//					// swipe vertical?
	//					if(Math.abs(deltaY) > MIN_DISTANCE){
	//						// top or down
	//						if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
	//						if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
	//					}
	//					else {
	//						Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
	//						return false; // We don't consume the event
	//					}
	//
	//					return true;
	//		}
	//		}
	//		return false;
	//	}

}