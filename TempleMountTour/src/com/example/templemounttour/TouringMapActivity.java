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
	private ConnectionResult connectionResult;
	private static float lastZoom;
	static CameraPosition cameraPos;
	static 
	boolean lastTourType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		//getActionBar().hide();
		
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
		cameraPos = gMap.getCameraPosition();
		lastZoom = gMap.getCameraPosition().zoom;
		if(locClient!=null)
			locClient.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)==CommonStatusCodes.SUCCESS){
			
			gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_container)).getMap();
			
			locClient = new LocationClient(this, this, this);
			setStations();
			placeMarkers();
			if(lastTourType!=MainActivity.tourIsLive && locClient.isConnected())
				locClient.disconnect();
			locClient.connect();
			if(lastTourType==MainActivity.tourIsLive)
				gMap.setMyLocationEnabled(true);
		}
		else{
			Dialog d = GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, CONNECTION_FAILURE_RESOLUTION_REQUEST); // need to find request code
			d.show();
		}
		super.onResume();
	}

	// Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(),
                        "Location Updates");
            }
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
		db.close();
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
		lastTourType = MainActivity.tourIsLive;
		Log.d("is tour live?",Boolean.toString( MainActivity.tourIsLive));
		if(MainActivity.tourIsLive){
			youAreHere = locClient.getLastLocation();
			gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(youAreHere.getLatitude(), youAreHere.getLongitude())));
		}else{
			if(cameraPos!=null){
				gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
				gMap.moveCamera(CameraUpdateFactory.zoomTo(lastZoom));
			}
			else{
				gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(INITIAL_LATITUDE,INITIAL_LONGITUDE)));
				gMap.moveCamera(CameraUpdateFactory.zoomTo(17));
			}
		}
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
}
