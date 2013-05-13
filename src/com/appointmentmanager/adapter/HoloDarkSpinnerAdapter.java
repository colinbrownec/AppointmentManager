package com.appointmentmanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appointmentmanager.R;

public class HoloDarkSpinnerAdapter extends ArrayAdapter<String> {

	private Context context;
	
	public HoloDarkSpinnerAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	//Change dropdown menu color here
	public View getDropDownView (int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		
		//commenting out below literally makes this class do nothing gg
//		v.setBackgroundResource(R.drawable.spinner_darkblue_lightblue);
		return v;
	}

	//	//Change background color here
	//	@Override
	//	public View getView (int position, View convertView, ViewGroup parent) {
	//	    View v = super.getView(position, convertView, parent);
	//	    return v;
	//	}
}
