package com.example.templemounttour;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	
	public void populateTours(){
		Cursor c = null;
		try {
			c = db.query("tours", null, "tour = " + title, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tours = new HashMap<>();
		c.moveToFirst();
		for(int i = 0;i<c.getCount();i++){
			//TODO values need to  be abstracted
			TourPresentation tp = new TourPresentation(title, c.getString(2)); 
			tp.stationText = c.getString(3);
			// etc etc
			tours.put(tp.tourName, tp);
		}
	}

}
