package com.appointmentmanager.adapter;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appointmentmanager.R;
import com.appointmentmanager.StaffTimeList;

public class SettingsAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] title, subtitle, entry;

	public SettingsAdapter(Context context, String[] title, 
			String[] subtitle, String[] entry) {
		super(context, R.layout.list_settings, title);
		this.context = context;
		this.title = title;
		this.subtitle = subtitle;
		this.entry = entry;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.list_settings, parent, false);
		TextView tv_title = (TextView) rowView.findViewById(R.id.settings_title);
		TextView tv_subtitle = (TextView) rowView.findViewById(R.id.settings_subtitle);
		TextView tv_entry = (TextView) rowView.findViewById(R.id.settings_entry);
		
		tv_title.setText(title[position]);
		tv_subtitle.setText(subtitle[position]);
		tv_entry.setText(entry[position]);

		return rowView;
	}
}