package com.ob.Obrowser;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class datab extends SQLiteOpenHelper {
	private static final int DB_ver=1;
	private static final String DB_NAME="url_db.db";
	private static final String TABLE_NAME="urltable";	
	private static String KEY_WORD="title ";
	private static String KEY_url="url ";
	private static String KEY_DEFINITION="data ";	
	private Cursor cursor;
	public datab(Context context) 
	{
        super(context,DB_NAME, null, DB_ver);
    }	
	@Override
	public void onCreate(SQLiteDatabase db) {
	Log.v("oncreate","in db");	
	String downloaded_pages= "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_WORD + " TEXT, " +
            KEY_url + " TEXT, " +
            KEY_DEFINITION + " TEXT);";		
	        db.execSQL(downloaded_pages);	
	        Log.v("oncreate","out db");	
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); 
        // Create tables again
        onCreate(db);		
	}
	public void addlink(String _title,String _url,String _res)
	{					
		SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues values = new ContentValues();
		values.put(KEY_WORD,_title);
		values.put(KEY_url, _url);
		values.put(KEY_DEFINITION, _res);
		db.insert(TABLE_NAME, null, values);
		Log.v("data ","inserted in datab");
		db.close();		
	}
	public String rlink(String title1) {		
		SQLiteDatabase db = this.getWritableDatabase();
		/*Cursor cursor = db.query(TABLE_NAME, new String[] {"title", "data"}, 
         "title like " + title1, null, null, null, null);*/
		String q= "select * from urltable where title="+"'"+title1+"'";
		cursor =db.rawQuery(q, null);
		cursor.moveToFirst();
		String res1 = cursor.getString(2);		
		Log.v("cursor value", res1);
		db.close();		
		return res1;
	}
	public String getBaseUrl(String title1){
		SQLiteDatabase db = this.getWritableDatabase();
		/*Cursor cursor = db.query(TABLE_NAME, new String[] {"title", "data"}, 
         "title like " + title1, null, null, null, null);*/
		String q= "select * from urltable where title="+"'"+title1+"'";
		cursor =db.rawQuery(q, null);
		cursor.moveToFirst();
		String res3= cursor.getString(1);		
		Log.v("cursor value", res3);
		db.close();		
		return res3;
	}
	public String rlinkbyurl(String url) {		
		SQLiteDatabase db = this.getWritableDatabase();		
		Log.v("url in datab is",url);
		String q= "select * from urltable where url like "+"'%"+url+"%'";
		cursor =db.rawQuery(q, null);	
		if(cursor.moveToFirst())
			{	
				String res2 = cursor.getString(2);		
				Log.v("cursor value", res2);
				return res2;		
			}	
		else
			return "ERROR";
	}
	public void delete(String newtitle)
	{
		SQLiteDatabase db = this.getWritableDatabase();
//		String q="delete from urltable where title like "+"'"+newtitle+"%"+"'";
//		Cursor cursor=db.rawQuery(q, null);		
		db.delete(TABLE_NAME,KEY_WORD+" like '"+newtitle+"%'",null);		
		db.close();
	}
}