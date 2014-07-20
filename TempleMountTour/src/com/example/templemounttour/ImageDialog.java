package com.example.templemounttour;

import com.androidquery.AQuery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageDialog extends android.support.v4.app.DialogFragment {
	String source;
	
	public ImageDialog(String link){
		super();
		source = link;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //R.style.CustomDialog
        ImageView iv = new ImageView(getActivity());
        LinearLayout ivLayout = new LinearLayout(getActivity());
        android.widget.LinearLayout.LayoutParams llParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
        android.widget.LinearLayout.LayoutParams ivParams = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
        ivParams.weight=1f;
        iv.setLayoutParams(ivParams);
        ivLayout.setLayoutParams(llParams);
        ivLayout.setBackgroundColor(Color.TRANSPARENT);
        ivLayout.addView(iv);
        AQuery aq = new AQuery(getActivity());
        aq.id(iv).image(source);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        builder.setView(ivLayout); 
   /*     builder.setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_image_dialog, null));
        ImageView iv = (ImageView) getActivity().findViewById(R.id.image_dialog);
        AQuery aq = new AQuery(getActivity());
        aq.id(R.id.image_dialog).image(source);*/
        ivLayout.setBackground(getResources().getDrawable(R.drawable.glass));
        Log.d("Image source", source);
        
        return builder.create();
	}
}
