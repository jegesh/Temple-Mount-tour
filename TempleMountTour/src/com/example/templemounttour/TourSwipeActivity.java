package com.example.templemounttour;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

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
    	tourPagerAdapter = new ToursPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
       	mViewPager.setAdapter(tourPagerAdapter);
       	
       	if(firstTab = true){
       		mViewPager.setCurrentItem(this.getIntent().getIntExtra(StationMenuActivity.TOUR_INDEX, 0));
       		firstTab = false;
       	}
    	super.onStart();
    	
    }
    
    public void playVideo(View v){
		Log.d("Activity video", "trying to play: ");
		String vidLink = (String)v.getTag();
		Intent intent = new Intent( Intent.ACTION_VIEW);
		try {
			intent.setData(Uri.parse(vidLink));
		} catch (Exception e) {
			Toast.makeText(this, R.string.error+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(intent);
	    } 
		else
			Toast.makeText(this, R.string.no_video_error, Toast.LENGTH_SHORT).show();
	}

public class ToursPagerAdapter extends FragmentStatePagerAdapter {
    public static final String PIC_LINKS = "pic links";
	public static final String STATION_TEXT = "station text";
	public static final String TOUR_NAME = "tour name";
	public static final String AUDIO_LINK = "audio link";
	public static final String VIDEO_LINK = "video link";

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
        args.putString(AUDIO_LINK, tour.audioLink);
        args.putString(VIDEO_LINK,tour.videoLink);
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
	MediaPlayer mPlayer;
	String videoLink;
	
	public TourSwipeFragment(){
		
	}
	
		
	@Override
	public void onResume() {
		Bundle b = getArguments();
		LinearLayout llContainer = (LinearLayout)getView().findViewById(R.id.tour_pic_container);
		if(b.getString(ToursPagerAdapter.STATION_TEXT)!=null)
			((TextView)getView().findViewById(R.id.tour_explanation)).setText(b.getString(ToursPagerAdapter.STATION_TEXT));
		if(b.getStringArray(ToursPagerAdapter.PIC_LINKS)!=null){
			for(String link:b.getStringArray(ToursPagerAdapter.PIC_LINKS)){
				if(link.length()>10){
					ImageView iv = new ImageView(getActivity());
					android.widget.LinearLayout.LayoutParams ivParams =   new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
					//			ivParams = Gravity.CENTER;
					ivParams.topMargin = 40;
					ivParams.weight=1;
					ivParams.setMargins(15, 15, 15, 10);
					iv.setLayoutParams(ivParams);
					iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
					AQuery aq = new AQuery(getActivity(), getView());
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
							getFragmentManager().executePendingTransactions();

						}
					});

				}
			}
		}
		if(b.getString(ToursPagerAdapter.AUDIO_LINK)!=null && b.getString(ToursPagerAdapter.AUDIO_LINK).length()>10){
			WebView audioPlayer = (WebView)getView().findViewById(R.id.audio_webview);
			audioPlayer.setVisibility(View.VISIBLE);
			audioPlayer.setInitialScale(300);
			
			audioPlayer.loadUrl(b.getString(ToursPagerAdapter.AUDIO_LINK));
		}
				
		if(b.getString(ToursPagerAdapter.VIDEO_LINK)!=null && b.getString(ToursPagerAdapter.VIDEO_LINK).length()>10){
			TextView vidLink = (TextView)getView().findViewById(R.id.video_link);
			vidLink.setVisibility(View.VISIBLE);
			SpannableString underlinedText = new SpannableString(getString(R.string.video_link));
			underlinedText.setSpan(new UnderlineSpan(), 0, underlinedText.length(), 0);
			vidLink.setText(underlinedText);
			videoLink = b.getString(ToursPagerAdapter.VIDEO_LINK);
			vidLink.setTag(videoLink);
		}
		
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_tour, container, false);
		Bundle b = getArguments();
		TextView titleTxt = (TextView)v.findViewById(R.id.tour_title);
		titleTxt.setText(b.getString(ToursPagerAdapter.TOUR_NAME, getString(R.string.title_activity_tour_main_menu)));
		
		return v;
	}
	
		
	public void playVideo(View v){
		Log.d("Fragment video", "trying to play: "+ videoLink);
		Intent intent = new Intent( Intent.ACTION_VIEW);
		intent.setData(Uri.parse(videoLink));
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivity(intent);
	    }
	}
	
		
}}

