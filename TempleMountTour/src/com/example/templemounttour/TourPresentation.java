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
	
	public TourPresentation(String station, String tName, String picLinkString, String audio, String vidLink, String text){
		tourName = tName;
		this.stationName = station;
		stationText = text;
		if(picLinkString!=null){
			picLinks = picLinkString.split(",");
		}
		audioLink = audio;
		videoLink = vidLink;
			
	}
	
}
