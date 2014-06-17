package com.example.templemounttour;

import java.util.concurrent.ExecutionException;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/*
 * TODO: make 'loading' spinner appear while legalities dialog is building
 */

public class MainActivity extends Activity {
	public static boolean tourIsLive;
	public static String legalNotification;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// the purpose was to load the String into memory ahead of time so there would be no wait when the menu option was opened
		// but it's not working...
		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			
			@Override
			protected String doInBackground(String... params) {
				return GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getBaseContext());
			}
			
			@Override
			protected void onPostExecute(String result) {
				legalNotification = result;
				super.onPostExecute(result);
			}
		};
		task.execute(new String[]{});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.legal_info) {
			// if the String hasn't loaded yet, give it 1.5 sec to finish
			if(legalNotification==null)
				try {
					this.wait(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			DialogFragment legalDialog = new LegalitiesDialog();
			legalDialog.show(getFragmentManager(), "legal stuff");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startTour(View v){ // live tour
		Intent intent = new Intent(this, PrepVerifyActivity.class);
		startActivity(intent);
		tourIsLive = true;
	}
	
	public void startBrowsing(View v){ // offline tour
		Intent intent = new Intent(this, OfflineMapActivity.class);
		startActivity(intent);
		tourIsLive = false;
	}

}
