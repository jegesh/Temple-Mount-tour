package com.example.templemounttour;

import com.androidquery.AQuery;
import com.example.templemounttour.TourSwipeActivity.ToursPagerAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/*

public class TourSwipeFragment extends android.support.v4.app.Fragment{
	
	
	public TourSwipeFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_tour, container, false);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Bundle b = getArguments();
		
		TextView titleTxt = (TextView)getActivity().findViewById(R.id.tour_title);
		titleTxt.setText(b.getString(ToursPagerAdapter.TOUR_NAME, getString(R.string.title_activity_tour_main_menu)));
		LinearLayout llContainer = (LinearLayout)getActivity().findViewById(R.id.tour_pic_container);
		LayoutParams layParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if(b.getString(ToursPagerAdapter.STATION_TEXT)!=null)
			((TextView)getActivity().findViewById(R.id.tour_explanation)).setText(b.getString(ToursPagerAdapter.STATION_TEXT));
		if(b.getStringArray(ToursPagerAdapter.PIC_LINKS)!=null){
			for(String link:b.getStringArray(ToursPagerAdapter.PIC_LINKS)){
				ImageView iv = new ImageView(getActivity());
				android.widget.LinearLayout.LayoutParams ivParams =   new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
	//			ivParams = Gravity.CENTER;
				ivParams.topMargin = 40;
				
				iv.setLayoutParams(ivParams);
				AQuery aq = new AQuery(getActivity());
				int ivId = MainActivity.generateViewId();
				iv.setId(ivId);
				llContainer.addView(iv);
				aq.id(ivId).image(link);
				
			}
		}/*
		if(tp.audioLink!=null){
			
		}
		if(tp.videoLink!=null){
			
		}
		super.onActivityCreated(savedInstanceState);
	}
}*/


