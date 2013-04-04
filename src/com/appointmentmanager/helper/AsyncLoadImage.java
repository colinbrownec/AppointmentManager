package com.appointmentmanager.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.appointmentmanager.R;
import com.appointmentmanager.StaffTimeList;

public class AsyncLoadImage extends AsyncTask<Void, Void, Bitmap>{

	private ImageView iv;
	private ProgressBar pb;
	private String url;
	private int i;
	private Context context;

	public AsyncLoadImage(ImageView iv, String url, ProgressBar pb, 
			int i, Context context) {
//		Log.w("progress", "Async onstart");
		this.iv = iv;
		this.url = url;
		this.pb = pb;
		this.i = i;
		this.context = context;
	}
	//
	protected Bitmap doInBackground(Void... params) {

		Bitmap bmp = null;

		try {

			InputStream in = new java.net.URL(url).openStream();
			bmp = BitmapFactory.decodeStream(in);

		} catch (MalformedURLException e) {
			Log.v("progress", "Something wrong with the bitmap");
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("progress", "Something wrong with the bitmap");
			e.printStackTrace();
		}
		return bmp;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
//		Log.w("progress", "Async post execute");
		pb.setVisibility(View.GONE);
		iv.setVisibility(View.VISIBLE);
		StaffTimeList.portraitBitmap[i] = result;

		if (result == null) 
			result = BitmapFactory.decodeResource(context.getResources()
					, R.drawable.l_social_person);
		
		iv.setImageBitmap(result);
		StaffTimeList.portraitBitmap[i] = result;
	}

}