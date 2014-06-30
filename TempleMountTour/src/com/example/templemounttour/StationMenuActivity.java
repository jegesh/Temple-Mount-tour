package com.example.templemounttour;

import java.util.Set;

import com.androidquery.AQuery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StationMenuActivity extends ListActivity {
	public static final String TOUR_INDEX = "tour index";
	public static final String STATION_TITLE = "station title";
	public static final String TOUR_TITLES = "tour titles array";
	public static StationMarker station;
	public static SQLiteDatabase db;
	private String[] tourTitles;
	String currentStationTitle;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_menu);
		String title = "";
		if(getIntent().hasExtra(TouringMapActivity.STATION_NAME))
			title = getIntent().getStringExtra(TouringMapActivity.STATION_NAME);
		else
			title = currentStationTitle;
		setTitle(title);
		AppDBHelper helper = new AppDBHelper(this);
		helper.openDataBase();
		db = helper.db;
		Cursor c = db.query("stations",null,"title = ?",new String[]{title},null,null,null);
		c.moveToFirst();
		station = new StationMarker(c.getDouble(2), c.getDouble(3), c.getString(1), db);
		station.populateTours(this);
		c.close();
		db.close();
		tourTitles = StationMenuActivity.setToStringArray(StationMenuActivity.station.tours.keySet());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tourTitles);
		for(int i = 0;i<tourTitles.length;i++)
			Log.d(station.tours.get(tourTitles[i]).tourName, station.tours.get(tourTitles[i]).picLinks[0]);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String tourTitle = StationMenuActivity.setToStringArray(station.tours.keySet())[position];
				openTour(tourTitle, StationMenuActivity.station,position);
			}
		});
		currentStationTitle = title;
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.station_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static String[] setToStringArray(Set<String> set){
		String[] strArray = new String[set.size()];
		Object[] tmp = set.toArray();
		for(int i = 0;i<tmp.length;i++)
			strArray[i] = (String)tmp[i];
		return strArray;
	}
	
	public void openTour(String tourTitle, StationMarker station, int position){
		Intent intent = new Intent(this, TourSwipeActivity.class);
		intent.putExtra(TOUR_TITLES, tourTitles);
		intent.putExtra(STATION_TITLE, station.title);
		intent.putExtra(TOUR_INDEX, position);
		startActivity(intent);
	}
}
		
	

