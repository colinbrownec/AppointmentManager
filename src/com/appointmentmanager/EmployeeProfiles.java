package com.appointmentmanager;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.appointmentmanager.adapter.EmployeeProfileAdapter;

public class EmployeeProfiles extends Activity {
	
	private static Context context;
	private Activity activity;
	private Intent intent;
	
	HashMap<String, Object> memoryMap;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_employee_profiles);
		
		//housekeeping
		context = this.getApplicationContext();
		activity = this;
		intent = getIntent();
		
		memoryMap = (HashMap<String, Object>)intent.getSerializableExtra("memoryMap");

		String[] eName = (String[]) memoryMap.get("STAFF_NAME_LIST");
		String[] eHobby = (String[]) memoryMap.get("STAFF_HOBBY_LIST");
		String[] eWorkDay = (String[]) memoryMap.get("STAFF_AVALIABLE_LIST");
		String[] eEnvyWork = (String[]) memoryMap.get("STAFF_YEAR_LIST");
		
		ListView mainListView = (ListView) findViewById( R.id.employeeListView );   
		EmployeeProfileAdapter listAdapter = new EmployeeProfileAdapter(this, 
				eName, eHobby, eWorkDay, eEnvyWork);
		mainListView.setAdapter( listAdapter );

		mainListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
//				goEditPatient(patientID[numPatients-myItemInt-1]);
			}                 
		});
		
		
	}
	
	public void goBackMain(View v) {
		Intent nextScreen = new Intent(context, MainActivity.class);
		nextScreen.putExtra("memoryMap",memoryMap);
		startActivity(nextScreen);
	}
	
	private static void toast(String text)
	{ 	Toast.makeText(context, text, Toast.LENGTH_LONG).show(); }

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee_profiles, menu);
		return true;
	}
}
