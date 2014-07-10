package com.example.templemounttour;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class PrepVerifyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prep_verify);

	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.legal_info) {
			DialogFragment legalDialog = new LegalitiesDialog();
			legalDialog.show(getFragmentManager(), "legal stuff");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startTour(View v){
		XMLLayoutParser parser = new XMLLayoutParser(this);
		int[] checkboxes=null;
		try {
			checkboxes = parser.getElementIds(R.layout.activity_prep_verify, XMLLayoutParser.CHECKBOX);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean allChecked = true;
		for(int id:checkboxes){
			CheckBox cbox = (CheckBox)findViewById(id);
			if(!cbox.isChecked())
				allChecked = false;
		}
		if(allChecked){
			Intent intent = new Intent(this,TouringMapActivity.class);
			startActivity(intent);
		}else{
			TextView message = (TextView)findViewById(R.id.message_unprepared);
			message.setVisibility(View.VISIBLE);
		}
	}
	
	public void getHelp(View v){
		// open website with explanations of preparations
	}

}
