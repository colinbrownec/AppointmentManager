package com.appointmentmanager;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appointmentmanager.adapter.SettingsAdapter;
import com.appointmentmanager.helper.AsyncJSONStatusHandler;

public class Settings extends Activity {

	private static Context context;
	private Activity activity;
	private Intent intent;
	private static HashMap<String, Object> memoryMap;

	private static final int NAME_DIALOG = 1;
	private static final int PHONE_DIALOG = 2;
	private static final int THEME_DIALOG = 3;
	private static final int INFO_DIALOG = 4;
	
	private Dialog dia_name, dia_phone, dia_theme, dia_info;
	
	private String name, phone, theme;
	private String phoneNumber;
	private SettingsAdapter listAdapter;
	
	private Button butt_status;
	private AsyncJSONStatusHandler statusHandler;
	
	String[] title, subtitle, entry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);
		
		Log.w("progress","-------------Oncreate Settings------------");
		
		//housekeeping
		context = this.getApplicationContext();
		activity = this;
		intent = getIntent();
		
		//Misc
		butt_status = (Button) findViewById(R.id.status);

		memoryMap = (HashMap<String, Object>)intent.getSerializableExtra("memoryMap");

		
		statusHandler = new AsyncJSONStatusHandler("ZZ", butt_status, activity);
		statusHandler.execute();
		
		//Get phone number
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = phone = tMgr.getLine1Number();
		
		if (phone != null)
			phone = phone.substring(phone.length()-10,phone.length()-7)+"-"+
					phone.substring(phone.length()-7,phone.length()-4)+"-"+
					phone.substring(phone.length()-4);
		else {
			phone = "555-555-5555";
			phoneNumber = "5555555555";
		}
		
		//Phone number is established
		SaveInfo("main", "PHONE", phone);
		SaveInfo("main", "PHONE_NUMBER", phoneNumber);
		
		//Name:
		name = "(enter your name)";
		
		String temp = GetInfo("main", "NAME");
		if ( temp.length() > 0 ) {;
			name = temp;
		}
		
		//Listview setup
		title = new String[3];
		subtitle = new String[3];
		entry = new String[3];
		
		title[0] = "Name";
		title[1] = "Phone Number";
		title[2] = "Theme";
		
		subtitle[0] = "What you register under";
		subtitle[1] = "Used as a callback";
		subtitle[2] = "Choose your theme";
		
		entry[0] = name;
		entry[1] = phone;
		entry[2] = "Holo Light";

		ListView mainListView = (ListView) findViewById( R.id.settingsListView );   
		listAdapter = new SettingsAdapter(this, title, subtitle, entry);
		mainListView.setAdapter( listAdapter );

		mainListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				if (myItemInt == 0) showDialog(NAME_DIALOG);
				if (myItemInt == 1) showDialog(PHONE_DIALOG);
			}
		});
	}
	
	public void onStart() {
		super.onStart();
		
		Drawable icon= context.getResources().getDrawable(R.drawable.l_rating_good);
		butt_status.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
	}

	protected void onPrepareDialog(final int id, final Dialog v) {
		//open keyboard
		v.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		switch(id) {
		case NAME_DIALOG: {
			
			final EditText et_name = (EditText) v.findViewById(R.id.name_edittext);
			if ( !name.equals("(enter your name)") )
				et_name.setText(GetInfo("main", "NAME"));
			et_name.requestFocus();
			
			Button butt_cancel = (Button) v.findViewById(R.id.accept);
			butt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					if (et_name.getText().toString().length() <= 0)
						toast("Please enter a name.");
					else {
						SaveInfo("main", "NAME", et_name.getText().toString());
						entry[0] =  et_name.getText().toString();
						listAdapter.notifyDataSetChanged();
						dia_name.cancel();
					}
				}
			});
		}break;
		
		case PHONE_DIALOG:{
			
			final EditText et_phone = (EditText) v.findViewById(R.id.phone_edittext);
			et_phone.setText(GetInfo("main", "PHONE_NUMBER"));
			et_phone.requestFocus();
			
			Button butt_cancel = (Button) v.findViewById(R.id.accept);
			butt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					if (et_phone.getText().toString().length() < 10)
						toast("Please enter a valid number with area code.");
					else {
						String temp = et_phone.getText().toString();
						temp = temp.substring(temp.length()-10,temp.length()-7)+"-"+
								temp.substring(temp.length()-7,temp.length()-4)+"-"+
								temp.substring(temp.length()-4);
						
						SaveInfo("main", "PHONE_NUMBER", et_phone.getText().toString());
						SaveInfo("main", "PHONE", temp);
						
						entry[1] =  temp;
						listAdapter.notifyDataSetChanged();
						dia_phone.cancel();
					}
				}
			});
		}break;
		
		case THEME_DIALOG:{
			
			Button butt_cancel = (Button) v.findViewById(R.id.accept);
			butt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dia_theme.cancel();
				}
			});
		}break;
		
		case INFO_DIALOG:{
			
			
			
			Button butt_cancel = (Button) v.findViewById(R.id.ab_cancel);
			butt_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dia_info.cancel();
				}
			});
		}break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id)
		{
		case (NAME_DIALOG): {
			View v = View.inflate(this, R.layout.dialog_setting_name, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v);
			dia_name = builder.create();
			return dia_name;
		}

		case (PHONE_DIALOG): {
			View v = View.inflate(this, R.layout.dialog_setting_phone, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v);
			dia_phone = builder.create();
			return dia_phone;
		}

		case (THEME_DIALOG): {
			View v = View.inflate(this, R.layout.dialog_setting_name, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v);
			dia_theme = builder.create();
			return dia_theme;
		}
		
		case (INFO_DIALOG): {
			View v = View.inflate(this, R.layout.dialog_reservation_info, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v);
			dia_info = builder.create();
			return dia_info;
		}
		
		}		
		return null;
	}

	public void goBackMain(View v) {
		Intent nextScreen = new Intent(context, MainActivity.class);
		nextScreen.putExtra("memoryMap", memoryMap);
		startActivity(nextScreen);
	}

	public void goCheckStatus(View v) {
		showDialog(INFO_DIALOG);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }
	
	private void SaveInfo(String area, String saveTag, String value) {
		SharedPreferences sharedPreferences = getSharedPreferences(area,MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(saveTag, value);
		editor.commit();
	}

	private String GetInfo(String tag, String which) {	
		SharedPreferences sharedPreferences = getSharedPreferences(tag,MODE_PRIVATE);
		String savedStr = sharedPreferences.getString(which, "");
		return savedStr;
	}	

}
