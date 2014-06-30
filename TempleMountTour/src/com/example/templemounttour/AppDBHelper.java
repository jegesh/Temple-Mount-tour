package com.example.templemounttour;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "holy_mount.db";
	private static final int DB_VERSION = 3;
	public SQLiteDatabase db;
	public Context activity;
	private static String path;
	
	public AppDBHelper(Context ctx){
		super(ctx, DB_NAME, null, DB_VERSION);
		this.activity = ctx;
		path = ctx.getApplicationInfo().dataDir + "/databases/";
	}

	public AppDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		
	}
	
	public void createDataBase() throws IOException{
		 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    	//	Log.d("duhhh..", "Error: database already here!");
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
	
	private boolean checkDataBase(){
		 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = path + DB_NAME;
    		Log.d("path", path);
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(Exception e){
 
    		//database does't exist yet.
    		e.printStackTrace();
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
	
	 private void copyDataBase() throws IOException{
		 	Log.d("Copying db", "roger that");
	    	//Open your local db as the input stream
	    	InputStream myInput = activity.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = path + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	Log.d("Done", "copying");
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 
	 public void openDataBase() throws SQLException{
		 
	    	//Open the database
	        String myPath = path + DB_NAME;
	    	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	 
	    }
	 

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
