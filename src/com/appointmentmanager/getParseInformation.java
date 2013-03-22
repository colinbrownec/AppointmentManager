package com.appointmentmanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class getParseInformation extends AsyncTask<Void, Void, Void>{

	ImageView imageview;
	Bitmap bitmap;

	public getParseInformation(ImageView imageview)
	{
		this.imageview = imageview;
	}

	@Override
	protected Void doInBackground(Void... params) {
		//
		Log.i("parse", "enter parse");
		ParseQuery query = new ParseQuery("Employees");
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) { // if it was successful
					// probably want to call a method

					for (ParseObject employee : objects) {
						Log.i("progress", ""+employee.get("e_name"));

						ParseFile file = (ParseFile) employee.get("e_picture");
						Log.i("progress", ""+file.getUrl());

						try {
							bitmap = BitmapFactory.decodeStream((InputStream)new URL(file.getUrl()).getContent());
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				} else {
					// handle error
					Log.i("progress", "FAILED: "+ e.getMessage());
				}
			}
		});
		return null;
	}

	protected void onPostExecute(Void result) {
		imageview.setImageBitmap(bitmap);

	}
}