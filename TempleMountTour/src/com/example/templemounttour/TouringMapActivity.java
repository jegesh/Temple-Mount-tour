package com.example.templemounttour;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TouringMapActivity extends Activity  implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	public static final String STATION_NAME = "station name";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final double INITIAL_LATITUDE = 31.777358;
	public static final double INITIAL_LONGITUDE = 35.235343;
	GoogleMap gMap;
	Location youAreHere;
	LocationClient locClient;
	static HashMap<String, StationMarker> stations;
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		getActionBar().hide();
		AppDBHelper helper = new AppDBHelper(this);
		helper.openDataBase();
		db = new AppDBHelper(this).getReadableDatabase();
		db = helper.db;
			
		
/*		int viewerHeight = this.getWindow().getAttributes().height;
		mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewerHeight-60));
		mView.onCreate(savedInstanceState);
	*/	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	@Override
	protected void onStop() {
		if(locClient!=null)
			locClient.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)==CommonStatusCodes.SUCCESS){
			
			gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_container)).getMap();
			
			locClient = new LocationClient(this, this, this);
			// initialize database
			setStations();
			placeMarkers();
			locClient.connect();
			gMap.setMyLocationEnabled(true);
		}
		else{
			Dialog d = GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, CONNECTION_FAILURE_RESOLUTION_REQUEST); // need to find request code
			d.show();
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.map, menu);
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
	
	private void setStations(){
		Cursor c = null;
		try {
			c = db.query("stations", null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		stations = new HashMap<>();
		c.moveToFirst();
		
		for(int i = 0;i<c.getCount();i++){
			//TODO values need to  be abstracted
			StationMarker sm = new StationMarker(c.getDouble(2), c.getDouble(3), c.getString(1),this,db); 
			stations.put(sm.title, sm);
			c.moveToNext();
		}
		Log.d("Help me", "No. of entries: "+stations.size());
	}
	
	private void placeMarkers(){
		for(java.util.Map.Entry<String, StationMarker> e:stations.entrySet()){
			gMap.addMarker(new MarkerOptions().position(new LatLng(e.getValue().latitude,e.getValue().longitude)).title(e.getKey()));
			
		}
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker m) {
				Intent intent = new Intent(getBaseContext(), StationMenuActivity.class);
				intent.putExtra(STATION_NAME, m.getTitle());
				startActivity(intent);
				return true;
			}
		});
		
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle arg0) {
		if(MainActivity.tourIsLive){
			youAreHere = locClient.getLastLocation();
			gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(youAreHere.getLatitude(), youAreHere.getLongitude())));
		}else{
			gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(INITIAL_LATITUDE,INITIAL_LONGITUDE)));
		}
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gMap.moveCamera(CameraUpdateFactory.zoomTo(17));
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
}
