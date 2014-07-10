package com.example.templemounttour;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.en;
import com.google.android.gms.location.LocationClient;

public final class ServicesAvailabilityHelper {
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static Activity act;
	private ConnectionResult connectionResult;
	ErrorDialogFragment frag;
	LocationClient lClient;
	
	public ServicesAvailabilityHelper(Activity a){
		act = a;
//		lClient = lc;
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
    
    boolean locationsEnabled(){
    	Log.d("Checking", "Device locations");
    	if(!MainActivity.tourIsLive)
    		return true;
    	else{
	    	try {
	    		LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
	    		List<String> providers = lm.getAllProviders();
	    		boolean enabled = true;
	    		for(int i = 0;i<providers.size();i++){
	    			if(lm.isProviderEnabled(providers.get(i))){
	    				if(providers.get(i).equalsIgnoreCase(LocationManager.GPS_PROVIDER))
	    					enabled = false;
	    				enabled = true;
	    			}
	    			
	    		}
	    		if(providers.size()==0)
	    			return false;
	    		return enabled;
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
    	return false;
    }

    boolean allServicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(act);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            if(locationsEnabled())
            	return true;
            else{ // location services need to be turned on in the device
            	frag = new ErrorDialogFragment();
            	frag.setDialog(getLocationErrorDialog());
            }
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, act, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                frag = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                frag.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                
            }else{
            	// Google Play does not provide an error dialog
            	frag = new ErrorDialogFragment();
            	frag.setDialog(getDefaultGooglePlayFailureDialog());
            }
            
        }
        return false;
    }
    
    Dialog getLocationErrorDialog(){
    	Dialog di = new Dialog(act, DialogFragment.STYLE_NORMAL);
    	di.setTitle(R.string.message_error);
    	di.setContentView(R.layout.location_error_dialog);
    	return di;
    	
    }
    
    Dialog getDefaultGooglePlayFailureDialog(){
    	Dialog di = new Dialog(act, DialogFragment.STYLE_NORMAL);
    	di.setTitle(R.string.message_error);
    	di.setContentView(R.layout.location_error_dialog);
    	return di;
    }
}
