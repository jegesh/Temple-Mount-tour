package com.example.templemounttour;

import java.util.Set;

import com.androidquery.AQuery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class StationMenuActivity extends Activity {
	public static final String TOUR_NAME = "tour name";
	public static final String STATION_TITLE = "station title";
	public static StationMarker station;
	public static SQLiteDatabase db;
	public static int tourIndexLimit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_menu);
		String title = getIntent().getStringExtra(TouringMapActivity.STATION_NAME);
		setTitle(title);
		AppDBHelper helper = new AppDBHelper(this);
		helper.openDataBase();
		db = helper.db;
		Cursor c = db.query("stations",null,"title = ?",new String[]{title},null,null,null);
		c.moveToFirst();
		station = new StationMarker(c.getDouble(2), c.getDouble(3), c.getString(1), this, db);
		station.populateTours(this);
		if (savedInstanceState == null) {
			StationMenuFragment frag = new StationMenuFragment();
			
			getFragmentManager().beginTransaction().add(R.id.station_fragment_container, frag ).commit();
		}
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
		for(int i=0;i<set.size();i++)
			strArray[i] = set.iterator().next();
		return strArray;
	}
	
	public void openTour(String tourTitle, StationMarker station){
		TourFragment frag = new TourFragment();
		Bundle b = new Bundle();
		b.putString(STATION_TITLE, station.title);
		b.putString(TOUR_NAME, tourTitle);
		frag.setArguments(b);
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(R.id.station_fragment_container, frag );
		trans.addToBackStack(null);
		trans.commit();
	}
}
	class StationMenuFragment extends ListFragment{
		
		
		public StationMenuFragment(){
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			StationMenuActivity.tourIndexLimit = StationMenuActivity.station.tours.size()-1;
			
			
			View v = inflater.inflate(R.layout.fragment_station_menu, container, false);
			return v;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			
			String[] tourTitles = StationMenuActivity.setToStringArray(StationMenuActivity.station.tours.keySet());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, tourTitles);
			getListView().setAdapter(adapter);
			getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String tourTitle = StationMenuActivity.setToStringArray(StationMenuActivity.station.tours.keySet())[position];
					((StationMenuActivity)getActivity()).openTour(tourTitle, StationMenuActivity.station);
				}
			});
			super.onActivityCreated(savedInstanceState);
		}
	}
	
	class TourFragment extends Fragment{
		
		
		public TourFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			
			View v = inflater.inflate(R.layout.fragment_tour, container, false);
			return v;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
Bundle b = getArguments();
			
			TourPresentation tp = new TourPresentation(b.getString(StationMenuActivity.STATION_TITLE), b.getString(StationMenuActivity.TOUR_NAME), getActivity());
			TextView titleTxt = (TextView)getActivity().findViewById(R.id.tour_title);
			titleTxt.setText(tp.tourName);
			LinearLayout llContainer = (LinearLayout)getActivity().findViewById(R.id.tour_pic_container);
			LayoutParams layParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if(tp.stationText!=null)
				((TextView)getActivity().findViewById(R.id.tour_explanation)).setText(tp.stationText);
			if(tp.picLinks!=null){
				for(String link:tp.picLinks){
					ImageView iv = new ImageView(getActivity());
					android.widget.LinearLayout.LayoutParams ivParams = (android.widget.LinearLayout.LayoutParams) new LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
					ivParams.gravity = Gravity.CENTER;
					iv.setLayoutParams(ivParams);
					AQuery aq = new AQuery(getActivity());
					int ivId = MainActivity.generateViewId();
					iv.setId(ivId);
					aq.id(ivId).image(link);
					llContainer.addView(iv);
					
				}
			}
			if(tp.audioLink!=null){
				
			}
			if(tp.videoLink!=null){
				
			}
			super.onActivityCreated(savedInstanceState);
		}
	}
	


