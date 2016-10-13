package com.ob.Obrowser;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class ListDBHelper extends SQLiteOpenHelper {
	private static final int DB_ver=1;
	private static final String DB_NAME="list_db.db";
	private static final String TABLE_NAME="listtable";	
	private static String KEY_WORD="title ";
	private Cursor cursor;
	public ListDBHelper(Context context)
	{
        super(context,DB_NAME, null, DB_ver);
    }	
	@Override
	public void onCreate(SQLiteDatabase db) {
	Log.v("oncreate","in db");	
			
	        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
	                KEY_WORD + " TEXT);");	
	        Log.v("oncreate","out db");	
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); 
        // Create tables again
        onCreate(db);		
	}
	public void additem(String _list)
	{					
		SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues values = new ContentValues();
		values.put(KEY_WORD,_list);
		db.insert(TABLE_NAME, null, values);
		Log.v("data ","inserted in datab");		
		db.close();		
	}
	public List<String> rlink() 
	{		
		 List<String> a=new ArrayList<String>();
		 SQLiteDatabase db = this.getWritableDatabase();
		 String q= "select * from listtable ";
		 cursor =db.rawQuery(q, null);
		// looping through all rows and adding to list
		 if (cursor.moveToFirst()) 
		 {
	        do {
	             a.add(cursor.getString(0));
	        } while (cursor.moveToNext());
	    }
		db.close();		
		return a;
	}
	public void delete(String newtitle)
	{
		SQLiteDatabase db = this.getWritableDatabase();
//		String q="delete from listtable where title like "+"'"+newtitle+"%"+"'";
//		Cursor cursor=db.rawQuery(q, null);	
		db.delete(TABLE_NAME,KEY_WORD+" like '"+newtitle+"%'",null);		
		db.close();
	}
}