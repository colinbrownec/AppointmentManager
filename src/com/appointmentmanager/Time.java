package com.appointmentmanager;

public class Time {
	private int hour;
	private int minute;

	//Time only works from between 9:30 am to 6:30 pm
	public Time(String time) {
		String[] tempArr = time.split(":");
		hour = Integer.parseInt(tempArr[0]);
		minute = Integer.parseInt(tempArr[1]);
	}

	public void add(int added) {
		if (added > 60) {
			hour = (hour+added/60 >12)? hour+added/60 - 12: hour+added/60;
			added %= 60;
		}

		if (minute+added >59) {
			minute = (minute+added)-60;
			hour = (hour==12)?1:hour+1;
		} else {
			minute += added;
		}
	}

	public boolean isLaterThan(Time t) {
		String[] tempArr = t.toString().split(":");
		int tHour = Integer.parseInt(tempArr[0]);
		int tMinute = Integer.parseInt(tempArr[1]);

		int tempHour = hour;
		int tempMinute = minute;

		if (tempHour>=1 && tempHour<=6) tempHour += 12;
		if (tHour>=1 && tHour<=6) tHour += 12;

		if (tempHour>tHour || tHour == tempHour && tempMinute>tMinute) 
			return true;
		else
			return false;
	}

	public String toString() {
		if (minute<10) 
			return hour+":0"+minute;
		return hour+":"+minute;
	}
}
