package com.example.templemounttour;

import java.io.File;
import java.util.Currency;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
	
	private static float lastZoom;
	static CameraPosition cameraPos;
	static boolean lastTourType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		AppDBHelper helper = new AppDBHelper(this);
		helper.openDataBase();
		db = helper.db;
			
	}
	
	@Override
	protected void onStart() {
		super.onStart();
				
	}
	
	@Override
	protected void onStop() {
		Log.d("Map message", "In onStop");
		lastTourType = MainActivity.tourIsLive;
		cameraPos = gMap.getCameraPosition();
		lastZoom = gMap.getCameraPosition().zoom;
		if(locClient!=null)
			locClient.disconnect();
		if(!locClient.isConnected())
			Log.d("Map message", "Location client disengaged");
	//	locClient = null;
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	//	locClient.disconnect();
	}
	
	@Override
	protected void onResume() {
	
		super.onResume();
		ServicesAvailabilityHelper saHelper = new ServicesAvailabilityHelper(this);
		if(saHelper.allServicesConnected()){
			if(gMap == null)
				gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_container)).getMap();
			locClient = new LocationClient(this, this, this);
			setStations();
			placeMarkers();
	//		if(lastTourType!=MainActivity.tourIsLive && locClient.isConnected())
	//			locClient.disconnect();
			locClient.connect();
			if(MainActivity.tourIsLive)
				gMap.setMyLocationEnabled(true);
		}else{
			setContentView(R.layout.no_map_error_layout);
			saHelper.frag.show(getFragmentManager(), "Location Updates");
			
		}
	//	super.onResume();
		
	}
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.toggle_normal:
			toggleToNormal();
			return true;
		case R.id.toggle_satellite:
			toggleToSatellite();
			return true;

		default:
			return false;
		}
	}
	
	public void toggleToSatellite(){
		int type = gMap.getMapType();
		if(type != GoogleMap.MAP_TYPE_SATELLITE)
			gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		
	}
	
	public void toggleToNormal(){
		int type = gMap.getMapType();
		if(type == GoogleMap.MAP_TYPE_SATELLITE)
			gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
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
			StationMarker sm = new StationMarker(c.getDouble(2), c.getDouble(3), c.getString(1),db); 
			stations.put(sm.title, sm);
			c.moveToNext();
		}
		c.close();
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
		Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.message_gms_connect_fail), Toast.LENGTH_SHORT).show();
		
	}


	@Override
	public void onConnected(Bundle arg0) {
	//	lastTourType = MainActivity.tourIsLive;
		Log.d("is tour live?",Boolean.toString( MainActivity.tourIsLive));
		if(MainActivity.tourIsLive){
			gMap.moveCamera(CameraUpdateFactory.zoomTo(17));
			Location l = null;
			while(l==null){
				youAreHere = locClient.getLastLocation();
				l=youAreHere;
			}
			gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(youAreHere.getLatitude(), youAreHere.getLongitude())));
			
		}else{
			if(cameraPos!=null && lastTourType==MainActivity.tourIsLive){
				gMap.moveCamera(CameraUpdateFactory.zoomTo(lastZoom));
				gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
			}
			else{
				gMap.moveCamera(CameraUpdateFactory.zoomTo(17));
				gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(INITIAL_LATITUDE,INITIAL_LONGITUDE)));
			}
		}
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
}
