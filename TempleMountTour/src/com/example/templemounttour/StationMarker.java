package com.example.templemounttour;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StationMarker {
	public double longitude;
	public double latitude;
	public String title;
	public HashMap<String, TourPresentation> tours;
	SQLiteDatabase db;
	
	
	public StationMarker(double lat, double lng, String name, Context act, SQLiteDatabase database){
		this.title = name;
		latitude = lat;
		longitude = lng;
		db = database;
		
	}
	
	public void populateTours(Context ctx){
		Cursor c = null;
		try {
			c = db.query("tours", null, "station = ?" , new String[]{title}, null, null, null);
			Log.d("populateTours", "no problems");
		} catch (Exception e) {
			e.printStackTrace();
		}
		tours = new HashMap<>();
		if(c!=null){
			boolean hasTour = c.moveToFirst();
			Log.d("Has Tour?", Boolean.toString(hasTour));
			for(int i = 0;i<c.getCount();i++){
				//TODO values need to  be abstracted
				TourPresentation tp = new TourPresentation(title, c.getString(1),ctx); 
				
				tours.put(tp.tourName, tp);
				c.moveToNext();
			}
			c.close();
		}
	}

}
