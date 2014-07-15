package com.example.templemounttour;

import com.androidquery.AQuery;
import com.example.templemounttour.TourSwipeActivity.ToursPagerAdapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TourSwipeActivity extends FragmentActivity {
    ToursPagerAdapter tourPagerAdapter;
    ViewPager mViewPager;
    StationMarker station;
    SQLiteDatabase db;
    String[] titles;
    boolean firstTab;
    android.app.FragmentManager fragMan;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_swipe);
        fragMan = getFragmentManager();
        db = new AppDBHelper(this).getReadableDatabase();
        station = new StationMarker(getIntent().getStringExtra(StationMenuActivity.STATION_TITLE), db);
        station.populateTours(this);
        db.close();
        setTitle(station.title);
        titles = getIntent().getStringArrayExtra(StationMenuActivity.TOUR_TITLES);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        
    }
    
    @Override
    protected void onStart() {
    	tourPagerAdapter =
                new ToursPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
       	mViewPager.setAdapter(tourPagerAdapter);
       	if(firstTab = true){
       		mViewPager.setCurrentItem(this.getIntent().getIntExtra(StationMenuActivity.TOUR_INDEX, 0));
       		firstTab = false;
       	}
    	super.onStart();
    	
    }
    
    @Override
    protected void onResume() {
    
    	super.onResume();
    }


public class ToursPagerAdapter extends FragmentStatePagerAdapter {
    public static final String PIC_LINKS = "pic links";
	public static final String STATION_TEXT = "station text";
	public static final String TOUR_NAME = "tour name";

	public ToursPagerAdapter(FragmentManager fm) {
        super(fm);
    }
	/*
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if(firstTab){
			Log.d("setPrimaryItem method", "First Tab is true, index is: "+TourSwipeActivity.this.getIntent().getIntExtra(StationMenuActivity.TOUR_INDEX, 0));
			android.support.v4.app.Fragment f = getItem(TourSwipeActivity.this.getIntent().getIntExtra(StationMenuActivity.TOUR_INDEX, 0));
			firstTab = false;
			super.setPrimaryItem(container, TourSwipeActivity.this.getIntent().getIntExtra(StationMenuActivity.TOUR_INDEX, 0), object);
		}else
			super.setPrimaryItem(container, position, object);
	}; */

    @Override
    public android.support.v4.app.Fragment getItem(int i) {
    	Log.d("We're here", "In the getItem method");
        android.support.v4.app.Fragment fragment = new TourSwipeFragment();
        Bundle args = new Bundle();
        String tName =  titles[i];
        TourPresentation tour = station.tours.get(tName);
        args.putString(TOUR_NAME, tName);
        args.putString(STATION_TEXT, tour.stationText);
        args.putStringArray(PIC_LINKS, tour.picLinks);
        Log.d("tour name", tName);
        Log.d("first pic link", tour.picLinks[0]);
        Log.d("last pic link", tour.picLinks[tour.picLinks.length-1]);
        // etc etc
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return station.tours.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return station.title;
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
    	// TODO Auto-generated method stub
    	return super.isViewFromObject(view, object);
    }
}


public static class TourSwipeFragment extends android.support.v4.app.Fragment{
	String picLink;
	
	public TourSwipeFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_tour, container, false);
		
		Bundle b = getArguments();
		
		TextView titleTxt = (TextView)v.findViewById(R.id.tour_title);
		titleTxt.setText(b.getString(ToursPagerAdapter.TOUR_NAME, getString(R.string.title_activity_tour_main_menu)));
		LinearLayout llContainer = (LinearLayout)v.findViewById(R.id.tour_pic_container);
		LayoutParams layParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		if(b.getString(ToursPagerAdapter.STATION_TEXT)!=null)
			((TextView)v.findViewById(R.id.tour_explanation)).setText(b.getString(ToursPagerAdapter.STATION_TEXT));
		if(b.getStringArray(ToursPagerAdapter.PIC_LINKS)!=null){
			for(String link:b.getStringArray(ToursPagerAdapter.PIC_LINKS)){
				ImageView iv = new ImageView(getActivity());
				android.widget.LinearLayout.LayoutParams ivParams =   new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
	//			ivParams = Gravity.CENTER;
				ivParams.topMargin = 40;
				ivParams.weight=1;
				ivParams.setMargins(15, 15, 15, 10);
				iv.setLayoutParams(ivParams);
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				AQuery aq = new AQuery(getActivity(), v);
				int ivId = MainActivity.generateViewId();
				iv.setId(ivId);
				llContainer.addView(iv);
				aq.id(ivId).image(link);
				picLink = link;
				iv.setTag(link);
				iv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String link = (String)v.getTag();
						ImageDialog dialog = new ImageDialog(link);
						
						dialog.show(getFragmentManager(), "image");
						//dialog.getDialog().getWindow().setBackgroundDrawableResource(R.drawable.glass);
						
						
						
					}
				});
				
			}
		}/*
		if(tp.audioLink!=null){
			
		}
		if(tp.videoLink!=null){
			
		}*/
		
		return v;
	}
	
	
}}

