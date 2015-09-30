package com.example.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactDBOpenHelper extends SQLiteOpenHelper{
	
	private static final String table_name = "contact";
	private static final String column_id = "ID";
	private static final String column_name ="NAME";
	private static final String column_phone ="PHONENUMBER";
	private static final String column_mail = "EMAIL";
	
	private static final String create_table = "create table "+table_name+"("
			+column_id +" integer primary key autoincrement,"
			+column_name+" string,"
			+column_phone+" string,"
			+column_mail+" string"+ ")";
	
	private static final String drop_table = "drop table "+table_name;

	public ContactDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		//Call the constructor of super class : SQLiteOpenHelper
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Creation of the Database
		db.execSQL(create_table);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Update : drop & recreate table
		db.execSQL(drop_table);
		db.execSQL(create_table);
		
	}
	


}
