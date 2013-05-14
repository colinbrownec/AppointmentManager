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

public class EmployeeProfileAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] name, hobby, workDay, envyWork;
	private Bitmap[] bitmapArr;

	public EmployeeProfileAdapter(Context context, String[] name, 
			String[] hobby, String[] workDay, String[] envyWork) {
		super(context, R.layout.list_employee_profiles, name);
		this.context = context;
		this.name = name;
		this.hobby = hobby;
		this.workDay = workDay;
		this.envyWork = envyWork;
		
		bitmapArr = new Bitmap[name.length];
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.list_employee_profiles, parent, false);
		TextView tv_name = (TextView) rowView.findViewById(R.id.employee_name);
		ImageView iv_portrait = (ImageView) rowView.findViewById(R.id.employee_portrait);
		TextView tv_yearsWorked = (TextView) rowView.findViewById(R.id.years_worked);
		TextView tv_workDays = (TextView) rowView.findViewById(R.id.work_days);
		TextView tv_hobbies = (TextView) rowView.findViewById(R.id.hobbies);

		tv_name.setText(name[position]);
		if (Integer.parseInt(envyWork[position]) == 1)
			tv_yearsWorked.setText(envyWork[position] + " year");
		else tv_yearsWorked.setText(envyWork[position] + " years");
		tv_workDays.setText(workDay[position]);
		tv_hobbies.setText(hobby[position]);
		
		if (bitmapArr[position] == null) {
			File imageFile = new File(Environment.getExternalStorageDirectory().toString()+
					"/aptmgr/employeePortrait"+(position+1)+".JPEG");
			bitmapArr[position] = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		} 
		
		iv_portrait.setImageBitmap(bitmapArr[position]);

		return rowView;
	}
}