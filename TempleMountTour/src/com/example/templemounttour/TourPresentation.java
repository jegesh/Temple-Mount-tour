package com.example.templemounttour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TourPresentation {
	public String tourName;
	public String stationText;
	public String[] picLinks;
	public String audioLink;
	public String videoLink;
	public String stationName;
	
	public TourPresentation(String station, String tName, Context activity){
		tourName = tName;
		this.stationName = station;
		AppDBHelper helper = new AppDBHelper(activity);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query("tours", null, "station = ? AND tour = ?", new String[]{station, tName}, null, null, "tour DESC");
		if(c.moveToFirst()){
			stationText = c.getString(2);
			if(c.getString(3)!=null){
				String links = c.getString(3);
				picLinks = links.split(",");
			}
			audioLink = c.getString(4);
			videoLink = c.getString(5);
			
		}
		c.close();
		db.close();
	}
	
}
