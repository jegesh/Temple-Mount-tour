package com.example.templemounttour;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
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
		
/*		int viewerHeight = this.getWindow().getAttributes().height;
		mView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, viewerHeight-60));
		mView.onCreate(savedInstanceState);
	*/	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		locClient.connect();
	}
	
	@Override
	protected void onStop() {
		locClient.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)==CommonStatusCodes.SUCCESS){
			super.onResume();
			gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_container)).getMap();
			gMap.setMyLocationEnabled(true);
			locClient = new LocationClient(this, this, this);
			// initialize database
			setStations();
			placeMarkers();
		}
		else{
			Dialog d = GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, CONNECTION_FAILURE_RESOLUTION_REQUEST); // need to find request code
			d.show();
		}
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
			StationMarker sm = new StationMarker(c.getDouble(2), c.getDouble(3), c.getString(1)); 
			stations.put(sm.title, sm);
		}
	}
	
	private void placeMarkers(){
		for(java.util.Map.Entry<String, StationMarker> e:stations.entrySet()){
			gMap.addMarker(new MarkerOptions().position(new LatLng(e.getValue().latitude,e.getValue().longitude)).title(e.getKey()));
			
		}
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker m) {
				Intent intent = new Intent(getBaseContext(), StationActivity.class);
				intent.putExtra(STATION_NAME, m.getTitle());
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
		youAreHere = locClient.getLastLocation();
		gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(youAreHere.getLatitude(), youAreHere.getLongitude())));
		gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		gMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
}
