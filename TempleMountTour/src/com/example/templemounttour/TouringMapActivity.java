package com.example.templemounttour;

import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class TouringMapActivity extends Activity  implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	GoogleMap gMap;
	Location youAreHere;
	LocationClient locClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_container)).getMap();
		gMap.setMyLocationEnabled(true);
		gMap.moveCamera(CameraUpdateFactory.zoomTo(18));
		locClient = new LocationClient(this, this, this);
		
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
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)==CommonStatusCodes.SUCCESS)
			super.onResume();
		else{
			Dialog d = GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, 0); // need to find request code
			d.show();
		}
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle arg0) {
		youAreHere = locClient.getLastLocation();
		gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(youAreHere.getLatitude(), youAreHere.getLongitude())));
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
}
