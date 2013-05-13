package com.appointmentmanager.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
			Log.v("progress", "Something wrong with the bitmap URL");
		} catch (IOException e) {
			Log.v("progress", "Something wrong with the bitmap I/O");
		}
		return bmp;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		pb.setVisibility(View.GONE);
		iv.setVisibility(View.VISIBLE);

		if (result == null) 
			result = BitmapFactory.decodeResource(context.getResources()
					, R.drawable.l_social_person);

		iv.setImageBitmap(result);
		
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/aptmgr";
		File dir = new File(extStorageDirectory);
		if(!dir.exists()) dir.mkdirs();
		
		

		OutputStream outStream = null;
		File file = new File(extStorageDirectory, "employeePortrait"+(i+1)+".JPEG");
		try {
			outStream = new FileOutputStream(file);
			result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
//			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

}