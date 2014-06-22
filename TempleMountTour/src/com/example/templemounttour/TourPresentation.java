package com.example.templemounttour;

public class TourPresentation {
	public String tourName;
	public String stationText;
	public String[] picLinks;
	public String audioLink;
	public String videoLink;
	public String station;
	
	public TourPresentation(String station, String tName){
		tourName = tName;
		this.station = station;
	}
	
}
